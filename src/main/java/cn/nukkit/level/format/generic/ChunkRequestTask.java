package cn.nukkit.level.format.generic;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.GlobalBlockPaletteInterface.StaticVersion;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.util.BitArrayVersion;
import cn.nukkit.level.util.PalettedSubChunkStorage;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.LevelChunkPacket;
import cn.nukkit.network.protocol.SubChunkPacket;
import cn.nukkit.network.protocol.SubChunkPacket11810;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Hash;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static cn.nukkit.SharedConstants.*;
import static cn.nukkit.level.format.leveldb.LevelDbConstants.*;

//TODO: 接入 GameVersion 按需准备数据
@Log4j2
public class ChunkRequestTask extends AsyncTask<Void> {
    private static final byte[] EMPTY = new byte[0];

    public static final int PADDING_SUB_CHUNK_COUNT = 4;
    public static final byte[] PADDING_SUB_CHUNK_BLOB = new byte[]{
            8, // sub chunk version
            0, // no layers - client will treat this as all-air
    };
    public static final long PADDING_SUB_CHUNK_HASH = Hash.xxh64(PADDING_SUB_CHUNK_BLOB);

    public static final int FULL_COLUMN_NORMAL = 16;
    public static final int FULL_COLUMN_HALF = FULL_COLUMN_NORMAL / 2; // nether and old world

    public static final long MINIMIZE_BIOME_PALETTES_HASH;
    public static final byte[] MINIMIZE_BIOME_PALETTES;
    public static final byte[] MINIMIZE_CHUNK_DATA_NO_CACHE; // biomePalettes + borderBlocks
    public static final byte[] MINIMIZE_CHUNK_DATA_OLD;

    static {
        BinaryStream stream = new BinaryStream();
        PalettedSubChunkStorage emptyBiomeStorage = PalettedSubChunkStorage.ofBiome(BitArrayVersion.EMPTY, EnumBiome.OCEAN.id);
        emptyBiomeStorage.writeTo(stream);
        byte[] emptyPalette = stream.getBuffer();

        stream.reuse();
        PalettedSubChunkStorage singletonBiomeStorage = PalettedSubChunkStorage.ofBiome(BitArrayVersion.V0, EnumBiome.OCEAN.id);
        singletonBiomeStorage.writeTo(stream);

        for (int i = 0; i < 4 + 16 + 4; i++) {
            stream.put(emptyPalette);
        }
        MINIMIZE_BIOME_PALETTES = stream.getBuffer();
        MINIMIZE_BIOME_PALETTES_HASH = Hash.xxh64(MINIMIZE_BIOME_PALETTES);

        stream.putVarInt(0); // borderBlocks
        MINIMIZE_CHUNK_DATA_NO_CACHE = stream.getBuffer();

        stream.reuse();
        stream.put(new byte[16 * 16]); // biome
        stream.putVarInt(0); // borderBlocks
        MINIMIZE_CHUNK_DATA_OLD = stream.getBuffer();
    }

    Chunk chunk;
    int x;
    int z;
    Level level;

    boolean success;

    long timestamp;
    int count;

    ChunkBlobCache chunkBlobCache;

    final Map<StaticVersion, byte[]> payloads = new EnumMap<>(StaticVersion.class);
    final Map<StaticVersion, byte[][]> subChunkPayloads = new EnumMap<>(StaticVersion.class);
    byte[] subModePayloadNew; // 1.18.30+
    byte[] subModePayload;
    byte[] payload;
    byte[] payloadOld;
    ChunkPacketCache chunkPacketCache;
    byte[] heightmapType;
    byte[][] heightmapData;
    boolean[] emptySection;

    public ChunkRequestTask(Chunk chunk) {
        this.chunk = chunk;
        x = chunk.getX();
        z = chunk.getZ();
        level = chunk.getProvider().getLevel();
        timestamp = chunk.getChanges();
    }

