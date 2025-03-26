package cn.nukkit.level.format.generic;

import cn.nukkit.Server;
import cn.nukkit.block.Blocks;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.level.GlobalBlockPaletteInterface.StaticVersion;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.LevelChunkPacket;
import cn.nukkit.network.protocol.LevelChunkPacket12060;
import cn.nukkit.network.protocol.SubChunkPacket;
import cn.nukkit.network.protocol.SubChunkPacket11810;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Hash;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.nukkit.level.format.leveldb.LevelDbConstants.*;

@Log4j2
public class ChunkRequestTask extends AsyncTask<Void> {
    private static final EnumSet<StaticVersion> PRELOAD_VERSIONS = EnumSet.noneOf(StaticVersion.class);

    private static final byte[] EMPTY = new byte[0];

    private final long timestamp;
    private final Chunk chunk;
    private final int chunkX;
    private final int chunkZ;
    private final HeightRange heightRange;
    private final short[] heightmap;
    private final boolean[] borders;
    private final BlockEntity[] blockEntities;
    private final Level level;
    private final Set<StaticVersion> requestedVersions;

    private final Map<StaticVersion, byte[]> fullChunkPayloads = new EnumMap<>(StaticVersion.class);

    private byte[] subRequestModeFullChunkPayload;
    private final Map<StaticVersion, byte[][]> subChunkPayloads = new EnumMap<>(StaticVersion.class);

    private ChunkCachedData chunkCachedData;

    public ChunkRequestTask(Chunk chunk) {
        this.chunk = chunk;
        chunkX = chunk.getX();
        chunkZ = chunk.getZ();
        heightRange = chunk.getHeightRange();
        blockEntities = chunk.getBlockEntities().values().toArray(new BlockEntity[0]);
        heightmap = chunk.getHeightmap().clone();
        borders = chunk.getBorders().clone();
        LevelProvider provider = chunk.getProvider();
        level = provider.getLevel();
        this.requestedVersions = EnumSet.copyOf(PRELOAD_VERSIONS);
        this.requestedVersions.addAll(level.getRequestChunkVersions().keySet());
        timestamp = chunk.getChanges();
    }