    @Override
    public void onRun() {
        try {
            BinaryStream stream = new BinaryStream(8192);

            stream.reuse();
            byte[] biomePalette;
            PalettedSubChunkStorage storage = PalettedSubChunkStorage.ofBiome(toValidBiome(chunk.getBiomeId(0, 0)));
            for (int x = 0; x < 16; x++) {
                int xi = x << 8;
                for (int z = 0; z < 16; z++) {
                    int xzi = xi | (z << 4);
                    int biomeId = toValidBiome(chunk.getBiomeId(x, z));
                    for (int y = 0; y < 16; y++) {
                        storage.set(xzi | y, biomeId);
                    }
                }
            }
            storage.writeTo(stream);
            biomePalette = stream.getBuffer();
            // right now we don't support 3D natively, so we just 3Dify our 2D biomes so they fill the column
            for (int i = 0; i < 23; i++) {
                stream.put(biomePalette);
            }
            byte[] biomePalettesNew = stream.getBuffer(); // 1.18.30+
            byte[] biomePalettes =  Arrays.copyOf(biomePalettesNew, biomePalettesNew.length + biomePalette.length);
            System.arraycopy(biomePalette, 0, biomePalettes, biomePalettesNew.length, biomePalette.length);

            ChunkSection[] sections = chunk.getSections();
            for (int i = sections.length - 1; i >= 0; i--) {
                if (!sections[i].isEmpty()) {
                    count = i + 1;
                    break;
                }
            }
            boolean emptyChunk = count == 0;
            int extendedCount = emptyChunk ? 0 : PADDING_SUB_CHUNK_COUNT + count;
            emptySection = new boolean[count];

            byte[][] subChunkBlockEntities = new byte[PADDING_SUB_CHUNK_COUNT + 16][];
            byte[] fullChunkBlockEntities = EMPTY;
            Arrays.fill(subChunkBlockEntities, EMPTY);
            if (!emptyChunk && !chunk.getBlockEntities().isEmpty()) {
                stream.reuse();
                List<CompoundTag>[] tagLists = new List[count];

                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (blockEntity instanceof BlockEntitySpawnable) {
                        int subChunkY = blockEntity.getSubChunkY();
                        if (subChunkY >= tagLists.length) {
                            continue;
                        }
                        if (subChunkY < 0) {
                            //TODO: unsupported
                            continue;
                        }
                        List<CompoundTag> tagList = tagLists[subChunkY];
                        if (tagList == null) {
                            tagList = new ObjectArrayList<>();
                            tagLists[subChunkY] = tagList;
                        }
                        CompoundTag nbt = ((BlockEntitySpawnable) blockEntity).getSpawnCompound();
                        if (nbt.isEmpty()) {
                            continue;
                        }
                        tagList.add(nbt);
                    }
                }

                for (int i = PADDING_SUB_CHUNK_COUNT; i < extendedCount; i++) {
                    List<CompoundTag> tagList = tagLists[i - PADDING_SUB_CHUNK_COUNT];
                    if (tagList == null) {
                        continue;
                    }

                    byte[] data;
                    try {
                        data = NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN, true);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    subChunkBlockEntities[i] = data;

                    stream.put(data);
                }

                fullChunkBlockEntities = stream.getBuffer();
            }

            short[] heightmap = chunk.getHeightmap();
            heightmapType = new byte[extendedCount];
            heightmapData = new byte[extendedCount][];

            for (int i = 0; i < heightmapData.length; i++) {
                int chunkY = i - PADDING_SUB_CHUNK_COUNT;
                int minY = chunkY << 4;
                int maxY = ((chunkY + 1) << 4) - 1;
                int tooLowCount = 0;
                int tooHighCount = 0;
                byte[] subChunkHeightmap = new byte[SUB_CHUNK_2D_SIZE];
                for (int j = 0; j < SUB_CHUNK_2D_SIZE; j++) {
                    int height = heightmap[j];
                    if (height == -1) {
                        height = -4 << 4;
                    }

                    if (height < minY) {
                        subChunkHeightmap[j] = -1;
                        ++tooLowCount;
                    } else if (height > maxY) {
                        subChunkHeightmap[j] = 0xf + 1;
                        ++tooHighCount;
                    } else {
                        subChunkHeightmap[j] = (byte) (height & 0xf);
                    }
                }
                if (tooLowCount == SUB_CHUNK_2D_SIZE) {
                    heightmapType[i] = SubChunkPacket.HEIGHT_MAP_TYPE_ALL_TOO_LOW;
                } else if (tooHighCount == SUB_CHUNK_2D_SIZE) {
                    heightmapType[i] = SubChunkPacket.HEIGHT_MAP_TYPE_ALL_TOO_HIGH;
                } else {
                    heightmapType[i] = SubChunkPacket.HEIGHT_MAP_TYPE_HAS_DATA;
                    heightmapData[i] = subChunkHeightmap;
                }
            }

            Long2ObjectMap<byte[]> clientBlobs = new Long2ObjectOpenHashMap<>(count + 1); // 16 subChunks + 1 biome
            Long2ObjectMap<byte[]> extendedClientBlobs = new Long2ObjectOpenHashMap<>(extendedCount + 1); // 4 + 16 subChunks + 1 biome
            Long2ObjectMap<byte[]> extendedClientBlobsNew = new Long2ObjectOpenHashMap<>(extendedCount + 1); // 1.18.30+

            LongList blobIds = new LongArrayList(count + 1);
            LongList extendedBlobIds = new LongArrayList(extendedCount + 1);
            LongList extendedBlobIdsNew = new LongArrayList(extendedCount + 1); // 1.18.30+

            if (!emptyChunk) {
                for (int i = 0; i < PADDING_SUB_CHUNK_COUNT; i++) {
                    extendedBlobIds.add(PADDING_SUB_CHUNK_HASH);
                    extendedBlobIdsNew.add(PADDING_SUB_CHUNK_HASH);
                }
                extendedClientBlobs.put(PADDING_SUB_CHUNK_HASH, PADDING_SUB_CHUNK_BLOB);
                extendedClientBlobsNew.put(PADDING_SUB_CHUNK_HASH, PADDING_SUB_CHUNK_BLOB);
            }

            for (int i = 0; i < count; i++) {
                stream.reuse();
                emptySection[i] = sections[i].writeToCache(stream, GlobalBlockPalette::getNameByBlockId);
                byte[] subChunk = stream.getBuffer();

                long hash = Hash.xxh64(subChunk);
                blobIds.add(hash);
                extendedBlobIds.add(hash);
                clientBlobs.put(hash, subChunk);
                extendedClientBlobs.put(hash, subChunk);
            }

            for (int i = 0; i < count; i++) {
                stream.reuse();
                sections[i].writeToCache(stream, GlobalBlockPalette::getNewNameByBlockId); // 1.18.30+
                byte[] subChunk = stream.getBuffer();

                long hash = Hash.xxh64(subChunk);
                extendedBlobIdsNew.add(hash);
                extendedClientBlobsNew.put(hash, subChunk);
            }

            byte[] biome = chunk.getBiomeIdArray();
            long hash = Hash.xxh64(biome);
            blobIds.add(hash);
            clientBlobs.put(hash, biome);

            hash = Hash.xxh64(biomePalettes);
            extendedBlobIds.add(hash);
            extendedClientBlobs.put(hash, biomePalettes);

            hash = Hash.xxh64(biomePalettesNew);
            extendedBlobIdsNew.add(hash);
            extendedClientBlobsNew.put(hash, biomePalettesNew);

            stream.reuse();
            stream.putByte((byte) 0);
            stream.put(fullChunkBlockEntities);
            byte[] clientBlobCachedPayload = stream.getBuffer(); // borderBlocks + blockEntities

            stream.reuse();
            stream.put(biomePalettes);
            stream.putByte((byte) 0);
            subModePayload = stream.getBuffer(); // biomePalettes + borderBlocks

            stream.reuse();
            stream.put(biomePalettesNew);
            stream.putByte((byte) 0);
            subModePayloadNew = stream.getBuffer();

            chunkBlobCache = new ChunkBlobCache(count, heightmapType, heightmapData, emptySection, blobIds.toLongArray(), extendedBlobIds.toLongArray(), extendedBlobIdsNew.toLongArray(), clientBlobs, extendedClientBlobs, extendedClientBlobsNew, clientBlobCachedPayload, subChunkBlockEntities);

            for (StaticVersion version : StaticVersion.getValues()) {
                if (ENABLE_BLOCK_STATE_PERSISTENCE && version.getProtocol() < StaticVersion.V1_17_40.getProtocol()) {
                    // drop support for unavailable versions
                    continue;
                }

                byte[][] blockStorages = new byte[extendedCount][];
                payloads.put(version, encodeChunk(chunk, sections, count, blockStorages, biomePalettesNew, biomePalettes, fullChunkBlockEntities, version));

                if (version.getProtocol() >= StaticVersion.V1_18.getProtocol()) {
                    if (!emptyChunk && fullChunkBlockEntities.length != 0) {
                        for (int y = PADDING_SUB_CHUNK_COUNT; y < extendedCount; y++) {
                            byte[] blockEntities = subChunkBlockEntities[y];
                            if (blockEntities.length == 0) {
                                continue;
                            }
                            byte[] blockStorage = blockStorages[y];

                            stream.reuse();
                            stream.put(blockStorage);
                            stream.put(blockEntities);
                            blockStorages[y] = stream.getBuffer(); // subChunkBlocks + subChunkBlockEntities
                        }
                    }

                    if (DUMP_NETWORK_SUB_CHUNK && version == StaticVersion.getValues()[StaticVersion.getValues().length - 1]) {
                        for (int y = 0; y < extendedCount; y++) {
                            try (OutputStream out = Files.newOutputStream(Paths.get(Nukkit.DATA_PATH).resolve("debug")
                                    .resolve("subchunk_" + x + "_" + y + "_" + z + ".blob"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                                out.write(blockStorages[y]);
                            } catch (Exception e) {
                                log.error("Failed to dump sub chunk network data", e);
                            }
                        }
                    }

                    this.subChunkPayloads.put(version, blockStorages);
                }
            }

            payload = encodeChunk(false, chunk, sections, count, fullChunkBlockEntities);
            payloadOld = encodeChunk(true, chunk, sections, count, fullChunkBlockEntities);

            if (level.isCacheChunks()) {
                Map<StaticVersion, BatchPacket> packets = new EnumMap<>(StaticVersion.class);
                Map<StaticVersion, BatchPacket[]> subPackets = new EnumMap<>(StaticVersion.class);
                Map<StaticVersion, SubChunkPacket[]> subPacketsUncompressed = new EnumMap<>(StaticVersion.class);

                payloads.forEach((version, payload) -> {
                    int actualCount = count;
                    if (version.getProtocol() >= StaticVersion.V1_18.getProtocol()) {
                        actualCount = extendedCount;

                        byte[][] subChunkData = subChunkPayloads.get(version);
                        BatchPacket[] compressed = new BatchPacket[extendedCount];
                        SubChunkPacket[] uncompressed = new SubChunkPacket[extendedCount];
                        for (int i = 0; i < extendedCount; i++) {
                            int y = i - PADDING_SUB_CHUNK_COUNT;
                            SubChunkPacket packet;
                            if (version.getProtocol() >= StaticVersion.V1_18_10.getProtocol()) {
                                packet = new SubChunkPacket11810();
                                if (ENABLE_EMPTY_SUB_CHUNK_NETWORK_OPTIMIZATION && (y < 0 || emptySection[y])) {
                                    packet.requestResult = SubChunkPacket.REQUEST_RESULT_SUCCESS_ALL_AIR;
                                }
                            } else {
                                packet = new SubChunkPacket();
                            }
                            compressed[i] = Level.getSubChunkCacheFromData(packet, Level.DIMENSION_OVERWORLD, x, y, z, subChunkData[i], heightmapType[i], heightmapData[i]);
                            packet.setBuffer(null, 0); // release buffer
                            uncompressed[i] = packet;
                        }
                        subPackets.put(version, compressed);
                        subPacketsUncompressed.put(version, uncompressed);
                    }
                    packets.put(version, Level.getChunkCacheFromData(x, z, actualCount, payload, false, true));
                });

                chunkPacketCache = new ChunkPacketCache(
                        packets,
                        subPackets,
                        subPacketsUncompressed,
                        Level.getChunkCacheFromData(x, z, LevelChunkPacket.CLIENT_REQUEST_FULL_COLUMN_FAKE_COUNT, subModePayloadNew, false, true),
                        Level.getChunkCacheFromData(x, z, LevelChunkPacket.CLIENT_REQUEST_FULL_COLUMN_FAKE_COUNT, subModePayload, false, true),
                        Level.getChunkCacheFromData(x, z, LevelChunkPacket.CLIENT_REQUEST_TRUNCATED_COLUMN_FAKE_COUNT, extendedCount, subModePayloadNew, false, true),
                        Level.getChunkCacheFromData(x, z, LevelChunkPacket.CLIENT_REQUEST_TRUNCATED_COLUMN_FAKE_COUNT, extendedCount, subModePayload, false, true),
                        Level.getChunkCacheFromData(x, z, count, payload, false, true),
                        Level.getChunkCacheFromData(x, z, count, payload, false, false),
                        Level.getChunkCacheFromData(x, z, count, payloadOld, true, false)
                );
            }
            success = true;
        } catch (Exception e) {
            log.warn("Chunk async load failed (network serialization)", e);
        }
    }

    @Override
    public void onCompletion(Server server) {
        if (!success) {
            return;
        }
        level.chunkRequestCallback(timestamp, x, z, count, chunkBlobCache, chunkPacketCache, payload, payloadOld, subModePayload, subModePayloadNew, payloads, subChunkPayloads, heightmapType, heightmapData, emptySection);
    }

    private static byte[] encodeChunk(boolean isOld, Chunk chunk, ChunkSection[] sections, int count, byte[] blockEntities) {
        BinaryStream stream = new BinaryStream();
        stream.reuse();
        if (isOld) {
            stream.putByte((byte) count);
        }
        for (int i = 0; i < count; i++) {
            sections[i].writeTo(stream);
        }
        if (isOld) {
            for (short height : chunk.getHeightmap()) {
                stream.putLShort(height);
            }
        }
        stream.put(chunk.getBiomeIdArray());
        stream.putByte((byte) 0);
        stream.put(blockEntities);

//        ByteBuf buf = Unpooled.wrappedBuffer(stream.getBuffer());
//        log.info(ByteBufUtil.prettyHexDump(buf), new Throwable("version=" + null + ", chunkX=" + chunk.getX() + ", chunkZ=" + chunk.getZ() + ", length=" + buf.readableBytes())));
//        buf.release();

        return stream.getBuffer();
    }

    private static byte[] encodeChunk(Chunk chunk, ChunkSection[] sections, int count, byte[][] blockStorages, byte[] biomePalettesNew, byte[] biomePalettes, byte[] blockEntities, StaticVersion version) {
        BinaryStream stream = new BinaryStream();
        stream.reuse();
        boolean extendedLevel = version.getProtocol() >= StaticVersion.V1_18.getProtocol();
        if (extendedLevel && blockStorages.length != 0) {
            //HACK: fill in fake sub chunks to make up for the new negative space client-side
            for (int i = 0; i < PADDING_SUB_CHUNK_COUNT; i++) {
                stream.put(PADDING_SUB_CHUNK_BLOB);
                blockStorages[i] = PADDING_SUB_CHUNK_BLOB;
            }
        }
        for (int i = 0; i < count; i++) {
            int mark = stream.getCount();
            sections[i].writeTo(stream, version);
            blockStorages[PADDING_SUB_CHUNK_COUNT + i] = stream.getBuffer(mark);
        }
        if (extendedLevel) {
            if (version.getProtocol() >= StaticVersion.V1_18_30.getProtocol()) {
                stream.put(biomePalettesNew);
            } else {
                stream.put(biomePalettes);
            }
        } else {
            stream.put(chunk.getBiomeIdArray());
        }
        stream.putByte((byte) 0);
        stream.put(blockEntities);

//        ByteBuf buf = Unpooled.wrappedBuffer(stream.getBuffer());
//        log.info(ByteBufUtil.prettyHexDump(buf), new Throwable("version=" + version + ", chunkX=" + chunk.getX() + ", chunkZ=" + chunk.getZ() + ", length=" + buf.readableBytes()));
//        buf.release();

        return stream.getBuffer();
    }

    private static int toValidBiome(int id) {
        String name = Biome.getNameById(id); //TODO: different version -- 07/10/2022
        // make sure we aren't sending bogus biomes - the 1.18.0 client crashes if we do this
        return name == null ? EnumBiome.OCEAN.id : id;
    }
}