    @Override
    public void onRun() {
        try {
            int minChunkY = heightRange.getMinChunkY();
            int maxChunkY = heightRange.getMaxChunkY();
            int chunkYIndexOffset = heightRange.getChunkYIndexOffset();
            int yIndexOffset = heightRange.getYIndexOffset();

            BinaryStream stream = new BinaryStream(1024);

            chunk.writeBiomeTo(stream, true);
            byte[] biome = stream.getBuffer();

            int cnt = 0;
            ChunkSection[] sections = chunk.getSections();
            int topChunkY = maxChunkY - 1;
            for (; topChunkY >= minChunkY; topChunkY--) {
                if (!sections[Level.subChunkYtoIndex(topChunkY)].isEmpty()) {
                    cnt = topChunkY - minChunkY + 1;
                    break;
                }
            }
            int count = cnt;

            boolean emptyChunk = count == 0;
            boolean[] emptySection = new boolean[count];

            byte[][] subChunkBlockEntities = new byte[count][];
            byte[] fullChunkBlockEntities = EMPTY;
            Arrays.fill(subChunkBlockEntities, EMPTY);
            if (!emptyChunk && blockEntities.length != 0) {
                stream.reuse();
                List<CompoundTag>[] tagLists = new List[count];

                for (BlockEntity blockEntity : blockEntities) {
                    if (blockEntity instanceof BlockEntitySpawnable) {
                        if (blockEntity.isClosed()) {
                            continue;
                        }
                        int subChunkY = blockEntity.getSubChunkY();
                        int subChunkIndex = Level.yToIndex(subChunkY, chunkYIndexOffset);
                        if (subChunkIndex >= tagLists.length) {
                            continue;
                        }
                        List<CompoundTag> tagList = tagLists[subChunkIndex];
                        if (tagList == null) {
                            tagList = new ObjectArrayList<>();
                            tagLists[subChunkIndex] = tagList;
                        }
                        CompoundTag nbt = ((BlockEntitySpawnable) blockEntity).getSpawnCompound();
                        if (nbt.isEmpty()) {
                            continue;
                        }
                        tagList.add(nbt);
                    }
                }

                for (int i = 0; i < count; i++) {
                    List<CompoundTag> tagList = tagLists[i];
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

            byte[] heightmapType = new byte[count];
            byte[][] heightmapData = new byte[count][];

            for (int chunkY = minChunkY; chunkY <= topChunkY; chunkY++) {
                int index = Level.yToIndex(chunkY, chunkYIndexOffset);
                int minY = chunkY << 4;
                int maxY = ((chunkY + 1) << 4) - 1;
                int tooLowCount = 0;
                int tooHighCount = 0;
                byte[] subChunkHeightmap = new byte[SUB_CHUNK_2D_SIZE];
                for (int i = 0; i < SUB_CHUNK_2D_SIZE; i++) {
                    int height = Level.indexToY(heightmap[i], yIndexOffset);
                    if (height < minY) {
                        subChunkHeightmap[i] = -1;
                        ++tooLowCount;
                    } else if (height > maxY) {
                        subChunkHeightmap[i] = 0xf + 1;
                        ++tooHighCount;
                    } else {
                        subChunkHeightmap[i] = (byte) (height & 0xf);
                    }
                }
                if (tooLowCount == SUB_CHUNK_2D_SIZE) {
                    heightmapType[index] = SubChunkPacket.HEIGHT_MAP_TYPE_ALL_TOO_LOW;
                } else if (tooHighCount == SUB_CHUNK_2D_SIZE) {
                    heightmapType[index] = SubChunkPacket.HEIGHT_MAP_TYPE_ALL_TOO_HIGH;
                } else {
                    heightmapType[index] = SubChunkPacket.HEIGHT_MAP_TYPE_HAS_DATA;
                    heightmapData[index] = subChunkHeightmap;
                }
            }

            ByteList borderBlockIndexes = new ByteArrayList();
            for (int i = 0; i < borders.length; i++) {
                if (borders[i]) {
                    borderBlockIndexes.add((byte) i);
                }
            }
            byte[] validBorderBlocks = borderBlockIndexes.size() != SUB_CHUNK_2D_SIZE ? borderBlockIndexes.toByteArray() : new byte[0]; // vanilla bug :(
            stream.reuse();
            stream.putByte(validBorderBlocks.length);
            stream.put(validBorderBlocks);
            byte[] borderBlocks = stream.getBuffer();

            int blobCount = count + 1; // N subChunks + 1 biome
            Long2ObjectMap<byte[]> blobs = new Long2ObjectOpenHashMap<>(blobCount);
            LongList blobIds = new LongArrayList(blobCount);

            for (int chunkY = minChunkY; chunkY <= topChunkY; chunkY++) {
                stream.reuse();
                emptySection[Level.yToIndex(chunkY, chunkYIndexOffset)] = sections[Level.subChunkYtoIndex(chunkY)]
                        .writeToCache(stream, Blocks::getBlockFullNameById);
                byte[] subChunk = stream.getBuffer();

                long hash = Hash.xxh64(subChunk);
                blobIds.add(hash);
                blobs.put(hash, subChunk);
            }

            long hash = Hash.xxh64(biome);
            blobIds.add(hash);
            blobs.put(hash, biome);

            stream.reuse();
            stream.put(borderBlocks);
            stream.put(fullChunkBlockEntities);
            byte[] cacheModeFullChunkPayload = stream.getBuffer(); // borderBlocks + blockEntities

            stream.reuse();
            stream.put(biome);
            stream.put(borderBlocks);
            subRequestModeFullChunkPayload = stream.getBuffer(); // biome + borderBlocks. (without cache)

            for (StaticVersion version : StaticVersion.getAvailableVersions()) {
                if (!requestedVersions.contains(version)) {
                    continue;
                }

                byte[][] blockStorages = new byte[count][];
                stream.reuse();
                for (int chunkY = minChunkY; chunkY <= topChunkY; chunkY++) {
                    int mark = stream.getCount();
                    sections[Level.subChunkYtoIndex(chunkY)].writeTo(stream, version);
                    blockStorages[Level.yToIndex(chunkY, chunkYIndexOffset)] = stream.getBuffer(mark);
                }
                stream.put(biome);
                stream.put(borderBlocks);
                stream.put(fullChunkBlockEntities);
                fullChunkPayloads.put(version, stream.getBuffer());

                if (!emptyChunk && fullChunkBlockEntities.length != 0) {
                    for (int i = 0; i < count; i++) {
                        byte[] blockEntities = subChunkBlockEntities[i];
                        if (blockEntities.length == 0) {
                            continue;
                        }
                        byte[] blockStorage = blockStorages[i];

                        stream.reuse();
                        stream.put(blockStorage);
                        stream.put(blockEntities);
                        blockStorages[i] = stream.getBuffer(); // subChunkBlocks + subChunkBlockEntities
                    }
                }

                this.subChunkPayloads.put(version, blockStorages);
            }

            ChunkPacketCache packetCache;
            if (level.isCacheChunks()) {
                Map<StaticVersion, BatchPacket> packets = new EnumMap<>(StaticVersion.class);
                Map<StaticVersion, LevelChunkPacket12060> packetsUncompressed = new EnumMap<>(StaticVersion.class);
                Map<StaticVersion, BatchPacket[]> subPackets = new EnumMap<>(StaticVersion.class);
                Map<StaticVersion, SubChunkPacket[]> subPacketsUncompressed = new EnumMap<>(StaticVersion.class);

                fullChunkPayloads.forEach((version, payload) -> {
                    byte[][] subChunkData = subChunkPayloads.get(version);
                    BatchPacket[] compressed = new BatchPacket[count];
                    SubChunkPacket[] uncompressed = new SubChunkPacket[count];
                    for (int i = 0; i < count; i++) {
                        SubChunkPacket packet = new SubChunkPacket11810();
                        if (emptySection[i]) {
                            packet.requestResult = SubChunkPacket.REQUEST_RESULT_SUCCESS_ALL_AIR;
                        }
                        compressed[i] = Level.getSubChunkCacheFromData(packet, Level.DIMENSION_OVERWORLD, chunkX, Level.indexToY(i, chunkYIndexOffset), chunkZ, subChunkData[i], heightmapType[i], heightmapData[i]);
                        packet.setBuffer(null, 0); // release buffer
                        uncompressed[i] = packet;
                    }
                    subPackets.put(version, compressed);
                    subPacketsUncompressed.put(version, uncompressed);

                    if (version.getProtocol() >= StaticVersion.V1_20_60.getProtocol()) {
                        LevelChunkPacket12060 packet = new LevelChunkPacket12060();
                        packet.chunkX = chunkX;
                        packet.chunkZ = chunkZ;
                        packet.dimension = level.getDimension().ordinal();
                        packet.subChunkCount = count;
                        packet.subChunkRequestLimit = 0;
                        packet.data = payload;
                        packet.setBuffer(null, 0);
                        packetsUncompressed.put(version, packet);
                    } else {
                        packets.put(version, Level.getChunkCacheFromData(chunkX, chunkZ, count, payload));
                    }
                });

                LevelChunkPacket12060 uncompressed = new LevelChunkPacket12060();
                uncompressed.chunkX = chunkX;
                uncompressed.chunkZ = chunkZ;
                uncompressed.dimension = level.getDimension().ordinal();
                uncompressed.subChunkCount = LevelChunkPacket.CLIENT_REQUEST_TRUNCATED_COLUMN_FAKE_COUNT;
                uncompressed.subChunkRequestLimit = count;
                uncompressed.data = subRequestModeFullChunkPayload;
                uncompressed.setBuffer(null, 0);

                packetCache = new ChunkPacketCache(
                        packets,
                        packetsUncompressed,
                        Level.getChunkCacheFromData(chunkX, chunkZ, LevelChunkPacket.CLIENT_REQUEST_TRUNCATED_COLUMN_FAKE_COUNT, count, subRequestModeFullChunkPayload),
                        uncompressed,
                        subPackets,
                        subPacketsUncompressed,
                        requestedVersions);
            } else {
                packetCache = null;
            }

            chunkCachedData = new ChunkCachedData(count, heightmapType, heightmapData, emptySection, new ChunkBlobCache(blobIds.toLongArray(), blobs, cacheModeFullChunkPayload, borderBlocks, subChunkBlockEntities), packetCache);
        } catch (Exception e) {
            log.warn("Chunk network serialization failed: [" + chunkX + "," + chunkZ + "] " + level.getFolderName(), e);
        }
    }

    @Override
    public void onCompletion(Server server) {
        if (chunkCachedData == null) {
            return;
        }
        level.chunkRequestCallback(timestamp, chunkX, chunkZ, chunkCachedData, fullChunkPayloads, subRequestModeFullChunkPayload, subChunkPayloads);
    }

    public static boolean addPreloadVersion(StaticVersion version) {
        return PRELOAD_VERSIONS.add(version);
    }

    public static Set<StaticVersion> getPreloadVersions() {
        return PRELOAD_VERSIONS;
    }
}
