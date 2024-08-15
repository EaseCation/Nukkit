package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockMultiface;
import cn.nukkit.block.BlockRedstoneDiode;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFullNames;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityPainting;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.event.level.*;
import cn.nukkit.event.level.PortalCreateEvent.CreateReason;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.weather.LightningStrikeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GlobalBlockPaletteInterface.StaticVersion;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.*;
import cn.nukkit.level.format.LevelProviderManager.LevelProviderHandle;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.ChunkCachedData;
import cn.nukkit.level.format.generic.ChunkPacketCache;
import cn.nukkit.level.format.leveldb.LevelDB;
import cn.nukkit.level.format.mcregion.McRegion;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.PopChunkManager;
import cn.nukkit.level.generator.task.GenerationTask;
import cn.nukkit.level.generator.task.LightPopulationTask;
import cn.nukkit.level.generator.task.PopulationTask;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.level.sound.BlockPlaceSound;
import cn.nukkit.level.sound.Sound;
import cn.nukkit.level.sound.SoundEnum;
import cn.nukkit.math.*;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.metadata.BlockMetadataStore;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.Network;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.BatchPacket.Track;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.BlockUpdateScheduler;
import cn.nukkit.utils.*;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * author: MagicDroidX Nukkit Project
 */
@Log4j2
public class Level implements ChunkManager, Metadatable {

    private static int levelIdCounter = 1;
    private static int chunkLoaderCounter = 1;

    public static final int BLOCK_UPDATE_NORMAL = 1;
    public static final int BLOCK_UPDATE_RANDOM = 2;
    public static final int BLOCK_UPDATE_SCHEDULED = 3;
    public static final int BLOCK_UPDATE_WEAK = 4;
    public static final int BLOCK_UPDATE_TOUCH = 5;
    public static final int BLOCK_UPDATE_REDSTONE = 6;
    public static final int BLOCK_UPDATE_TICK = 7;

    public static final int TIME_DAY = 0;
    public static final int TIME_NOON = 6000;
    public static final int TIME_SUNSET = 12000;
    public static final int TIME_NIGHT = 14000;
    public static final int TIME_MIDNIGHT = 18000;
    public static final int TIME_SUNRISE = 23000;

    public static final int TIME_FULL = 24000;

    public static final int DIMENSION_OVERWORLD = 0;
    public static final int DIMENSION_NETHER = 1;
    public static final int DIMENSION_THE_END = 2;

    // The blocks that can randomly tick
    private static final boolean[] randomTickBlocks = new boolean[Block.BLOCK_ID_COUNT];
    //TODO: move to Block class
    static {
        randomTickBlocks[Block.GRASS_BLOCK] = true;
        randomTickBlocks[Block.FARMLAND] = true;
        randomTickBlocks[Block.MYCELIUM] = true;
        randomTickBlocks[Block.SAPLING] = true;
        randomTickBlocks[Block.LEAVES] = true;
        randomTickBlocks[Block.LEAVES2] = true;
        randomTickBlocks[Block.SNOW_LAYER] = true;
        randomTickBlocks[Block.ICE] = true;
        randomTickBlocks[Block.FLOWING_LAVA] = true;
        randomTickBlocks[Block.LAVA] = true;
        randomTickBlocks[Block.CACTUS] = true;
        randomTickBlocks[Block.BLOCK_BEETROOT] = true;
        randomTickBlocks[Block.CARROTS] = true;
        randomTickBlocks[Block.POTATOES] = true;
        randomTickBlocks[Block.MELON_STEM] = true;
        randomTickBlocks[Block.PUMPKIN_STEM] = true;
        randomTickBlocks[Block.BLOCK_WHEAT] = true;
        randomTickBlocks[Block.BLOCK_REEDS] = true;
        randomTickBlocks[Block.RED_MUSHROOM] = true;
        randomTickBlocks[Block.BROWN_MUSHROOM] = true;
        randomTickBlocks[Block.BLOCK_NETHER_WART] = true;
        randomTickBlocks[Block.FIRE] = true;
        randomTickBlocks[Block.LIT_REDSTONE_ORE] = true;
        randomTickBlocks[Block.COCOA] = true;
        randomTickBlocks[Block.VINE] = true;
        randomTickBlocks[Block.BLOCK_KELP] = true;
        randomTickBlocks[Block.TURTLE_EGG] = true;
        randomTickBlocks[Block.BAMBOO] = true;
        randomTickBlocks[Block.BAMBOO_SAPLING] = true;
        randomTickBlocks[Block.SWEET_BERRY_BUSH] = true;
        randomTickBlocks[Block.CRIMSON_NYLIUM] = true;
        randomTickBlocks[Block.WARPED_NYLIUM] = true;
        randomTickBlocks[Block.WEEPING_VINES] = true;
        randomTickBlocks[Block.TWISTING_VINES] = true;
        randomTickBlocks[Block.POINTED_DRIPSTONE] = true;
        randomTickBlocks[Block.AZALEA_LEAVES] = true;
        randomTickBlocks[Block.AZALEA_LEAVES_FLOWERED] = true;
        randomTickBlocks[Block.BUDDING_AMETHYST] = true;
        randomTickBlocks[Block.COPPER_BLOCK] = true;
        randomTickBlocks[Block.EXPOSED_COPPER] = true;
        randomTickBlocks[Block.WEATHERED_COPPER] = true;
        randomTickBlocks[Block.CUT_COPPER] = true;
        randomTickBlocks[Block.EXPOSED_CUT_COPPER] = true;
        randomTickBlocks[Block.WEATHERED_CUT_COPPER] = true;
        randomTickBlocks[Block.CUT_COPPER_STAIRS] = true;
        randomTickBlocks[Block.EXPOSED_CUT_COPPER_STAIRS] = true;
        randomTickBlocks[Block.WEATHERED_CUT_COPPER_STAIRS] = true;
        randomTickBlocks[Block.CUT_COPPER_SLAB] = true;
        randomTickBlocks[Block.EXPOSED_CUT_COPPER_SLAB] = true;
        randomTickBlocks[Block.WEATHERED_CUT_COPPER_SLAB] = true;
        randomTickBlocks[Block.DOUBLE_CUT_COPPER_SLAB] = true;
        randomTickBlocks[Block.EXPOSED_DOUBLE_CUT_COPPER_SLAB] = true;
        randomTickBlocks[Block.WEATHERED_DOUBLE_CUT_COPPER_SLAB] = true;
        randomTickBlocks[Block.CAVE_VINES] = true;
        randomTickBlocks[Block.CAVE_VINES_BODY_WITH_BERRIES] = true;
        randomTickBlocks[Block.CAVE_VINES_HEAD_WITH_BERRIES] = true;
        randomTickBlocks[Block.MANGROVE_LEAVES] = true;
        randomTickBlocks[Block.MANGROVE_PROPAGULE] = true;
        randomTickBlocks[Block.CHORUS_FLOWER] = true;
        randomTickBlocks[Block.CHERRY_SAPLING] = true;
        randomTickBlocks[Block.CHERRY_LEAVES] = true;
        randomTickBlocks[Block.CHISELED_COPPER] = true;
        randomTickBlocks[Block.EXPOSED_CHISELED_COPPER] = true;
        randomTickBlocks[Block.WEATHERED_CHISELED_COPPER] = true;
        randomTickBlocks[Block.COPPER_GRATE] = true;
        randomTickBlocks[Block.EXPOSED_COPPER_GRATE] = true;
        randomTickBlocks[Block.WEATHERED_COPPER_GRATE] = true;
        randomTickBlocks[Block.COPPER_BULB] = true;
        randomTickBlocks[Block.EXPOSED_COPPER_BULB] = true;
        randomTickBlocks[Block.WEATHERED_COPPER_BULB] = true;
        randomTickBlocks[Block.COPPER_DOOR] = true;
        randomTickBlocks[Block.EXPOSED_COPPER_DOOR] = true;
        randomTickBlocks[Block.WEATHERED_COPPER_DOOR] = true;
        randomTickBlocks[Block.COPPER_TRAPDOOR] = true;
        randomTickBlocks[Block.EXPOSED_COPPER_TRAPDOOR] = true;
        randomTickBlocks[Block.WEATHERED_COPPER_TRAPDOOR] = true;
        randomTickBlocks[Block.SPRUCE_SAPLING] = true;
        randomTickBlocks[Block.BIRCH_SAPLING] = true;
        randomTickBlocks[Block.JUNGLE_SAPLING] = true;
        randomTickBlocks[Block.ACACIA_SAPLING] = true;
        randomTickBlocks[Block.DARK_OAK_SAPLING] = true;
        randomTickBlocks[Block.SPRUCE_LEAVES] = true;
        randomTickBlocks[Block.BIRCH_LEAVES] = true;
        randomTickBlocks[Block.JUNGLE_LEAVES] = true;
        randomTickBlocks[Block.DARK_OAK_LEAVES] = true;
    }

    /**
     * exclusive
     */
    public static final int MAX_Y = 512;
    /**
     * inclusive
     */
    public static final int MIN_Y = -512;
    /**
     * exclusive
     */
    public static final int MAX_CHUNK_Y = MAX_Y >> 4;
    /**
     * inclusive
     */
    public static final int MIN_CHUNK_Y = MIN_Y >> 4;

    public static final int MAX_SUB_CHUNK_COUNT = MAX_CHUNK_Y << 1;

    private final Long2ObjectMap<BlockEntity> blockEntities = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectMap<Player> players = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectMap<Entity> entities = new Long2ObjectOpenHashMap<>();

    public final Long2ObjectMap<Entity> updateEntities = new Long2ObjectOpenHashMap<>();

    private final Queue<BlockEntity> updateBlockEntities = new ConcurrentLinkedQueue<>();

    private final boolean cacheChunks;

    private final Server server;

    private final int levelId;

    private LevelProvider provider;
    private final HeightRange heightRange;

    private final Int2ObjectMap<ChunkLoader> loaders = new Int2ObjectOpenHashMap<>();

    private final Int2IntMap loaderCounter = new Int2IntOpenHashMap();

    private final Long2ObjectMap<Int2ObjectMap<ChunkLoader>> chunkLoaders = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectMap<Int2ObjectMap<Player>> playerLoaders = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectMap<List<DataPacket>> chunkPackets = new Long2ObjectOpenHashMap<>();

    private final Long2LongMap unloadQueue = new Long2LongOpenHashMap();

    private float time;
    public boolean stopTime;

    public float skyLightSubtracted;

    private final String folderName;

    private final Map<Long, BaseFullChunk> chunks = new ConcurrentHashMap<>(); //temporal solution for CME

    private static final int CHUNK_CACHE_SIZE = 4;
    private final long[] lastChunkPos = new long[CHUNK_CACHE_SIZE];
    private final BaseFullChunk[] lastChunk = new BaseFullChunk[CHUNK_CACHE_SIZE];

    private final Long2ObjectMap<IntSet> changedBlocks = new Long2ObjectOpenHashMap<>();
    private final Lock changeBlockLock = new ReentrantLock();

    private final BlockUpdateScheduler updateQueue;
    private final BlockUpdateScheduler randomUpdateQueue;

    private final Queue<Vector3> normalUpdateQueue = new ConcurrentLinkedDeque<>();
//    private final TreeSet<BlockUpdateEntry> updateQueue = new TreeSet<>();
//    private final List<BlockUpdateEntry> nextTickUpdates = Lists.newArrayList();
    //private final Map<BlockVector3, Integer> updateQueueIndex = new HashMap<>();

    private final Map<StaticVersion, LongSet> requestChunkVersions = new EnumMap<>(StaticVersion.class);

    private final Long2ObjectMap<Int2ObjectMap<Player>> subChunkSendQueue = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectMap<Int2ObjectMap<Player>> chunkSendQueue = new Long2ObjectOpenHashMap<>();
    private final LongSet chunkSendTasks = new LongOpenHashSet();

    private final Long2BooleanMap chunkPopulationQueue = new Long2BooleanOpenHashMap();
    private final Long2BooleanMap chunkPopulationLock = new Long2BooleanOpenHashMap();
    private final Long2BooleanMap chunkGenerationQueue = new Long2BooleanOpenHashMap();
    private final int chunkGenerationQueueSize;
    private final int chunkPopulationQueueSize;

    private boolean autoSave;

    private BlockMetadataStore blockMetadata;

    private final boolean useSections;

    private final Vector3 temporalVector;

    public int sleepTicks = 0;

    private final int chunkTickRadius;
    private final Long2IntMap chunkTickList = new Long2IntOpenHashMap();
    private final int chunksPerTicks;
    private boolean disableChunkTick;
    private final boolean clearChunksOnTick;

    private int updateLCG = ThreadLocalRandom.current().nextInt();

    private static final int LCG_CONSTANT = 1013904223;

    private int tickRate;
    public int tickRateTime = 0;
    public int tickRateCounter = 0;

    private final Class<? extends Generator> generatorClass;
    private final ThreadLocal<Generator> generators = new ThreadLocal<Generator>() {
        @Override
        public Generator initialValue() {
            try {
                Generator generator = generatorClass.getConstructor(Map.class).newInstance(provider.getGeneratorOptions());
                NukkitRandom rand = new NukkitRandom(getSeed());
                generator.init(new PopChunkManager(getSeed(), getHeightRange()), rand, provider.getWorldGeneratorOptions());
                return generator;
            } catch (Throwable e) {
                Server.getInstance().getLogger().logException(e);
                return null;
            }
        }
    };

    private boolean raining;
    private int rainTime;
    private int rainingIntensity;
    private boolean thundering;
    private int thunderTime;

    private long levelCurrentTick;

    private Dimension dimension;

    public GameRules gameRules;

    private boolean redstoneEnabled = true;
    private boolean extinguishFireIgnoreGameRule;
    private boolean newArmorMechanics = true;

    private boolean autoCompaction;

    private boolean initialized;

    private static final AtomicInteger callbackIdCounter = new AtomicInteger();
    private final Int2ObjectMap<Consumer<Block>> callbackBlockSet = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<BiConsumer<Long, DataPacket>> callbackChunkPacketSend = new Int2ObjectOpenHashMap<>();

    public Level(Server server, String name, String path, LevelProviderHandle providerHandle) {
        this.levelId = levelIdCounter++;
        this.blockMetadata = new BlockMetadataStore(this);
        this.server = server;
        this.autoSave = server.getAutoSave();
        this.autoCompaction = server.isAutoCompactionEnabled();
        this.folderName = name;
        this.updateQueue = new BlockUpdateScheduler(this, 0);
        this.randomUpdateQueue = new BlockUpdateScheduler(this, 0);
        Arrays.fill(lastChunkPos, ChunkPosition.INVALID_CHUNK_POSITION);

        Class<? extends LevelProvider> provider = providerHandle.getClazz();
        boolean convert = provider == McRegion.class; // McRegion to Anvil
        if (convert) {
            try {
                Path dir = Paths.get(path);
                Path dirBak = dir.getParent().resolve(dir.getName(dir.getNameCount() - 1) + ".bak");
                log.info("Backing up pre-conversion level to {}", dirBak);
                File file = dirBak.toFile();
                FileUtils.deleteDirectory(file);
                FileUtils.moveDirectory(dir.toFile(), file);
                this.provider = new McRegion(this, dirBak.toString());
            } catch (Exception e) {
                throw new LevelException(e);
            }

            log.info(this.server.getLanguage().translate("nukkit.level.updating",
                    TextFormat.GREEN + this.provider.getName() + TextFormat.RESET));
            LevelProvider old = this.provider;
            try {
                this.provider = new LevelProviderConverter(this, path)
                        .from(old)
                        .to(LevelProviderManager.ANVIL)
                        .perform();
            } catch (IOException e) {
                throw new LevelException(e);
            }
            old.close();
            provider = Anvil.class;
        }

        convert = provider == Anvil.class; // Anvil to LevelDB
        try {
            if (convert) {
                Path dir = Paths.get(path);
                Path dirBak = dir.getParent().resolve(dir.getName(dir.getNameCount() - 1) + ".bak");
                log.info("Backing up pre-conversion level to {}", dirBak);
                File file = dirBak.toFile();
                FileUtils.deleteDirectory(file);
                FileUtils.moveDirectory(dir.toFile(), file);
                this.provider = new Anvil(this, dirBak.toString());
            } else {
                this.provider = providerHandle.getInstantiator().create(this, path);
            }
        } catch (Exception e) {
            throw new LevelException(e);
        }

        if (convert) {
            log.info(this.server.getLanguage().translate("nukkit.level.updating",
                    TextFormat.GREEN + this.provider.getName() + TextFormat.RESET));
            LevelProvider old = this.provider;
            try {
                this.provider = new LevelProviderConverter(this, path)
                        .from(old)
                        .to(LevelProviderManager.LEVELDB)
                        .perform();
            } catch (IOException e) {
                throw new LevelException(e);
            }
            old.close();
            provider = LevelDB.class;
        }

        if (provider != providerHandle.getClazz()) {
            providerHandle = LevelProviderManager.getProviderByClass(provider);
        }

        this.heightRange = this.provider.getHeightRange();

        initialized = true;

        this.provider.updateLevelName(name);

        log.info(this.server.getLanguage().translate("nukkit.level.preparing",
                TextFormat.GREEN + this.provider.getName() + TextFormat.WHITE));

        this.generatorClass = this.provider.getGenerator();

        try {
            this.useSections = providerHandle.isUseSubChunk();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.time = this.provider.getTime();

        this.raining = this.provider.isRaining();
        this.rainTime = this.provider.getRainTime();
        Random random = ThreadLocalRandom.current();
        if (this.rainTime <= 0) {
            setRainTime(random.nextInt(168000) + 12000);
        }

        this.thundering = this.provider.isThundering();
        this.thunderTime = this.provider.getThunderTime();
        if (this.thunderTime <= 0) {
            setThunderTime(random.nextInt(168000) + 12000);
        }

        this.levelCurrentTick = this.provider.getCurrentTick();
        this.updateQueue.setLastTick(levelCurrentTick);
        this.randomUpdateQueue.setLastTick(levelCurrentTick);

        this.chunkTickRadius = Math.min(this.server.getViewDistance(),
                Math.max(1, this.server.getConfiguration().getChunkTickRadius()));
        this.chunksPerTicks = this.server.getConfiguration().getChunkTickingPerTick();
        this.chunkGenerationQueueSize = this.server.getConfiguration().getChunkGenerationQueueSize();
        this.chunkPopulationQueueSize = this.server.getConfiguration().getChunkPopulationQueueSize();
        this.chunkTickList.clear();
        this.clearChunksOnTick = this.server.getConfiguration().isClearChunksOnTick();
        this.cacheChunks = this.server.getConfiguration().isCacheChunks();
        this.temporalVector = new Vector3(0, 0, 0);
        this.tickRate = 1;

        this.skyLightSubtracted = this.calculateSkylightSubtracted(1);
    }

    /**
     * @param x chunk x
     * @param z chunk z
     * @return chunk position hash
     */
    public static long chunkHash(int x, int z) {
        return (((long) x) << 32) | (z & 0xffffffffL);
    }

    /**
     * @param x chunk local x
     * @param y world y
     * @param z chunk local z
     * @return full chunk local position hash
     */
    public static int localBlockHash(int x, int y, int z) {
        return ((y & 0x3ff) << 22) | ((x & 0xf) << 4) | (z & 0xf);
    }

    /**
     * @param triple full chunk local position hash
     * @return chunk local x
     */
    public static int localBlockX(int triple) {
        return (triple & 0xf0) >> 4;
    }

    /**
     * @param triple full chunk local position hash
     * @return world y
     */
    public static int localBlockY(int triple) {
        return triple >> 22;
    }

    /**
     * @param triple full chunk local position hash
     * @return chunk local z
     */
    public static int localBlockZ(int triple) {
        return triple & 0xf;
    }

    /**
     * @param chunkHash chunk position hash
     * @param blockHash full chunk local position hash
     * @return world position
     */
    public static Vector3 getBlockXYZ(long chunkHash, int blockHash) {
        return new Vector3(
                (getHashX(chunkHash) << 4) | localBlockX(blockHash),
                localBlockY(blockHash),
                (getHashZ(chunkHash) << 4) | localBlockZ(blockHash)
        );
    }

    /**
     * @param x world x
     * @param y world y
     * @param z world z
     * @return world position hash
     */
    public static long blockHash(int x, int y, int z) {
        return Hash.hashBlockPos(x, y, z);
    }

    /**
     * @param x chunk local x
     * @param y sub chunk local y
     * @param z chunk local z
     * @return sub chunk local position hash
     */
    public static int chunkBlockHash(int x, int y, int z) {
        return (x << 12) | (z << 8) | y;
    }

    /**
     * @param hash chunk position hash
     * @return chunk x
     */
    public static int getHashX(long hash) {
        return (int) (hash >> 32);
    }

    /**
     * @param hash chunk position hash
     * @return chunk z
     */
    public static int getHashZ(long hash) {
        return (int) hash;
    }

    /**
     * @param subChunkY sub chunk y
     * @return array index
     */
    public static int subChunkYtoIndex(int subChunkY) {
        return subChunkY & 0x3f;
    }

    /**
     * @param arrayIndex array index
     * @return sub chunk y
     */
    public static int subChunkIndexToY(int arrayIndex) {
        return arrayIndex << 26 >> 26;
    }

    /**
     * @param y sub chunk y or world y
     * @param offset array offset
     * @return array index
     */
    public static int yToIndex(int y, int offset) {
        return y + offset;
    }

    /**
     * @param arrayIndex array index
     * @param offset array offset
     * @return sub chunk y or world y
     */
    public static int indexToY(int arrayIndex, int offset) {
        return arrayIndex - offset;
    }

    public static int generateChunkLoaderId(ChunkLoader loader) {
        if (loader.getLoaderId() == null || loader.getLoaderId() == 0) {
            return chunkLoaderCounter++;
        } else {
            throw new IllegalStateException("ChunkLoader has a loader id already assigned: " + loader.getLoaderId());
        }
    }

    public int getTickRate() {
        return tickRate;
    }

    public int getTickRateTime() {
        return tickRateTime;
    }

    public void setTickRate(int tickRate) {
        this.tickRate = tickRate;
    }

    public void initLevel() {
        Generator generator = generators.get();
        this.dimension = Dimension.byIdOrDefault(generator.getDimension());
        this.gameRules = this.provider.getGamerules();
    }

    public Generator getGenerator() {
        return generators.get();
    }

    public BlockMetadataStore getBlockMetadata() {
        return this.blockMetadata;
    }

    public Server getServer() {
        return server;
    }

    final public LevelProvider getProvider() {
        return this.provider;
    }

    final public int getId() {
        return this.levelId;
    }

    public void close() {
        if (this.getAutoSave()) {
            this.save();
        }

        for (BaseFullChunk chunk : new ObjectArrayList<>(this.chunks.values())) {
            this.unloadChunk(chunk.getX(), chunk.getZ(), false);
        }
        Arrays.fill(lastChunkPos, ChunkPosition.INVALID_CHUNK_POSITION);
        Arrays.fill(lastChunk, null);

        this.provider.close();
        this.provider = null;
        this.blockMetadata = null;
        this.server.getLevels().remove(this.levelId);
    }

    public void addSound(Sound sound) {
        this.addSound(sound, (Player[]) null);
    }

    public void addSound(Sound sound, Player... players) {
        DataPacket[] packets = sound.encode();

        if (players == null) {
            if (packets != null) {
                for (DataPacket packet : packets) {
                    this.addChunkPacket(sound.getChunkX(), sound.getChunkZ(), packet);
                }
            }
        } else {
            if (packets != null) {
                if (packets.length == 1) {
                    Server.broadcastPacket(players, packets[0]);
                } else {
                    this.server.batchPackets(players, packets, false);
                }
            }
        }
    }

    public void addSound(Sound sound, Collection<Player> players) {
        this.addSound(sound, players.toArray(new Player[0]));
    }

    public void addSound(Vector3 pos, SoundEnum sound) {
        this.addSound(pos, sound, 1, 1, (Player[]) null);
    }

    public void addSound(Vector3 pos, SoundEnum sound, Player... players) {
        this.addSound(pos, sound, 1, 1, players);
    }

    public void addSound(Vector3 pos, SoundEnum sound, Collection<Player> players) {
        this.addSound(pos, sound, 1, 1, players);
    }

    public void addSound(Vector3 pos, SoundEnum sound, float volume, float pitch) {
        this.addSound(pos, sound, volume, pitch, (Player[]) null);
    }

    public void addSound(Vector3 pos, SoundEnum sound, float volume, float pitch, Collection<Player> players) {
        this.addSound(pos, sound, volume, pitch, players.toArray(new Player[0]));
    }

    public void addSound(Vector3 pos, SoundEnum sound, float volume, float pitch, Player... players) {
        this.addSound(pos, sound.getSound(), volume, pitch, players);
    }

    public void addSound(Vector3 pos, String sound) {
        this.addSound(pos, sound, 1, 1, (Player[]) null);
    }

    public void addSound(Vector3 pos, String sound, Collection<Player> players) {
        this.addSound(pos, sound, 1, 1, players);
    }

    public void addSound(Vector3 pos, String sound, Player... players) {
        this.addSound(pos, sound, 1, 1, players);
    }

    public void addSound(Vector3 pos, String sound, float volume, float pitch) {
        this.addSound(pos, sound, volume, pitch, (Player[]) null);
    }

    public void addSound(Vector3 pos, String sound, float volume, float pitch, Collection<Player> players) {
        this.addSound(pos, sound, volume, pitch, players.toArray(new Player[0]));
    }

    public void addSound(Vector3 pos, String sound, float volume, float pitch, Player... players) {
        volume = Mth.clamp(volume, 0, 1);
        pitch = Math.max(pitch, 0);

        PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = sound;
        packet.volume = volume;
        packet.pitch = pitch;
        packet.x = pos.getFloorX();
        packet.y = pos.getFloorY();
        packet.z = pos.getFloorZ();

        if (players == null || players.length == 0) {
            if (this.players.isEmpty()) {
                return;
            }

            addChunkPacket(pos.getChunkX(), pos.getChunkZ(), packet);
        } else {
            Server.broadcastPacket(players, packet);
        }
    }

    public void addLevelEvent(Vector3 pos, int event) {
        this.addLevelEvent(pos, event, 0);
    }

    public void addLevelEvent(Vector3 pos, int event, int data) {
        if (this.players.isEmpty()) {
            return;
        }

        LevelEventPacket pk = new LevelEventPacket();
        pk.evid = event;
        if (pos != null) {
            pk.x = (float) pos.x;
            pk.y = (float) pos.y;
            pk.z = (float) pos.z;
        }
        pk.data = data;

        if (pos == null) {
            Server.broadcastPacket(this.players.values(), pk);
        } else {
            this.addChunkPacket(pos.getChunkX(), pos.getChunkZ(), pk);
        }
    }

    public void addLevelEvent(int event, int data) {
        this.addLevelEvent(null, event, data);
    }

    public void addLevelEvent(int event, CompoundTag data) {
        this.addLevelEvent(null, event, data);
    }

    public void addLevelEvent(Vector3 pos, int event, CompoundTag data) {
        if (this.players.isEmpty()) {
            return;
        }

        LevelEventGenericPacket pk = new LevelEventGenericPacket();
        pk.eventId = event;
        pk.tag = data;

        if (pos == null) {
            Server.broadcastPacket(this.players.values(), pk);
        } else {
            this.addChunkPacket(pos.getChunkX(), pos.getChunkZ(), pk);
        }
    }

    public void addLevelSoundEvent(Vector3 pos, int type) {
        this.addLevelSoundEvent(pos, type, -1);
    }

    /**
     * Broadcasts sound to players
     *
     * @param pos  position where sound should be played
     * @param type ID of the sound from {@link cn.nukkit.network.protocol.LevelSoundEventPacket}
     * @param data generic data that can affect sound
     */
    public void addLevelSoundEvent(Vector3 pos, int type, int data) {
        this.addLevelSoundEvent(pos, type, data, ":", false, false);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, Player... viewers) {
        this.addLevelSoundEvent(pos, type, -1, ":", false, false, viewers);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, int data, Player... viewers) {
        this.addLevelSoundEvent(pos, type, data, ":", false, false, viewers);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, String identifier) {
        this.addLevelSoundEvent(pos, type, -1, identifier, false, false);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, String identifier, Player... viewers) {
        this.addLevelSoundEvent(pos, type, -1, identifier, false, false, viewers);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, String identifier, boolean isBaby) {
        this.addLevelSoundEvent(pos, type, -1, identifier, isBaby, false);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, String identifier, boolean isBaby, Player... viewers) {
        this.addLevelSoundEvent(pos, type, -1, identifier, isBaby, false, viewers);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, String identifier, boolean isBaby, boolean isGlobal) {
        this.addLevelSoundEvent(pos, type, -1, identifier, isBaby, isGlobal);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, String identifier, boolean isBaby, boolean isGlobal, Player... viewers) {
        this.addLevelSoundEvent(pos, type, -1, identifier, isBaby, isGlobal, viewers);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, int data, String identifier) {
        this.addLevelSoundEvent(pos, type, data, identifier, false, false);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, int data, String identifier, Player... viewers) {
        this.addLevelSoundEvent(pos, type, data, identifier, false, false, viewers);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, int data, String identifier, boolean isBaby) {
        this.addLevelSoundEvent(pos, type, data, identifier, isBaby, false);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, int data, String identifier, boolean isBaby, Player... viewers) {
        this.addLevelSoundEvent(pos, type, data, identifier, isBaby, false, viewers);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, int data, String identifier, boolean isBaby, boolean isGlobal) {
        this.addLevelSoundEvent(pos, type, data, identifier, isBaby, isGlobal, (Player[]) null);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, int data, String identifier, boolean isBaby, boolean isGlobal, Player... viewers) {
        LevelSoundEventPacket pk = new LevelSoundEventPacket();
        pk.sound = type;
        pk.extraData = data;
        pk.entityIdentifier = identifier;
        pk.x = (float) pos.x;
        pk.y = (float) pos.y;
        pk.z = (float) pos.z;
        pk.isGlobal = isGlobal;
        pk.isBabyMob = isBaby;

        if (viewers == null || viewers.length == 0) {
            this.addChunkPacket(pos.getChunkX(), pos.getChunkZ(), pk);
        } else {
            Server.broadcastPacket(viewers, pk);
        }
    }

    public void addParticle(Particle particle) {
        this.addParticle(particle, (Player[]) null);
    }

    public void addParticle(Particle particle, Player... players) {
        DataPacket[] packets = particle.encode();

        if (players == null || players.length == 0) {
            if (this.players.isEmpty()) {
                return;
            }

            if (packets != null) {
                for (DataPacket packet : packets) {
                    this.addChunkPacket(particle.getChunkX(), particle.getChunkZ(), packet);
                }
            }
        } else {
            if (packets != null) {
                if (packets.length == 1) {
                    Server.broadcastPacket(players, packets[0]);
                } else {
                    this.server.batchPackets(players, packets, false);
                }
            }
        }
    }

    public void addParticle(Particle particle, Collection<Player> players) {
        this.addParticle(particle, players.toArray(new Player[0]));
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect, Player... players) {
        this.addParticleEffect(pos, particleEffect, -1, null, players);
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect, long uniqueEntityId, Player... players) {
        this.addParticleEffect(pos, particleEffect, uniqueEntityId, null, players);
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect, @Nullable String molangVariables, Player... players) {
        this.addParticleEffect(pos, particleEffect, -1, molangVariables, players);
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect, long uniqueEntityId, @Nullable String molangVariables, Player... players) {
        this.addParticleEffect(pos, particleEffect.getIdentifier(), uniqueEntityId, molangVariables, players);
    }

    public void addParticleEffect(Vector3 pos, String identifier, Player... players) {
        this.addParticleEffect(pos, identifier, -1, null, players);
    }

    public void addParticleEffect(Vector3 pos, String identifier, long uniqueEntityId, Player... players) {
        this.addParticleEffect(pos, identifier, uniqueEntityId, null, players);
    }

    public void addParticleEffect(Vector3 pos, String identifier, @Nullable String molangVariables, Player... players) {
        this.addParticleEffect(pos, identifier, -1, molangVariables, players);
    }

    public void addParticleEffect(Vector3 pos, String identifier, long uniqueEntityId, @Nullable String molangVariables, Player... players) {
        if (players == null || players.length == 0) {
            players = this.getChunkPlayers(pos.getChunkX(), pos.getChunkZ()).values().toArray(new Player[0]);
        }

        Vector3f vec = pos.asVector3f();
        for (Player player : players) {
            player.spawnParticleEffect(vec, identifier, uniqueEntityId, molangVariables);
        }
    }

    public void broadcastEntityEvent(Entity entity, int event) {
        broadcastEntityEvent(entity, event, 0);
    }

    public void broadcastEntityEvent(Entity entity, int event, int data) {
        if (players.isEmpty()) {
            return;
        }

        EntityEventPacket packet = new EntityEventPacket();
        packet.eid = entity.getId();
        packet.event = event;
        packet.data = data;
        addChunkPacket(entity.getChunkX(), entity.getChunkZ(), packet);
    }

    public boolean getAutoSave() {
        return this.autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public void setSaveChunksOnUnload(boolean save) {
        provider.setSaveChunksOnClose(save);

        if (!save) {
            setAutoSave(false);
        }
    }

    public boolean unload() {
        return this.unload(false);
    }

    public boolean unload(boolean force) {
        LevelUnloadEvent ev = new LevelUnloadEvent(this);

        if (this == this.server.getDefaultLevel() && !force) {
            ev.setCancelled();
        }

        this.server.getPluginManager().callEvent(ev);

        if (!force && ev.isCancelled()) {
            return false;
        }

        log.info(this.server.getLanguage().translate("nukkit.level.unloading",
                TextFormat.GREEN + this.getName() + TextFormat.WHITE));
        Level defaultLevel = this.server.getDefaultLevel();

        for (Player player : new ObjectArrayList<>(this.getPlayers().values())) {
            if (this == defaultLevel || defaultLevel == null) {
                player.close(player.getLeaveMessage(), "Forced default level unload");
            } else {
                player.teleport(this.server.getDefaultLevel().getSafeSpawn());
            }
        }

        if (this == defaultLevel) {
            this.server.setDefaultLevel(null);
        }

        this.close();

        return true;
    }

    public Int2ObjectMap<Player> getChunkPlayers(int chunkX, int chunkZ) {
        long index = Level.chunkHash(chunkX, chunkZ);
        Int2ObjectMap<Player> loaders = this.playerLoaders.get(index);
        if (loaders != null) {
            return new Int2ObjectOpenHashMap<>(loaders);
        } else {
            return new Int2ObjectOpenHashMap<>();
        }
    }

    public ChunkLoader[] getChunkLoaders(int chunkX, int chunkZ) {
        long index = Level.chunkHash(chunkX, chunkZ);
        Int2ObjectMap<ChunkLoader> loaders = this.chunkLoaders.get(index);
        if (loaders != null) {
            return loaders.values().toArray(new ChunkLoader[0]);
        } else {
            return new ChunkLoader[0];
        }
    }

    public void addChunkPacket(int chunkX, int chunkZ, DataPacket packet) {
        long index = Level.chunkHash(chunkX, chunkZ);
        List<DataPacket> packets = this.chunkPackets.computeIfAbsent(index, key -> new ObjectArrayList<>());
        packets.add(packet);
        for (BiConsumer<Long, DataPacket> consumer : this.callbackChunkPacketSend.values()) {
            consumer.accept(index, packet);
        }
    }

    public void registerChunkLoader(ChunkLoader loader, int chunkX, int chunkZ) {
        this.registerChunkLoader(loader, chunkX, chunkZ, true);
    }

    public void registerChunkLoader(ChunkLoader loader, int chunkX, int chunkZ, boolean autoLoad) {
        int hash = loader.getLoaderId();
        long index = Level.chunkHash(chunkX, chunkZ);
        Int2ObjectMap<ChunkLoader> chunks = this.chunkLoaders.get(index);
        if (chunks == null) {
            chunks = new Int2ObjectOpenHashMap<>();
            chunks.put(hash, loader);
            this.chunkLoaders.put(index, chunks);

            Int2ObjectMap<Player> players = new Int2ObjectOpenHashMap<>();
            if (loader instanceof Player) {
                players.put(hash, (Player) loader);
            }
            this.playerLoaders.put(index, players);
        } else if (chunks.putIfAbsent(hash, loader) != null) {
            return;
        } else {
            if (loader instanceof Player) {
                this.playerLoaders.get(index).put(hash, (Player) loader);
            }
        }

        if (this.loaders.putIfAbsent(hash, loader) == null) {
            this.loaderCounter.put(hash, 1);
        } else {
            this.loaderCounter.put(hash, this.loaderCounter.get(hash) + 1);
        }

        this.cancelUnloadChunkRequest(chunkX, chunkZ);

        if (autoLoad) {
            this.loadChunk(chunkX, chunkZ);
        }
    }

    public void unregisterChunkLoader(ChunkLoader loader, int chunkX, int chunkZ) {
        int hash = loader.getLoaderId();
        long index = Level.chunkHash(chunkX, chunkZ);
        Int2ObjectMap<ChunkLoader> chunks = this.chunkLoaders.get(index);
        if (chunks == null) {
            return;
        }
        if (chunks.remove(hash) != null) {
            this.playerLoaders.get(index).remove(hash);
            if (chunks.isEmpty()) {
                this.chunkLoaders.remove(index);
                this.playerLoaders.remove(index);
                this.unloadChunkRequest(chunkX, chunkZ, true);
            }

            int count = this.loaderCounter.get(hash);
            if (--count == 0) {
                this.loaderCounter.remove(hash);
                this.loaders.remove(hash);
            } else {
                this.loaderCounter.put(hash, count);
            }
        }
    }

    public void checkTime() {
        if (!this.stopTime && this.gameRules.getBoolean(GameRule.DO_DAYLIGHT_CYCLE)) {
            this.time += tickRate;
        }
    }

    public void sendTime(Player... players) {
        if (players.length == 0) return;

        SetTimePacket pk = new SetTimePacket();
        pk.time = (int) this.time;

        Server.broadcastPacket(players, pk);
    }

    public void sendTime() {
        sendTime(this.players.values().toArray(new Player[0]));
    }

    public GameRules getGameRules() {
        return gameRules;
    }

    public void doTick(int currentTick) {
        updateBlockLight(lightQueue);
        this.checkTime();

        if (currentTick % 1200 == 0) { // Send time to client every 60 seconds to make sure it stay in sync
            this.sendTime();
        }

        // Tick Weather
        if (this.dimension != Dimension.NETHER && this.dimension != Dimension.END && gameRules.getBoolean(GameRule.DO_WEATHER_CYCLE)) {
            this.rainTime--;
            if (this.rainTime <= 0) {
                if (!this.setRaining(!this.raining)) {
                    if (this.raining) {
                        setRainTime(ThreadLocalRandom.current().nextInt(12000) + 12000);
                    } else {
                        setRainTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
                    }
                }
            }

            this.thunderTime--;
            if (this.thunderTime <= 0) {
                if (!this.setThundering(!this.thundering)) {
                    if (this.thundering) {
                        setThunderTime(ThreadLocalRandom.current().nextInt(12000) + 3600);
                    } else {
                        setThunderTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
                    }
                }
            }

            if (this.isThundering()) {
                Map<Long, ? extends FullChunk> chunks = getChunks();
                if (chunks instanceof Long2ObjectOpenHashMap) {
                    Long2ObjectOpenHashMap<? extends FullChunk> fastChunks = (Long2ObjectOpenHashMap) chunks;
                    Iterator<? extends Long2ObjectMap.Entry<? extends FullChunk>> iter = fastChunks.long2ObjectEntrySet().fastIterator();
                    while (iter.hasNext()) {
                        Long2ObjectMap.Entry<? extends FullChunk> entry = iter.next();
                        performThunder(entry.getLongKey(), entry.getValue());
                    }
                } else {
                    for (Map.Entry<Long, ? extends FullChunk> entry : getChunks().entrySet()) {
                        performThunder(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        this.skyLightSubtracted = this.calculateSkylightSubtracted(1);

        this.levelCurrentTick++;

        this.unloadChunks();

        this.updateQueue.tick(this.getCurrentTick());
        if (gameRules.getInteger(GameRule.RANDOM_TICK_SPEED) > 0) {
            randomUpdateQueue.tick(getCurrentTick());
        }

        Vector3 pos;
        while ((pos = this.normalUpdateQueue.poll()) != null) {
            Block block = getBlock(pos);
            BlockUpdateEvent event = new BlockUpdateEvent(block, 0);
            this.server.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                block.onUpdate(BLOCK_UPDATE_NORMAL);
            }

            block = getExtraBlock(block);
            if (!block.isAir()) {
                event = new BlockUpdateEvent(block, 1);
                server.getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    block.onUpdate(BLOCK_UPDATE_NORMAL);
                }
            }
        }

        if (!this.updateEntities.isEmpty()) {
            for (long id : new ObjectArrayList<>(this.updateEntities.keySet())) {
                Entity entity = this.updateEntities.get(id);
                if (entity == null) {
                    this.updateEntities.remove(id);
                    continue;
                }
                if (entity.closed || !entity.onUpdate(currentTick)) {
                    this.updateEntities.remove(id);
                }
            }
        }

        this.updateBlockEntities.removeIf(blockEntity -> !blockEntity.isValid() || !blockEntity.onUpdate());

        this.tickChunks();

        sendChangedBlocks();

        this.processChunkRequest();

        if (this.sleepTicks > 0 && --this.sleepTicks <= 0) {
            this.checkSleep();
        }

        Iterator<Long2ObjectMap.Entry<List<DataPacket>>> iterator = this.chunkPackets.long2ObjectEntrySet().iterator();
        while (iterator.hasNext()) {
            Long2ObjectMap.Entry<List<DataPacket>> entry = iterator.next();
            long index = entry.getLongKey();
            int chunkX = Level.getHashX(index);
            int chunkZ = Level.getHashZ(index);
            Collection<Player> chunkPlayers = this.getChunkPlayers(chunkX, chunkZ).values();
            if (!chunkPlayers.isEmpty()) {
                List<DataPacket> packets = entry.getValue();
                for (DataPacket pk : packets) {
                    Server.broadcastPacket(chunkPlayers, pk);
                }
            }
            iterator.remove();
        }

        if (gameRules.isStale()) {
            GameRulesChangedPacket packet = new GameRulesChangedPacket();
            packet.gameRules = gameRules;
            Server.broadcastPacket(players.values().toArray(new Player[0]), packet);
            gameRules.refresh();
        }
    }

    private void sendChangedBlocks() {
        changeBlockLock.lock();
        try {
            if (changedBlocks.isEmpty()) {
                return;
            }

            if (players.isEmpty()) {
                changedBlocks.clear();
                return;
            }

            Iterator<Long2ObjectMap.Entry<IntSet>> iter = changedBlocks.long2ObjectEntrySet().iterator();
            while (iter.hasNext()) {
                Long2ObjectMap.Entry<IntSet> entry = iter.next();
                long chunkIndex = entry.getLongKey();
                IntSet localPositions = entry.getValue();

                Vector3[] blocks = new Vector3[localPositions.size()];
                int i = 0;
                IntIterator iterator = localPositions.iterator();
                while (iterator.hasNext()) {
                    blocks[i++] = getBlockXYZ(chunkIndex, iterator.nextInt());
                }
                sendBlocks(getChunkPlayers(Level.getHashX(chunkIndex), Level.getHashZ(chunkIndex)).values().toArray(new Player[0]), blocks, UpdateBlockPacket.FLAG_ALL_PRIORITY);

                iter.remove();
            }
        } finally {
            changeBlockLock.unlock();
        }
    }

    private void performThunder(long index, FullChunk chunk) {
        if (ThreadLocalRandom.current().nextInt(10000) == 0 && areNeighboringChunksLoaded(index)) {
            int lcg = this.getUpdateLCG() >> 2;

            int chunkX = chunk.getX() * 16;
            int chunkZ = chunk.getZ() * 16;
            Vector3 vector = this.adjustPosToNearbyEntity(new Vector3(chunkX + (lcg & 0xf), 0, chunkZ + (lcg >>> 8 & 0xf)));

            Biome biome = Biome.getBiome(this.getBiomeId(vector.getFloorX(), vector.getFloorZ()));
            if (!biome.canRain()) {
                return;
            }

            int bId = this.getBlock(vector).getId();
            if (bId != Block.SHORT_GRASS && bId != Block.FLOWING_WATER) {
                vector.y += 1;
            }

            CompoundTag nbt = Entity.getDefaultNBT(vector);
            EntityLightning bolt = new EntityLightning(chunk, nbt);
            LightningStrikeEvent ev = new LightningStrikeEvent(this, bolt);
            getServer().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                bolt.spawnToAll();
            } else {
                bolt.setEffect(false);
            }

            this.addLevelSoundEvent(vector, LevelSoundEventPacket.SOUND_THUNDER, EntityFullNames.LIGHTNING_BOLT);
            this.addLevelSoundEvent(vector, LevelSoundEventPacket.SOUND_EXPLODE, EntityFullNames.LIGHTNING_BOLT);
        }
    }

    public Vector3 adjustPosToNearbyEntity(Vector3 pos) {
        pos.y = this.getHighestBlockAt(pos.getFloorX(), pos.getFloorZ());
        AxisAlignedBB axisalignedbb = new SimpleAxisAlignedBB(pos.x, pos.y, pos.z, pos.getX(), heightRange.getMaxY() - 1, pos.getZ()).expand(3, 3, 3);
        List<Entity> list = new ObjectArrayList<>();

        for (Entity entity : this.getCollidingEntities(axisalignedbb)) {
            if (entity.isAlive() && canBlockSeeSky(entity)) {
                list.add(entity);
            }
        }

        if (!list.isEmpty()) {
            return list.get(ThreadLocalRandom.current().nextInt(list.size())).getPosition();
        } else {
            if (pos.getY() == -1) {
                pos = pos.up(2);
            }

            return pos;
        }
    }

    public void checkSleep() {
        if (dimension != Dimension.OVERWORLD) {
            return;
        }
        if (this.players.isEmpty()) {
            return;
        }

        int playerCount = 0;
        int sleepingCount = 0;
        for (Player p : this.getPlayers().values()) {
            if (p.isSpectator()) {
                continue;
            }
            playerCount++;

            if (!p.isSleeping()) {
                continue;
            }
            sleepingCount++;
        }

        if (playerCount > 0 && (float) sleepingCount / playerCount * 100 >= gameRules.getInteger(GameRule.PLAYERS_SLEEPING_PERCENTAGE)) {
            if (this.gameRules.getBoolean(GameRule.DO_DAYLIGHT_CYCLE)) {
                int time = this.getTime() % Level.TIME_FULL;
                if ((time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE) || this.isThundering()) {
                    this.setTime(this.getTime() + Level.TIME_FULL - time);
                }

            }

            if (this.gameRules.getBoolean(GameRule.DO_WEATHER_CYCLE) && this.isThundering()) {
                this.setThundering(false);
                this.setRaining(false);
            }

            for (Player p : this.getPlayers().values()) {
                p.stopSleep();
            }
        }
    }

    public boolean isNight() {
        return time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE;
    }

    /*public void sendBlockExtraData(int x, int y, int z, int id, int data) {
        this.sendBlockExtraData(x, y, z, id, data, this.getChunkPlayers(x >> 4, z >> 4).values());
    }

    public void sendBlockExtraData(int x, int y, int z, int id, int data, Collection<Player> players) {
        sendBlockExtraData(x, y, z, id, data, players.toArray(new Player[0]));
    }

    public void sendBlockExtraData(int x, int y, int z, int id, int data, Player[] players) {
        LevelEventPacket pk = new LevelEventPacket();
        pk.evid = LevelEventPacket.EVENT_SET_DATA;
        pk.x = x + 0.5f;
        pk.y = y + 0.5f;
        pk.z = z + 0.5f;
        pk.data = (data << 8) | id;

        Server.broadcastPacket(players, pk);
    }*/

    public void sendBlocks(Player[] target, Vector3[] blocks) {
        this.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_NONE);
    }

    public void sendBlocks(Player[] target, Vector3[] blocks, int flags) {
        this.sendBlocks(target, blocks, flags, 0);
    }

    public void sendBlocks(Player[] target, Vector3[] blocks, int flags, int layer) {
        if (target == null || target.length == 0) {
            return;
        }

        for (Vector3 b : blocks) {
            if (b == null) {
                continue;
            }

            int x = (int) b.x;
            int y = (int) b.y;
            int z = (int) b.z;
            int id;
            int data;
            if (b instanceof Block block) {
                id = block.getId();
                data = block.getDamage();
            } else {
                int fullBlock = this.getFullBlock(layer, x, y, z);
                id = fullBlock >> Block.BLOCK_META_BITS;
                data = fullBlock & Block.BLOCK_META_MASK;
            }

            UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
            updateBlockPacket.x = x;
            updateBlockPacket.y = y;
            updateBlockPacket.z = z;
            updateBlockPacket.flags = flags;
            updateBlockPacket.blockId = id;
            updateBlockPacket.blockData = data;
            updateBlockPacket.dataLayer = layer;
            for (Player player : target) {
                player.dataPacket(updateBlockPacket);
            }
        }
    }

    private void tickChunks() {
        if (this.chunksPerTicks <= 0 || disableChunkTick || this.loaders.isEmpty()) {
            this.chunkTickList.clear();
            return;
        }

        int chunksPerLoader = Math.min(200, Math.max(1, (int) (((double) (this.chunksPerTicks - this.loaders.size()) / this.loaders.size() + 0.5))));
        int randRange = 3 + chunksPerLoader / 30;
        randRange = Math.min(randRange, this.chunkTickRadius);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (!this.loaders.isEmpty()) {
            for (ChunkLoader loader : this.loaders.values()) {
                int chunkX = (int) loader.getX() >> 4;
                int chunkZ = (int) loader.getZ() >> 4;

                long index = Level.chunkHash(chunkX, chunkZ);
                int existingLoaders = Math.max(0, this.chunkTickList.getOrDefault(index, 0));
                this.chunkTickList.put(index, existingLoaders + 1);
                for (int chunk = 0; chunk < chunksPerLoader; ++chunk) {
                    int dx = random.nextInt(2 * randRange) - randRange;
                    int dz = random.nextInt(2 * randRange) - randRange;
                    long hash = Level.chunkHash(dx + chunkX, dz + chunkZ);
                    if (!this.chunkTickList.containsKey(hash) && provider.isChunkLoaded(hash)) {
                        this.chunkTickList.put(hash, -1);
                    }
                }
            }
        }

        int blockTest = 0;

        if (!chunkTickList.isEmpty()) {
            boolean skipRandomTick = server.getTicksPerSecondRaw() < 19 || server.getTicksPerSecondAverage() < 19; // 高负载时跳过随机刻

            Iterator<Long2IntMap.Entry> iter = chunkTickList.long2IntEntrySet().iterator();
            while (iter.hasNext()) {
                Long2IntMap.Entry entry = iter.next();
                long index = entry.getLongKey();
                if (!areNeighboringChunksLoaded(index)) {
                    iter.remove();
                    continue;
                }

                int chunkX = getHashX(index);
                int chunkZ = getHashZ(index);

                FullChunk chunk = this.getChunk(chunkX, chunkZ, false);
                if (chunk == null) {
                    iter.remove();
                    continue;
                }

                int loaders = entry.getIntValue();
                if (loaders <= 0) {
                    iter.remove();
                }

                for (Entity entity : chunk.getEntities().values()) {
                    entity.scheduleUpdate();
                }

                if (skipRandomTick) {
                    continue;
                }

                int tickSpeed = gameRules.getInteger(GameRule.RANDOM_TICK_SPEED);
                if (tickSpeed > 0) {
                    //TODO: tickSnowAndIce
                    if (this.useSections) {
                        int highestNonAirSubChunkSeqIndex = -1;
                        ChunkSection[] sections = ((Chunk) chunk).getSections();
                        for (int chunkY = heightRange.getMaxChunkY() - 1; chunkY >= heightRange.getMinChunkY(); chunkY--) {
                            if (!sections[Level.subChunkYtoIndex(chunkY)].isEmpty()) {
                                highestNonAirSubChunkSeqIndex = heightRange.getChunkYIndexOffset() + chunkY;
                                break;
                            }
                        }
                        int subChunkCount = highestNonAirSubChunkSeqIndex + 1;
                        int tickCount = (int) (subChunkCount * 2.5f) * tickSpeed;
                        int maxHeight = subChunkCount << 4;
                        for (int i = 0; i < tickCount; i++) {
                            int lcg = this.getUpdateLCG() >> 2;
                            int x = lcg & 0x0f;
                            int y = ((lcg >>> 16) % maxHeight) - heightRange.getYIndexOffset();
                            int z = lcg >>> 8 & 0x0f;

                            int fullId = chunk.getFullBlock(0, x, y, z);
                            int blockId = fullId >> Block.BLOCK_META_BITS;
                            if (randomTickBlocks[blockId]) {
                                Block block = Block.get(fullId, this, (chunkX << 4) | x, y, (chunkZ << 4) | z);
                                block.onUpdate(BLOCK_UPDATE_RANDOM);
                            }
                        }
                    } else {
                        float tickCount = tickSpeed * 2.5f;
                        for (int chunkY = 0; chunkY < 8 && (chunkY < 3 || blockTest != 0); ++chunkY) {
                            blockTest = 0;
                            for (int i = 0; i < tickCount; ++i) {
                                int lcg = this.getUpdateLCG() >> 2;
                                int x = lcg & 0x0f;
                                int y = (lcg >>> 16 & 0x0f) | (chunkY << 4);
                                int z = lcg >>> 8 & 0x0f;

                                int fullId = chunk.getFullBlock(0, x, y, z);
                                int blockId = fullId >> Block.BLOCK_META_BITS;
                                blockTest |= fullId;
                                if (randomTickBlocks[blockId]) {
                                    Block block = Block.get(fullId, this, (chunkX << 4) | x, y, (chunkZ << 4) | z);
                                    block.onUpdate(BLOCK_UPDATE_RANDOM);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (this.clearChunksOnTick) {
            this.chunkTickList.clear();
        }
    }

    public boolean save() {
        return this.save(false);
    }

    public boolean save(boolean force) {
        if (!this.getAutoSave() && !force) {
            return false;
        }

        this.server.getPluginManager().callEvent(new LevelSaveEvent(this));

        this.provider.setTime((int) this.time);
        this.provider.setRaining(this.raining);
        this.provider.setRainTime(this.rainTime);
        this.provider.setThundering(this.thundering);
        this.provider.setThunderTime(this.thunderTime);
        this.provider.setCurrentTick(this.levelCurrentTick);
        this.provider.setGameRules(this.gameRules);
        this.saveChunks();
        this.provider.saveLevelData();

        return true;
    }

    public void saveChunks() {
        for (FullChunk chunk : new ObjectArrayList<>(this.chunks.values())) {
            if (chunk.hasChanged()) {
                try {
                    this.provider.setChunk(chunk.getX(), chunk.getZ(), chunk);
                    this.provider.saveChunk(chunk.getX(), chunk.getZ());

                    chunk.setChanged(false);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void updateAroundRedstone(Vector3 pos, BlockFace face) {
        for (BlockFace side : BlockFace.getValues()) {
            if (face != null && side == face) {
                continue;
            }

            this.getBlock(pos.getSideVec(side)).onUpdate(BLOCK_UPDATE_REDSTONE);
        }
    }

    public void updateComparatorOutputLevel(Vector3 v) {
        for (BlockFace face : Plane.HORIZONTAL) {
            Vector3 pos = v.getSideVec(face);

            if (this.isChunkLoaded((int) pos.x >> 4, (int) pos.z >> 4)) {
                Block block1 = this.getBlock(pos);

                if (BlockRedstoneDiode.isDiode(block1)) {
                    block1.onUpdate(BLOCK_UPDATE_REDSTONE);
                } else if (block1.isNormalBlock()) {
                    pos = pos.getSideVec(face);
                    block1 = this.getBlock(pos);

                    if (BlockRedstoneDiode.isDiode(block1)) {
                        block1.onUpdate(BLOCK_UPDATE_REDSTONE);
                    }
                }
            }
        }
    }

    public void updateAround(Vector3 pos) {
        for (BlockFace face : BlockFace.getValues()) {
            normalUpdateQueue.add(pos.getSideVec(face));
        }
    }

    public void updateAround(int x, int y, int z) {
        updateAround(new Vector3(x, y, z));
    }

    public void scheduleUpdate(Block pos, int delay) {
        this.scheduleUpdate(pos, pos, delay, 0, true);
    }

    public void scheduleRandomUpdate(Block pos, int delay) {
        this.scheduleRandomUpdate(pos, pos, delay, 0, true);
    }

    public void scheduleUpdate(Block block, Vector3 pos, int delay) {
        this.scheduleUpdate(block, pos, delay, 0, true);
    }

    public void scheduleRandomUpdate(Block block, Vector3 pos, int delay) {
        this.scheduleRandomUpdate(block, pos, delay, 0, true);
    }

    public void scheduleUpdate(Block block, Vector3 pos, int delay, int priority) {
        this.scheduleUpdate(block, pos, delay, priority, true);
    }

    public void scheduleRandomUpdate(Block block, Vector3 pos, int delay, int priority) {
        this.scheduleRandomUpdate(block, pos, delay, priority, true);
    }

    public void scheduleUpdate(Block block, Vector3 pos, int delay, int priority, boolean checkArea) {
        if (block.isAir() || (checkArea && !this.isChunkLoaded(pos.getChunkX(), pos.getChunkZ()))) {
            return;
        }

        BlockUpdateEntry entry = new BlockUpdateEntry(pos.floor(), block, ((long) delay) + getCurrentTick(), priority);

        if (!this.updateQueue.contains(entry)) {
            this.updateQueue.add(entry);
        }
    }

    public void scheduleRandomUpdate(Block block, Vector3 pos, int delay, int priority, boolean checkArea) {
        if (block.isAir() || (checkArea && !this.isChunkLoaded(pos.getChunkX(), pos.getChunkZ()))) {
            return;
        }

        BlockUpdateEntry entry = new BlockUpdateEntry(pos.floor(), block, ((long) delay) + getCurrentTick(), priority);

        if (!this.randomUpdateQueue.contains(entry)) {
            this.randomUpdateQueue.add(entry);
        }
    }

    public boolean cancelScheduledUpdate(Vector3 pos, Block block) {
        BlockUpdateEntry entry = new BlockUpdateEntry(pos, block);

        return this.updateQueue.remove(entry);
    }

    public boolean cancelScheduledRandomUpdate(Vector3 pos, Block block) {
        BlockUpdateEntry entry = new BlockUpdateEntry(pos, block);

        return this.randomUpdateQueue.remove(entry);
    }

    public boolean isUpdateScheduled(Vector3 pos, Block block) {
        BlockUpdateEntry entry = new BlockUpdateEntry(pos, block);

        return this.updateQueue.contains(entry);
    }

    public boolean isRandomUpdateScheduled(Vector3 pos, Block block) {
        BlockUpdateEntry entry = new BlockUpdateEntry(pos, block);

        return this.randomUpdateQueue.contains(entry);
    }

    public boolean isBlockTickPending(Vector3 pos, Block block) {
        return this.updateQueue.isBlockTickPending(pos, block);
    }

    public boolean isRandomBlockTickPending(Vector3 pos, Block block) {
        return this.randomUpdateQueue.isBlockTickPending(pos, block);
    }

    public Set<BlockUpdateEntry> getPendingBlockUpdates(FullChunk chunk) {
        int minX = (chunk.getX() << 4) - 2;
        int maxX = minX + 16 + 2;
        int minZ = (chunk.getZ() << 4) - 2;
        int maxZ = minZ + 16 + 2;

        return this.getPendingBlockUpdates(new SimpleAxisAlignedBB(minX, heightRange.getMinY(), minZ, maxX, heightRange.getMaxY(), maxZ));
    }

    public Set<BlockUpdateEntry> getPendingRandomBlockUpdates(FullChunk chunk) {
        int minX = (chunk.getX() << 4) - 2;
        int maxX = minX + 16 + 2;
        int minZ = (chunk.getZ() << 4) - 2;
        int maxZ = minZ + 16 + 2;

        return this.getPendingRandomBlockUpdates(new SimpleAxisAlignedBB(minX, heightRange.getMinY(), minZ, maxX, heightRange.getMaxY(), maxZ));
    }

    public Set<BlockUpdateEntry> getPendingBlockUpdates(AxisAlignedBB boundingBox) {
        return updateQueue.getPendingBlockUpdates(boundingBox);
    }

    public Set<BlockUpdateEntry> getPendingRandomBlockUpdates(AxisAlignedBB boundingBox) {
        return randomUpdateQueue.getPendingBlockUpdates(boundingBox);
    }

    public boolean hasTickInPendingTicks(Vector3 pos, Block block) {
        return isUpdateScheduled(pos, block) || isRandomUpdateScheduled(pos, block);
    }

    public Block[] getCollisionBlocks(AxisAlignedBB bb) {
        return this.getCollisionBlocks(bb, false);
    }

    public Block[] getCollisionBlocks(AxisAlignedBB bb, boolean targetFirst) {
        int minX = Mth.floor(bb.getMinX());
        int minY = Mth.floor(bb.getMinY());
        int minZ = Mth.floor(bb.getMinZ());
        int maxX = Mth.floor(bb.getMaxX());
        int maxY = Mth.floor(bb.getMaxY());
        int maxZ = Mth.floor(bb.getMaxZ());

        long loopTimes = 0;
        if (targetFirst) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        if (loopTimes++ > 1000000) {
                            this.server.getLogger().logException(new AxisAlignedBBLoopException("Level.getCollisionBlocks bb=" + bb + " minX=" + minX + " maxX=" + maxX + " minY=" + minY + " maxY=" + maxY + " minZ=" + minZ + " maxZ=" + maxZ + " x=" + x + " y=" + y + " z=" + z));
                            return new Block[0];
                        }
                        Block block = this.getBlock(x, y, z, false);
                        if (block.getId() != BlockID.AIR && block.collidesWithBB(bb)) {
                            return new Block[]{block};
                        }
                    }
                }
            }
            return new Block[0];
        } else {
            List<Block> collides = new ObjectArrayList<>();
            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        if (loopTimes++ > 1000000) {
                            this.server.getLogger().logException(new AxisAlignedBBLoopException("Level.getCollisionBlocks bb=" + bb + " minX=" + minX + " maxX=" + maxX + " minY=" + minY + " maxY=" + maxY + " minZ=" + minZ + " maxZ=" + maxZ + " x=" + x + " y=" + y + " z=" + z));
                            return collides.toArray(new Block[0]);
                        }
                        Block block = this.getBlock(x, y, z, false);
                        if (block.getId() != BlockID.AIR && block.collidesWithBB(bb)) {
                            collides.add(block);
                        }
                    }
                }
            }
            return collides.toArray(new Block[0]);
        }
    }

    public boolean isFullBlock(Vector3 pos) {
        AxisAlignedBB bb;
        if (pos instanceof Block) {
            if (((Block) pos).isSolid()) {
                return true;
            }
            bb = ((Block) pos).getBoundingBox();
        } else {
            bb = this.getBlock(pos).getBoundingBox();
        }

        return bb != null && bb.getAverageEdgeLength() >= 1;
    }

    /**
     * @return block hit result
     */
    @Nullable
    public MovingObjectPosition clip(Vector3 a, Vector3 b, boolean liquid, int maxDistance) {
        return clip(a, b, liquid, maxDistance, false);
    }

    /**
     * @return block hit result
     */
    @Nullable
    public MovingObjectPosition clip(Vector3 a, Vector3 b, boolean liquid, int maxDistance, boolean ignoreBarrier) {
        int xBlock0 = Mth.floor(a.x);
        int yBlock0 = Mth.floor(a.y);
        int zBlock0 = Mth.floor(a.z);
        int xBlock1 = Mth.floor(b.x);
        int yBlock1 = Mth.floor(b.y);
        int zBlock1 = Mth.floor(b.z);

        MovingObjectPosition hitResult;
        if (liquid) {
            Block extraBlock = getExtraBlock(xBlock0, yBlock0, zBlock0, false);
            if (extraBlock.isLiquid()) {
                hitResult = extraBlock.clip(a, b, extraBlock::getCollisionBoundingBox);
            } else if (!extraBlock.isAir()) {
                hitResult = extraBlock.calculateIntercept(a, b);
            } else {
                Block block = getBlock(xBlock0, yBlock0, zBlock0, false);
                if (block.isLiquid()) {
                    hitResult = block.clip(a, b, block::getCollisionBoundingBox);
                } else if (ignoreBarrier && block.is(Block.BARRIER)) {
                    hitResult = null;
                } else {
                    hitResult = block.calculateIntercept(a, b);
                }
            }
        } else {
            Block block = getBlock(xBlock0, yBlock0, zBlock0, false);
            if (ignoreBarrier && block.is(Block.BARRIER)) {
                hitResult = null;
            } else {
                hitResult = block.calculateIntercept(a, b);
            }
        }
        if (hitResult != null) {
            return hitResult;
        }

        for (int i = 0; i < maxDistance; i++) {
            if (xBlock0 == xBlock1 && yBlock0 == yBlock1 && zBlock0 == zBlock1) {
                return null;
            }

            double xClip = 0;
            double yClip = 0;
            double zClip = 0;
            boolean hasXClip = false;
            boolean hasYClip = false;
            boolean hasZClip = false;

            if (xBlock1 > xBlock0) {
                hasXClip = true;
                xClip = xBlock0 + 1;
            }
            if (xBlock1 < xBlock0) {
                hasXClip = true;
                xClip = xBlock0;
            }
            if (yBlock1 > yBlock0) {
                hasYClip = true;
                yClip = yBlock0 + 1;
            }
            if (yBlock1 < yBlock0) {
                hasYClip = true;
                yClip = yBlock0;
            }
            if (zBlock1 > zBlock0) {
                hasZClip = true;
                zClip = zBlock0 + 1;
            }
            if (zBlock1 < zBlock0) {
                hasZClip = true;
                zClip = zBlock0;
            }

            double xDist = 999;
            double yDist = 999;
            double zDist = 999;
            double xd = b.x - a.x;
            double yd = b.y - a.y;
            double zd = b.z - a.z;

            if (hasXClip) {
                xDist = (xClip - a.x) / xd;
            }
            if (hasYClip) {
                yDist = (yClip - a.y) / yd;
            }
            if (hasZClip) {
                zDist = (zClip - a.z) / zd;
            }

            if (yDist <= xDist || zDist <= xDist) {
                if (zDist <= yDist) {
                    if (zBlock1 <= zBlock0) {
                        --zBlock0;
                    } else {
                        ++zBlock0;
                    }

                    a.x = xd * zDist + a.x;
                    a.y = yd * zDist + a.y;
                    a.z = zClip;
                } else {
                    if (yBlock1 <= yBlock0) {
                        --yBlock0;
                    } else {
                        ++yBlock0;
                    }

                    a.x = xd * yDist + a.x;
                    a.y = yClip;
                    a.z = zd * yDist + a.z;
                }
            } else {
                if (xBlock1 <= xBlock0) {
                    --xBlock0;
                } else {
                    ++xBlock0;
                }

                a.x = xClip;
                a.y = yd * xDist + a.y;
                a.z = zd * xDist + a.z;
            }

            if (liquid) {
                Block extraBlock = getExtraBlock(xBlock0, yBlock0, zBlock0, false);
                if (extraBlock.isLiquid()) {
                    hitResult = extraBlock.clip(a, b, extraBlock::getCollisionBoundingBox);
                } else if (!extraBlock.isAir()) {
                    hitResult = extraBlock.calculateIntercept(a, b);
                } else {
                    Block block = getBlock(xBlock0, yBlock0, zBlock0, false);
                    if (block.isLiquid()) {
                        hitResult = block.clip(a, b, block::getCollisionBoundingBox);
                    } else if (!ignoreBarrier || !block.is(Block.BARRIER)) {
                        hitResult = block.calculateIntercept(a, b);
                    }
                }
            } else {
                Block block = getBlock(xBlock0, yBlock0, zBlock0, false);
                if (!ignoreBarrier || !block.is(Block.BARRIER)) {
                    hitResult = block.calculateIntercept(a, b);
                }
            }
            if (hitResult != null) {
                return hitResult;
            }
        }

        return null;
    }

    public AxisAlignedBB[] getCollisionCubes(Entity entity, AxisAlignedBB bb) {
        return this.getCollisionCubes(entity, bb, true);
    }

    public AxisAlignedBB[] getCollisionCubes(Entity entity, AxisAlignedBB bb, boolean entities) {
        return getCollisionCubes(entity, bb, entities, false);
    }

    public AxisAlignedBB[] getCollisionCubes(Entity entity, AxisAlignedBB bb, boolean entities, boolean solidEntities) {
        int minX = Mth.floor(bb.getMinX());
        int minY = Mth.floor(bb.getMinY());
        int minZ = Mth.floor(bb.getMinZ());
        int maxX = Mth.floor(bb.getMaxX());
        int maxY = Mth.floor(bb.getMaxY());
        int maxZ = Mth.floor(bb.getMaxZ());

        List<AxisAlignedBB> collides = new ObjectArrayList<>();

        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    Block block = this.getBlock(x, y, z, false);
                    // EC优化：使投掷物可以穿过屏障方块
                    if (block.getId() == BlockID.BARRIER && entity.canPassThroughBarrier()) {
                        continue;
                    }
                    if (!block.canPassThrough() && block.collidesWithBB(bb)) {
                        collides.add(block.getBoundingBox());
                    }
                }
            }
        }

        if (entities || solidEntities) {
            for (Entity ent : this.getCollidingEntities(bb.grow(0.25f, 0.25f, 0.25f), entity)) {
                if (solidEntities && !ent.canPassThrough()) {
                    collides.add(ent.boundingBox.clone());
                }
            }
        }

        return collides.toArray(new AxisAlignedBB[0]);
    }

    public boolean hasCollision(AxisAlignedBB bb) {
        return hasCollision(null, bb, false);
    }

    public boolean hasCollision(Entity entity, AxisAlignedBB bb, boolean entities) {
        int minX = Mth.floor(bb.getMinX());
        int minY = Mth.floor(bb.getMinY());
        int minZ = Mth.floor(bb.getMinZ());
        int maxX = Mth.floor(bb.getMaxX());
        int maxY = Mth.floor(bb.getMaxY());
        int maxZ = Mth.floor(bb.getMaxZ());

        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    Block block = this.getBlock(x, y, z);
                    if (!block.canPassThrough() && block.collidesWithBB(bb)) {
                        return true;
                    }
                }
            }
        }

        if (entities) {
            return this.getCollidingEntities(bb.grow(0.25f, 0.25f, 0.25f), entity).length > 0;
        }
        return false;
    }

    public int getFullLight(Vector3 pos) {
        FullChunk chunk = this.getChunk((int) pos.x >> 4, (int) pos.z >> 4, false);
        int level = 0;
        if (chunk != null) {
            level = chunk.getBlockSkyLight((int) pos.x & 0x0f, (int) pos.y, (int) pos.z & 0x0f);
            level -= this.skyLightSubtracted;

            if (level < 15) {
                level = Math.max(chunk.getBlockLight((int) pos.x & 0x0f, (int) pos.y, (int) pos.z & 0x0f),
                        level);
            }
        }

        return level;
    }

    public int calculateSkylightSubtracted(float tickDiff) {
        float angle = this.calculateCelestialAngle(getTime(), tickDiff);
        float light = 1 - (Mth.cos(angle * ((float) Math.PI * 2F)) * 2 + 0.5f);
        light = light < 0 ? 0 : light > 1 ? 1 : light;
        light = 1 - light;
        light = (float) ((double) light * ((isRaining() ? 1 : 0) - (double) 5f / 16d));
        light = (float) ((double) light * ((isThundering() ? 1 : 0) - (double) 5f / 16d));
        light = 1 - light;
        return (int) (light * 11f);
    }

    public float calculateCelestialAngle(int time, float tickDiff) {
        float angle = ((float) time + tickDiff) / 24000f - 0.25f;

        if (angle < 0) {
            ++angle;
        }

        if (angle > 1) {
            --angle;
        }

        float i = 1 - (float) ((Mth.cos((double) angle * Math.PI) + 1) / 2d);
        angle = angle + (i - angle) / 3;
        return angle;
    }

    public int getMoonPhase(long worldTime) {
        return (int) (worldTime / 24000 % 8 + 8) % 8;
    }

    public int getFullBlock(int layer, int x, int y, int z) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, false);
        return chunk == null ? 0 : chunk.getFullBlock(layer, x & 0x0f, y, z & 0x0f);
    }

    public Block getBlock(Vector3 pos) {
        return getBlock(0, pos);
    }

    public Block getBlock(BlockVector3 pos) {
        return getBlock(0, pos);
    }

    public Block getExtraBlock(Vector3 pos) {
        return getBlock(1, pos);
    }

    public Block getExtraBlock(BlockVector3 pos) {
        return getBlock(1, pos);
    }

    public Block getBlock(int layer, Vector3 pos) {
        return this.getBlock(layer, pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
    }

    public Block getBlock(int layer, BlockVector3 pos) {
        return this.getBlock(layer, pos.getX(), pos.getY(), pos.getZ());
    }

    public Block getBlock(Vector3 pos, boolean load) {
        return getBlock(0, pos, load);
    }

    public Block getBlock(BlockVector3 pos, boolean load) {
        return getBlock(0, pos, load);
    }

    public Block getExtraBlock(Vector3 pos, boolean load) {
        return getBlock(1, pos, load);
    }

    public Block getExtraBlock(BlockVector3 pos, boolean load) {
        return getBlock(1, pos, load);
    }

    public Block getBlock(int layer, Vector3 pos, boolean load) {
        return this.getBlock(layer, pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), load);
    }

    public Block getBlock(int layer, BlockVector3 pos, boolean load) {
        return this.getBlock(layer, pos.getX(), pos.getY(), pos.getZ(), load);
    }

    public Block getBlock(int x, int y, int z) {
        return getBlock(0, x, y, z);
    }

    public Block getExtraBlock(int x, int y, int z) {
        return getBlock(1, x, y, z);
    }

    public Block getBlock(int layer, int x, int y, int z) {
        return getBlock(layer, x, y, z, true);
    }

    public Block getBlock(int x, int y, int z, boolean load) {
        return getBlock(0, x, y, z, load);
    }

    public Block getExtraBlock(int x, int y, int z, boolean load) {
        return getBlock(1, x, y, z, load);
    }

    public Block getBlock(int layer, int x, int y, int z, boolean load) {
        int fullState;
        if (heightRange.isValidBlockY(y)) {
            int cx = x >> 4;
            int cz = z >> 4;
            BaseFullChunk chunk;
            if (load) {
                chunk = getChunk(cx, cz);
            } else {
                chunk = getChunkIfLoaded(cx, cz);
            }
            if (chunk != null) {
                fullState = chunk.getFullBlock(layer, x & 0xF, y, z & 0xF);
            } else {
                fullState = 0;
            }
        } else {
            fullState = 0;
        }
        Block block = Block.fromFullId(fullState);
        block.x = x;
        block.y = y;
        block.z = z;
        block.level = this;
        return block;
    }

    public void updateAllLight(Vector3 pos) {
        this.updateAllLight(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
    }

    public void updateAllLight(BlockVector3 pos) {
        this.updateAllLight(pos.getX(), pos.getY(), pos.getZ());
    }

    public void updateAllLight(int x, int y, int z) {
        this.updateBlockSkyLight(x, y, z);
        this.addLightUpdate(x, y, z);
    }

    public void updateBlockSkyLight(int x, int y, int z) {
        // todo
    }

    public void updateBlockLight(Map<Long, Set<Integer>> map) {
        int size = map.size();
        if (size == 0) {
            return;
        }
        Queue<Long> lightPropagationQueue = new ConcurrentLinkedQueue<>();
        Queue<LightUpdateEntry> lightRemovalQueue = new ConcurrentLinkedQueue<>();
        LongSet visited = new LongOpenHashSet();
        LongSet removalVisited = new LongOpenHashSet();

        Iterator<Map.Entry<Long, Set<Integer>>> iter = map.entrySet().iterator();
        while (iter.hasNext() && size-- > 0) {
            Map.Entry<Long, Set<Integer>> entry = iter.next();
            iter.remove();
            long index = entry.getKey();
            Set<Integer> blocks = entry.getValue();
            int chunkX = Level.getHashX(index);
            int chunkZ = Level.getHashZ(index);
            int bx = chunkX << 4;
            int bz = chunkZ << 4;
            for (int blockHash : blocks) {
                int y = localBlockY(blockHash);
                int x = bx | localBlockX(blockHash);
                int z = bz | localBlockZ(blockHash);
                BaseFullChunk chunk = getChunk(chunkX, chunkZ, false);
                if (chunk != null) {
                    int lcx = x & 0xF;
                    int lcz = z & 0xF;
                    int oldLevel = chunk.getBlockLight(lcx, y, lcz);
                    int newLevel = Block.light[chunk.getBlockId(0, lcx, y, lcz)];
                    if (oldLevel != newLevel) {
                        this.setBlockLightAt(x, y, z, newLevel);
                        long hash = Hash.hashBlockPos(x, y, z);
                        if (newLevel < oldLevel) {
                            removalVisited.add(hash);
                            lightRemovalQueue.add(new LightUpdateEntry(x, y, z, oldLevel));
                        } else {
                            visited.add(hash);
                            lightPropagationQueue.add(hash);
                        }
                    }
                }
            }
        }

        LightUpdateEntry entry;
        while ((entry = lightRemovalQueue.poll()) != null) {
            int x = entry.x;
            int y = entry.y;
            int z = entry.z;

            int lightLevel = entry.light;

            this.computeRemoveBlockLight(x - 1, y, z, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
            this.computeRemoveBlockLight(x + 1, y, z, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
            this.computeRemoveBlockLight(x, y - 1, z, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
            this.computeRemoveBlockLight(x, y + 1, z, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
            this.computeRemoveBlockLight(x, y, z - 1, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
            this.computeRemoveBlockLight(x, y, z + 1, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
        }

        Long hash;
        while ((hash = lightPropagationQueue.poll()) != null) {
            long node = hash;

            int x = Hash.hashBlockPosX(node);
            int y = Hash.hashBlockPosY(node);
            int z = Hash.hashBlockPosZ(node);

            int lightLevel = this.getBlockLightAt(x, y, z)
                    - Block.lightFilter[this.getBlock(x, y, z).getId()]
                    - Block.lightFilter[this.getExtraBlock(x, y, z).getId()];

            if (lightLevel >= 1) {
                this.computeSpreadBlockLight(x - 1, y, z, lightLevel, lightPropagationQueue, visited);
                this.computeSpreadBlockLight(x + 1, y, z, lightLevel, lightPropagationQueue, visited);
                this.computeSpreadBlockLight(x, y - 1, z, lightLevel, lightPropagationQueue, visited);
                this.computeSpreadBlockLight(x, y + 1, z, lightLevel, lightPropagationQueue, visited);
                this.computeSpreadBlockLight(x, y, z - 1, lightLevel, lightPropagationQueue, visited);
                this.computeSpreadBlockLight(x, y, z + 1, lightLevel, lightPropagationQueue, visited);
            }
        }
    }

    private void computeRemoveBlockLight(int x, int y, int z, int currentLight, Queue<LightUpdateEntry> queue,
                                         Queue<Long> spreadQueue, LongSet visited, LongSet spreadVisited) {
        int current = this.getBlockLightAt(x, y, z);
        if (current != 0 && current < currentLight) {
            this.setBlockLightAt(x, y, z, 0);
            if (current > 1) {
                long index = Hash.hashBlockPos(x, y, z);
                if (visited.add(index)) {
                    queue.add(new LightUpdateEntry(x, y, z, current));
                }
            }
        } else if (current >= currentLight) {
            long index = Hash.hashBlockPos(x, y, z);
            if (spreadVisited.add(index)) {
                spreadQueue.add(index);
            }
        }
    }

    private void computeSpreadBlockLight(int x, int y, int z, int currentLight, Queue<Long> queue, LongSet visited) {
        int current = this.getBlockLightAt(x, y, z);
        if (current < currentLight - 1) {
            this.setBlockLightAt(x, y, z, currentLight);

            long index = Hash.hashBlockPos(x, y, z);
            if (visited.add(index)) {
                if (currentLight > 1) {
                    queue.add(index);
                }
            }
        }
    }

    private final Map<Long, Set<Integer>> lightQueue = new ConcurrentHashMap<>(8, 0.9f, 1);

    public void addLightUpdate(int x, int y, int z) {
        long index = chunkHash(x >> 4, z >> 4);
        lightQueue.computeIfAbsent(index, key -> Collections.newSetFromMap(new ConcurrentHashMap<>(8, 0.9f, 1)))
                .add(Level.localBlockHash(x, y, z));
    }

    @Override
    public void setBlockFullIdAt(int layer, int x, int y, int z, int fullId) {
        setBlock(layer, x, y, z, Block.fromFullId(fullId), false, false);
    }

    public boolean setBlock(Vector3 pos, Block block) {
        return this.setBlock(0, pos, block);
    }

    public boolean setBlock(BlockVector3 pos, Block block) {
        return this.setBlock(0, pos, block);
    }

    public boolean setExtraBlock(Vector3 pos, Block block) {
        return this.setBlock(1, pos, block);
    }

    public boolean setExtraBlock(BlockVector3 pos, Block block) {
        return this.setBlock(1, pos, block);
    }

    public boolean setBlock(int layer, Vector3 pos, Block block) {
        return this.setBlock(layer, pos, block, false);
    }

    public boolean setBlock(int layer, BlockVector3 pos, Block block) {
        return this.setBlock(layer, pos, block, false);
    }

    public boolean setBlock(Vector3 pos, Block block, boolean direct) {
        return this.setBlock(0, pos, block, direct);
    }

    public boolean setBlock(BlockVector3 pos, Block block, boolean direct) {
        return this.setBlock(0, pos, block, direct);
    }

    public boolean setExtraBlock(Vector3 pos, Block block, boolean direct) {
        return this.setBlock(1, pos, block, direct);
    }

    public boolean setExtraBlock(BlockVector3 pos, Block block, boolean direct) {
        return this.setBlock(1, pos, block, direct);
    }

    public boolean setBlock(int layer, Vector3 pos, Block block, boolean direct) {
        return this.setBlock(layer, pos, block, direct, true);
    }

    public boolean setBlock(int layer, BlockVector3 pos, Block block, boolean direct) {
        return this.setBlock(layer, pos, block, direct, true);
    }

    public boolean setBlock(Vector3 pos, Block block, boolean direct, boolean update) {
        return this.setBlock(0, pos, block, direct, update);
    }

    public boolean setBlock(BlockVector3 pos, Block block, boolean direct, boolean update) {
        return this.setBlock(0, pos, block, direct, update);
    }

    public boolean setExtraBlock(Vector3 pos, Block block, boolean direct, boolean update) {
        return this.setBlock(1, pos, block, direct, update);
    }

    public boolean setExtraBlock(BlockVector3 pos, Block block, boolean direct, boolean update) {
        return this.setBlock(1, pos, block, direct, update);
    }

    public boolean setBlock(int layer, Vector3 pos, Block block, boolean direct, boolean update) {
        return setBlock(layer, pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), block, direct, update);
    }

    public boolean setBlock(int layer, BlockVector3 pos, Block block, boolean direct, boolean update) {
        return setBlock(layer, pos.getX(), pos.getY(), pos.getZ(), block, direct, update);
    }

    public boolean setBlock(int x, int y, int z, Block block) {
        return setBlock(0, x, y, z, block);
    }

    public boolean setExtraBlock(int x, int y, int z, Block block) {
        return setBlock(1, x, y, z, block);
    }

    public boolean setBlock(int layer, int x, int y, int z, Block block) {
        return setBlock(layer, x, y, z, block, false);
    }

    public boolean setBlock(int x, int y, int z, Block block, boolean direct) {
        return setBlock(0, x, y, z, block, direct);
    }

    public boolean setExtraBlock(int x, int y, int z, Block block, boolean direct) {
        return setBlock(1, x, y, z, block, direct);
    }

    public boolean setBlock(int layer, int x, int y, int z, Block block, boolean direct) {
        return setBlock(layer, x, y, z, block, direct, true);
    }

    public boolean setBlock(int x, int y, int z, Block block, boolean direct, boolean update) {
        return setBlock(0, x, y, z, block, direct, update);
    }

    public boolean setExtraBlock(int x, int y, int z, Block block, boolean direct, boolean update) {
        return setBlock(1, x, y, z, block, direct, update);
    }

    public boolean setBlock(int layer, int x, int y, int z, Block block, boolean direct, boolean update) {
        return setBlock(layer, x, y, z, block, direct, update, this.getChunkPlayers(x >> 4, z >> 4).values().toArray(new Player[0]));
    }

    public boolean setBlock(int x, int y, int z, Block block, boolean direct, boolean update, Player[] sends) {
        return setBlock(0, x, y, z, block, direct, update, sends);
    }

    public boolean setExtraBlock(int x, int y, int z, Block block, boolean direct, boolean update, Player[] sends) {
        return setBlock(1, x, y, z, block, direct, update, sends);
    }

    public boolean setBlock(int layer, int x, int y, int z, Block block, boolean direct, boolean update, Player[] sends) {
        if (!heightRange.isValidBlockY(y)) {
            return false;
        }

        BaseFullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        if (chunk == null) {
            return false;
        }

        Block blockPrevious = chunk.getAndSetBlock(layer, x & 0xF, y, z & 0xF, block);
        if (blockPrevious.getFullId() == block.getFullId()) {
            return false;
        }

        block.x = x;
        block.y = y;
        block.z = z;
        block.level = this;

        for (Consumer<Block> callback : this.callbackBlockSet.values()) {
            callback.accept(block);
        }

        int cx = x >> 4;
        int cz = z >> 4;
        long index = Level.chunkHash(cx, cz);
        if (direct) {
            if (sends == null || sends.length == 0) {
                this.sendBlocks(this.getChunkPlayers(cx, cz).values().toArray(new Player[0]), new Block[]{block}, UpdateBlockPacket.FLAG_ALL_PRIORITY, layer);
            } else {
                this.sendBlocks(sends, new Block[]{block}, UpdateBlockPacket.FLAG_ALL_PRIORITY, layer);
            }
        } else {
            addBlockChange(index, x, y, z);
        }

        for (ChunkLoader loader : this.getChunkLoaders(cx, cz)) {
            loader.onBlockChanged(block);
        }

        if (update) {
            if (blockPrevious.isTransparent() != block.isTransparent() || blockPrevious.getLightLevel() != block.getLightLevel()) {
                addLightUpdate(x, y, z);
            }

            BlockUpdateEvent ev = new BlockUpdateEvent(block, layer);
            this.server.getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                for (Entity entity : this.getNearbyEntities(new SimpleAxisAlignedBB(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1))) {
                    entity.scheduleUpdate();
                }

                block = ev.getBlock();
                block.onUpdate(BLOCK_UPDATE_NORMAL);

                int anotherLayer = (layer + 1) & 1;
                Block anotherBlock = getBlock(anotherLayer, x, y, z);
                BlockUpdateEvent event = new BlockUpdateEvent(anotherBlock, anotherLayer);
                this.server.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    anotherBlock = event.getBlock();
                    anotherBlock.onUpdate(BLOCK_UPDATE_NORMAL);
                }

                this.updateAround(x, y, z);

                if (block.hasComparatorInputOverride()) {
                    this.updateComparatorOutputLevel(block);
                }
            }
        }
        return true;
    }

    private void addBlockChange(int x, int y, int z) {
        long index = Level.chunkHash(x >> 4, z >> 4);
        addBlockChange(index, x, y, z);
    }

    private void addBlockChange(long index, int x, int y, int z) {
        changeBlockLock.lock();
        try {
            changedBlocks.computeIfAbsent(index, key -> new IntOpenHashSet())
                    .add(Level.localBlockHash(x, y, z));
        } finally {
            changeBlockLock.unlock();
        }
    }

    public EntityItem dropItem(Vector3 source, Item item) {
        return this.dropItem(source, item, null);
    }

    public EntityItem dropItem(Vector3 source, Item item, @Nullable Vector3 motion) {
        return this.dropItem(source, item, motion, 10);
    }

    public EntityItem dropItem(Vector3 source, Item item, @Nullable Vector3 motion, int delay) {
        return this.dropItem(source, item, motion, false, delay);
    }

    public EntityItem dropItem(Vector3 source, Item item, @Nullable Vector3 motion, boolean dropAround, int delay) {
        FullChunk chunk = getChunk(source.getChunkX(), source.getChunkZ(), true);
        if (chunk == null) {
            return null;
        }

        Random random = ThreadLocalRandom.current();
        if (motion == null) {
            if (dropAround) {
                float f = random.nextFloat() * 0.5f;
                float f1 = random.nextFloat() * ((float) Math.PI * 2);

                motion = new Vector3(-Mth.sin(f1) * f, 0.20000000298023224, Mth.cos(f1) * f);
            } else {
                motion = new Vector3(random.nextFloat() * 0.2f - 0.1f, 0.2f,
                        random.nextFloat() * 0.2f - 0.1f);
            }
        }

        CompoundTag itemTag = NBTIO.putItemHelper(item);
        itemTag.setName("Item");

        if (!item.isNull()) {
            EntityItem itemEntity = new EntityItem(
                    chunk,
                    Entity.getDefaultNBT(source, motion, random.nextFloat() * 360, 0)
                            .putShort("Health", 5).putCompound("Item", itemTag).putShort("PickupDelay", delay));

            itemEntity.spawnToAll();
            return itemEntity;
        }
        return null;
    }

    public Item useBreakOn(Vector3 vector) {
        return this.useBreakOn(vector, null);
    }

    public Item useBreakOn(Vector3 vector, boolean createParticles) {
        return this.useBreakOn(vector, null, createParticles);
    }

    public Item useBreakOn(Vector3 vector, @Nullable Item item) {
        return this.useBreakOn(vector, item, null);
    }

    public Item useBreakOn(Vector3 vector, @Nullable Item item, boolean createParticles) {
        return this.useBreakOn(vector, item, null, createParticles);
    }

    public Item useBreakOn(Vector3 vector, @Nullable Item item, @Nullable Player player) {
        return this.useBreakOn(vector, item, player, false);
    }

    public Item useBreakOn(Vector3 vector, @Nullable Item item, @Nullable Player player, boolean createParticles) {
        return useBreakOn(vector, null, item, player, createParticles);
    }

    public Item useBreakOn(Vector3 vector, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player, boolean createParticles) {
        if (player != null && player.getGamemode() > 2) {
            return null;
        }
        Block target = this.getBlock(vector);
        Item[] drops;
        int dropExp = target.getDropExp();

        if (item == null) {
            item = Items.air();
        }

        boolean isEnchantedBook = item.getId() == Item.ENCHANTED_BOOK;
        boolean isSilkTouch = !isEnchantedBook && item.getEnchantment(Enchantment.SILK_TOUCH) != null;

        if (player != null) {
            if (player.getGamemode() == Player.ADVENTURE) {
                Set<String> canDestroy = item.getCanDestroyBlocks();
                if (canDestroy != null && !canDestroy.isEmpty()) {
                    boolean canBreak = false;
                    for (String blockName : canDestroy) {
                        Block canDestroyBlock = Block.fromIdentifier(blockName);
                        if (canDestroyBlock == null) {
                            continue;
                        }
                        if (canDestroyBlock.getId() != target.getId()) {
                            continue;
                        }
                        canBreak = true;
                        break;
                    }
                    if (!canBreak) {
                        return null;
                    }
                } else {
                    return null;
                }
            }

            float breakTime = player.isCreative() ? 0 : target.getBreakTime(item, player);

            if (breakTime > 0) {
                breakTime -= 0.15f; // 3 ticks
            }

            Item[] eventDrops;
            if (!player.isSurvival()) {
                eventDrops = new Item[0];
            } else if (isSilkTouch && target.canSilkTouch()) {
                eventDrops = new Item[]{target.getSilkTouchResource()};
            } else {
                eventDrops = target.getDrops(item, player);
            }

            long now = System.currentTimeMillis();
            BlockBreakEvent ev = new BlockBreakEvent(player, target, face, item, eventDrops, player.isCreative(),
                    (player.lastBreak + breakTime * 1000d) > now);

            if (player.isSurvival() && !target.isBreakable(item)) {
                ev.setCancelled();
            }

            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return null;
            }

            if (!ev.getInstaBreak() && ev.isFastBreak()) {
                return null;
            }

            player.lastBreak = now;

            drops = ev.getDrops();
            dropExp = ev.getDropExp();
        } else if (!target.isBreakable(item)) {
            return null;
        } else if (isSilkTouch && target.canSilkTouch()) {
            drops = new Item[]{target.getSilkTouchResource()};
        } else {
            drops = target.getDrops(item);
        }

        if (createParticles) {
            this.addParticle(new DestroyBlockParticle(target, target));
        }

        // Close BlockEntity before we check onBreak
        BlockEntity blockEntity = this.getBlockEntity(target);
        if (blockEntity != null) {
            blockEntity.onBreak();
            blockEntity.close();

            this.updateComparatorOutputLevel(target);
        }

        target.onBreak(item, player);

        Block newBlock = getBlock(target);
        Block extra = getExtraBlock(target);
        if (newBlock.isAir() && !extra.isAir()) {
            setBlock(target, extra, true, false);
            setExtraBlock(target, Block.get(BlockID.AIR), true, false);
        }

        item.useOn(target);
        if (item.isTool() && item.getDamage() > item.getMaxDurability()) {
            item = Items.air();
            if (player != null) {
                addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
            }
        }

        if (this.gameRules.getBoolean(GameRule.DO_TILE_DROPS)) {
            if (!isSilkTouch && player != null && player.isSurvival() && dropExp > 0 && drops.length != 0) {
                this.dropExpOrb(vector.add(0.5, 0.5, 0.5), dropExp);
            }

            if (player == null || player.isSurvival()) {
                for (Item drop : drops) {
                    if (drop.getCount() > 0) {
                        this.dropItem(vector.add(0.5, 0.5, 0.5), drop);
                    }
                }
            }
        }

        return item;
    }

    public void dropExpOrb(Vector3 source, int exp) {
        dropExpOrb(source, exp, null);
    }

    public void dropExpOrb(Vector3 source, int exp, @Nullable Vector3 motion) {
        dropExpOrb(source, exp, motion, 10);
    }

    public void dropExpOrb(Vector3 source, int exp, @Nullable Vector3 motion, int delay) {
        FullChunk chunk = this.getChunk(source.getChunkX(), source.getChunkZ(), true);
        if (chunk == null) {
            return;
        }

        Random rand = ThreadLocalRandom.current();
        for (int split : EntityXPOrb.splitIntoOrbSizes(exp)) {
            CompoundTag nbt = Entity.getDefaultNBT(source, motion == null ? new Vector3(
                    (rand.nextFloat() * 0.2f - 0.1f) * 2,
                    rand.nextFloat() * 0.4f,
                    (rand.nextFloat() * 0.2f - 0.1f) * 2) : motion,
                    rand.nextFloat() * 360f, 0);

            nbt.putShort("Value", split);
            nbt.putShort("PickupDelay", delay);

            Entity entity = new EntityXPOrb(chunk, nbt);
            entity.spawnToAll();
        }
    }

    public Item useItemOn(Vector3 vector, Item item, BlockFace face, float fx, float fy, float fz) {
        return this.useItemOn(vector, item, face, fx, fy, fz, null);
    }

    public Item useItemOn(Vector3 vector, Item item, BlockFace face, float fx, float fy, float fz, @Nullable Player player) {
        return this.useItemOn(vector, item, face, fx, fy, fz, player, true);
    }

    public Item useItemOn(Vector3 vector, Item item, BlockFace face, float fx, float fy, float fz, @Nullable Player player, boolean playSound) {
        Block target = this.getBlock(vector);
        Block block = target.getSide(face);

        if (!heightRange.isValidBlockY(block.y)) {
            return null;
        }

        if (target.getId() == Item.AIR) {
            return null;
        }

        if (player != null) {
            PlayerInteractEvent ev = new PlayerInteractEvent(player, item, target, face,
                    target.getId() == BlockID.AIR ? Action.RIGHT_CLICK_AIR : Action.RIGHT_CLICK_BLOCK);

            if (player.getGamemode() > 2) {
                ev.setCancelled();
            }

            this.server.getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                target.onUpdate(BLOCK_UPDATE_TOUCH);
                if (!player.isSneaking() && target.canBeActivated() && target.onActivate(item, face, player)) {
                    return item;
                }

                if (!player.isSneaking() && item.canBeActivated()
                        && item.onActivate(this, player, block, target, face, fx, fy, fz)) {
                    if (item.getCount() <= 0) {
                        item = Items.air();
                        return item;
                    }
                }
            } else {
                return null;
            }
        } else if (target.canBeActivated() && target.onActivate(item, face, null)) {
            return item;
        }

        Block hand;
        if (item.canBePlaced()) {
            hand = item.getBlock();
            hand.position(block);
        } else {
            return null;
        }

        boolean snowLayer = hand.getId() == BlockID.SNOW_LAYER && block.canContainSnow();
        if (!(block.canBeReplaced() || snowLayer || hand.isHalfSlab() && block.isHalfSlab() || block.isCandle() && hand.getId() == block.getId())) {
            return null;
        }

        if (target.canBeReplaced()) {
            if (target.getId() == hand.getId() && !(target instanceof BlockMultiface)) {
                return null;
            }

            block = target;
            hand.position(block);
        } else if (block.canBeReplaced() && block.getId() == hand.getId() && !block.is(Block.SNOW_LAYER) && !(block instanceof BlockMultiface)) {
            return null;
        }

        if (!hand.canPassThrough() && hand.getBoundingBox() != null && !hand.isDoor() && !hand.is(Block.BLOCK_BED) && !hand.is(Block.BLOCK_SKULL) && (!hand.is(Block.BAMBOO) || target.is(Block.BAMBOO) || target.is(Block.BAMBOO_SAPLING))) {
            Entity[] entities = this.getCollidingEntities(hand.getBoundingBox());
            for (Entity e : entities) {
                if (e instanceof EntityProjectile || e instanceof EntityItem || e instanceof EntityXPOrb || e instanceof EntityFirework || e instanceof EntityPainting
                        || e == player || (e instanceof Player && ((Player) e).isSpectator())) {
                    continue;
                }
                return null;
            }

            if (player != null) {
                Vector3 diff = player.getNextPosition().subtract(player.getPosition());
                AxisAlignedBB bb = player.getBoundingBox().getOffsetBoundingBox(diff.x, diff.y, diff.z);
                bb.expand(-0.01, -0.01, -0.01);
                if (hand.getBoundingBox().intersectsWith(bb)) {
                    // This is a hack to prevent the player from placing blocks inside themselves
                    return LazyHolder.INVALID_ITEM;
                }
            }
        }

        if (player != null) {
            if (player.isAdventure()) {
                Set<String> canPlaceOn = item.getCanPlaceOnBlocks();
                if (canPlaceOn != null && !canPlaceOn.isEmpty()) {
                    boolean canPlace = false;
                    for (String blockName : canPlaceOn) {
                        Block canPlaceOnBlock = Block.fromIdentifier(blockName);
                        if (canPlaceOnBlock == null) {
                            continue;
                        }
                        if (canPlaceOnBlock.getId() != target.getId()) {
                            continue;
                        }
                        canPlace = true;
                        break;
                    }
                    if (!canPlace) {
                        return null;
                    }
                } else {
                    return null;
                }
            }

            BlockPlaceEvent event = new BlockPlaceEvent(player, hand, block, target, item);
            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return null;
            }
        }

        if (!hand.place(item, block, target, face, fx, fy, fz, player)) {
            return null;
        }

        Block extraAfter = getExtraBlock(block);
        if (block.isWater()) {
            if (!extraAfter.isWater()) {
                if (hand.canContainWater() && (block.isLiquidSource() || hand.canContainFlowingWater())) {
                    this.setExtraBlock(block, block, true, false);
                }
            } else if (!hand.canContainWater() || !hand.canContainFlowingWater() && !extraAfter.isLiquidSource()) {
                this.setExtraBlock(block, Block.get(BlockID.AIR), true);
            }
        } else if (extraAfter.isWater() && (!hand.canContainWater() || !hand.canContainFlowingWater() && !extraAfter.isLiquidSource())) {
            this.setExtraBlock(block, Block.get(BlockID.AIR), true);
        }

        if (player != null) {
            BlockPlaceSound sound = new BlockPlaceSound(block.blockCenter(), hand.getId(), hand.getDamage());
            addSound(sound, player.getViewers().values());

            if (!player.isCreative()) {
                item.setCount(item.getCount() - 1);
            }
        }

        if (playSound) {
            hand.playPlaceSound(target);
        }

        if (item.getCount() <= 0) {
            item = Items.air();
        }
        return item;
    }

    public Entity getEntity(long entityId) {
        return this.entities.get(entityId);
    }

    public Entity[] getEntities() {
        return entities.values().toArray(new Entity[0]);
    }

    public Long2ObjectMap<Entity> getActors() {
        return entities;
    }

    public Entity[] getCollidingEntities(AxisAlignedBB bb) {
        return this.getCollidingEntities(bb, null);
    }

    public Entity[] getCollidingEntities(AxisAlignedBB bb, Entity entity) {
        List<Entity> nearby = new ObjectArrayList<>();

        if (entity == null || entity.canCollide()) {
            int minX = Mth.floor(bb.getMinX() - 2) >> 4;
            int maxX = Mth.floor(bb.getMaxX() + 2) >> 4;
            int minZ = Mth.floor(bb.getMinZ() - 2) >> 4;
            int maxZ = Mth.floor(bb.getMaxZ() + 2) >> 4;

            for (int x = minX; x <= maxX; ++x) {
                for (int z = minZ; z <= maxZ; ++z) {
                    for (Entity ent : this.getChunkEntities(x, z, false).values()) {
                        if ((entity == null || (ent != entity && entity.canCollideWith(ent)))
                                && ent.boundingBox.intersectsWith(bb)) {
                            nearby.add(ent);
                        }
                    }
                }
            }
        }

        return nearby.toArray(new Entity[0]);
    }

    public Entity[] getNearbyEntities(AxisAlignedBB bb) {
        return this.getNearbyEntities(bb, null);
    }

    public Entity[] getNearbyEntities(AxisAlignedBB bb, Entity entity) {
        return getNearbyEntities(bb, entity, false);
    }

    public Entity[] getNearbyEntities(AxisAlignedBB bb, Entity entity, boolean loadChunks) {
        List<Entity> nearby = new ObjectArrayList<>();

        int minX = Mth.floor(bb.getMinX() - 2) >> 4;
        int maxX = Mth.floor(bb.getMaxX() + 2) >> 4;
        int minZ = Mth.floor(bb.getMinZ() - 2) >> 4;
        int maxZ = Mth.floor(bb.getMaxZ() + 2) >> 4;

        long loops = 0;
        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                if (loops++ > 1000000) {
                    this.server.getLogger().logException(new AxisAlignedBBLoopException("Level.getNearbyEntities bb=" + bb + " minX=" + minX + " maxX=" + maxX + " minZ=" + minZ + " maxZ=" + maxZ + " x=" + x + " z=" + z));
                    return nearby.toArray(new Entity[0]);
                }
                for (Entity ent : this.getChunkEntities(x, z, loadChunks).values()) {
                    if (ent != entity && ent.boundingBox.intersectsWith(bb)) {
                        nearby.add(ent);
                    }
                }
            }
        }

        return nearby.toArray(new Entity[0]);
    }

    public boolean hasEntity(AxisAlignedBB aabb) {
        return hasEntity(aabb, false);
    }

    public boolean hasEntity(AxisAlignedBB aabb, boolean loadChunks) {
        return hasEntity(aabb, entity -> !(entity instanceof Player) || !((Player) entity).isSpectator(), loadChunks);
    }

    public boolean hasEntity(AxisAlignedBB aabb, Predicate<Entity> predicate) {
        return hasEntity(aabb, predicate, false);
    }

    public boolean hasEntity(AxisAlignedBB aabb, Predicate<Entity> predicate, boolean loadChunks) {
        int minX = Mth.floor(aabb.getMinX() - 2) >> 4;
        int maxX = Mth.floor(aabb.getMaxX() + 2) >> 4;
        int minZ = Mth.floor(aabb.getMinZ() - 2) >> 4;
        int maxZ = Mth.floor(aabb.getMaxZ() + 2) >> 4;

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (Entity ent : this.getChunkEntities(x, z, loadChunks).values()) {
                    if (ent.boundingBox.intersectsWith(aabb) && predicate.test(ent)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public Map<Long, BlockEntity> getBlockEntities() {
        return blockEntities;
    }

    public BlockEntity getBlockEntityById(long blockEntityId) {
        return this.blockEntities.get(blockEntityId);
    }

    public Map<Long, Player> getPlayers() {
        return players;
    }

    public Map<Integer, ChunkLoader> getLoaders() {
        return loaders;
    }

    public BlockEntity getBlockEntity(Vector3 pos) {
        return getBlockEntity(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
    }

    public BlockEntity getBlockEntity(BlockVector3 pos) {
        return getBlockEntity(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockEntity getBlockEntity(int x, int y, int z) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, false);

        if (chunk != null) {
            return chunk.getTile(x & 0x0f, y, z & 0x0f);
        }

        return null;
    }

    public BlockEntity getBlockEntityIfLoaded(Vector3 pos) {
        return getBlockEntityIfLoaded(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
    }

    public BlockEntity getBlockEntityIfLoaded(BlockVector3 pos) {
        return getBlockEntityIfLoaded(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockEntity getBlockEntityIfLoaded(int x, int y, int z) {
        FullChunk chunk = this.getChunkIfLoaded(x >> 4, z >> 4);

        if (chunk != null) {
            return chunk.getTile(x & 0x0f, y, z & 0x0f);
        }

        return null;
    }

    public Map<Long, Entity> getChunkEntities(int chunkX, int chunkZ) {
        return getChunkEntities(chunkX, chunkZ, true);
    }

    public Map<Long, Entity> getChunkEntities(int chunkX, int chunkZ, boolean loadChunks) {
        FullChunk chunk = loadChunks ? this.getChunk(chunkX, chunkZ) : this.getChunkIfLoaded(chunkX, chunkZ);
        return chunk != null ? chunk.getEntities() : Long2ObjectMaps.emptyMap();
    }

    public Map<Long, BlockEntity> getChunkBlockEntities(int chunkX, int chunkZ) {
        return getChunkBlockEntities(chunkX, chunkZ, true);
    }

    public Map<Long, BlockEntity> getChunkBlockEntities(int chunkX, int chunkZ, boolean loadChunks) {
        FullChunk chunk = loadChunks ? this.getChunk(chunkX, chunkZ) : this.getChunkIfLoaded(chunkX, chunkZ);
        return chunk != null ? chunk.getBlockEntities() : Long2ObjectMaps.emptyMap();
    }

    @Deprecated
    @Override
    public int getBlockIdAt(int layer, int x, int y, int z) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        return chunk == null ? Block.AIR : chunk.getBlockId(layer, x & 0x0f, y, z & 0x0f);
    }

    @Deprecated
    @Override
    public void setBlockIdAt(int layer, int x, int y, int z, int id) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        if (chunk == null) {
            return;
        }
        chunk.setBlockId(layer, x & 0x0f, y, z & 0x0f, id);
        addBlockChange(x, y, z);

        if (!server.isPrimaryThread()) {
            return;
        }

        temporalVector.setComponents(x, y, z);
        for (ChunkLoader loader : this.getChunkLoaders(x >> 4, z >> 4)) {
            loader.onBlockChanged(temporalVector);
        }
    }

    @Deprecated
    @Override
    public void setBlockAt(int layer, int x, int y, int z, int id, int data) {
        BaseFullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        if (chunk == null) {
            return;
        }
        chunk.setBlockId(layer, x & 0x0f, y, z & 0x0f, id);
        chunk.setBlockData(layer, x & 0x0f, y, z & 0x0f, data);
        addBlockChange(x, y, z);

        if (!server.isPrimaryThread()) {
            return;
        }

        temporalVector.setComponents(x, y, z);
        for (ChunkLoader loader : this.getChunkLoaders(x >> 4, z >> 4)) {
            loader.onBlockChanged(temporalVector);
        }
    }

    /*@Deprecated
    public int getBlockExtraDataAt(int x, int y, int z) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        return chunk == null ? 0 : chunk.getBlockExtraData(0, x & 0x0f, y, z & 0x0f);
    }

    @Deprecated
    public void setBlockExtraDataAt(int x, int y, int z, int id, int data) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        if (chunk == null) {
            return;
        }
        chunk.setBlockExtraData(0, x & 0x0f, y, z & 0x0f, (data << 8) | id);

        this.sendBlockExtraData(x, y, z, id, data);
    }*/

    @Deprecated
    @Override
    public int getBlockDataAt(int layer, int x, int y, int z) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        return chunk == null ? 0 : chunk.getBlockData(layer, x & 0x0f, y, z & 0x0f);
    }

    @Deprecated
    @Override
    public void setBlockDataAt(int layer, int x, int y, int z, int data) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        if (chunk == null) {
            return;
        }
        chunk.setBlockData(layer, x & 0x0f, y, z & 0x0f, data);
        addBlockChange(x, y, z);

        if (!server.isPrimaryThread()) {
            return;
        }

        temporalVector.setComponents(x, y, z);
        for (ChunkLoader loader : this.getChunkLoaders(x >> 4, z >> 4)) {
            loader.onBlockChanged(temporalVector);
        }
    }

    public int getBlockSkyLightAt(int x, int y, int z) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        return chunk == null ? 0 : chunk.getBlockSkyLight(x & 0x0f, y, z & 0x0f);
    }

    public void setBlockSkyLightAt(int x, int y, int z, int level) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        if (chunk == null) {
            return;
        }
        chunk.setBlockSkyLight(x & 0x0f, y, z & 0x0f, level & 0x0f);
    }

    public int getBlockLightAt(int x, int y, int z) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        return chunk == null ? 0 : chunk.getBlockLight(x & 0x0f, y, z & 0x0f);
    }

    public void setBlockLightAt(int x, int y, int z, int level) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        if (chunk == null) {
            return;
        }
        chunk.setBlockLight(x & 0x0f, y, z & 0x0f, level & 0x0f);
    }

    public Biome getBiome(int x, int z) {
        return Biome.getBiome(getBiomeId(x, z));
    }

    public int getBiomeId(int x, int z) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        return chunk == null ? BiomeID.OCEAN : chunk.getBiomeId(x & 0x0f, z & 0x0f);
    }

    public void setBiomeId(int x, int z, int biomeId) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        if (chunk == null) {
            return;
        }
        chunk.setBiomeId(x & 0x0f, z & 0x0f, biomeId);
    }

    public Biome getBiome(int x, int y, int z) {
        return Biome.getBiome(getBiomeId(x, y, z));
    }

    public int getBiomeId(int x, int y, int z) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        return chunk == null ? BiomeID.OCEAN : chunk.getBiomeId(x & 0xf, y, z & 0xf);
    }

    public void setBiomeId(int x, int y, int z, int biomeId) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        if (chunk == null) {
            return;
        }
        chunk.setBiomeId(x & 0xf, y, z & 0xf, biomeId);
    }

    public int getHeightMap(int x, int z) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        return chunk == null ? 0 : chunk.getHeightMap(x & 0x0f, z & 0x0f);
    }

    private void setHeightMap(int x, int z, int value) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        if (chunk == null) {
            return;
        }
        chunk.setHeightMap(x & 0x0f, z & 0x0f, value);
    }

    public Map<Long, BaseFullChunk> getChunks() {
        return chunks;
    }

    @Nullable
    @Override
    public BaseFullChunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, false);
    }

    @Nullable
    public BaseFullChunk getChunk(int chunkX, int chunkZ, boolean create) {
        long index = Level.chunkHash(chunkX, chunkZ);
        boolean isMainThread = server.isPrimaryThread();

        BaseFullChunk chunk = this.getChunkFromCache(isMainThread, index);
        if (chunk != null) {
            return chunk;
        }

        if (!isMainThread) {
/*
            Lock lock = new ReentrantLock();
            Condition condition = lock.newCondition();
            lock.lock();

            BaseFullChunk[] result = new BaseFullChunk[1];
            getServer().getScheduler().scheduleTask(null, () -> {
                lock.lock();
                try {
                    if (loadChunkInternal(index, chunkX, chunkZ, create)) {
                        result[0] = getChunkFromCache(true, index);
                    }
                } finally {
                    condition.signal();
                    lock.unlock();
                }
            });

            try {
                condition.await();
            } catch (InterruptedException ignored) {
            } finally {
                lock.unlock();
            }
            return result[0];
*/
            return null;
        }

        if (this.loadChunkInternal(index, chunkX, chunkZ, create)) {
            return this.getChunkFromCache(true, index);
        }

        return null;
    }

    @Nullable
    public BaseFullChunk getChunkIfLoaded(int chunkX, int chunkZ) {
        long index = Level.chunkHash(chunkX, chunkZ);
        boolean isMainThread = server.isPrimaryThread();
        BaseFullChunk chunk = this.getChunkFromCache(isMainThread, index);
        if (chunk != null) {
            return chunk;
        }
        if (provider != null) {
            chunk = this.provider.getLoadedChunk(index);
        }
        if (isMainThread && chunk != null) {
            this.storeChunkInCache(index, chunk);
        }
        return chunk;
    }

    public void generateChunkCallback(int x, int z, BaseFullChunk chunk) {
        generateChunkCallback(x, z, chunk, true);
    }

    public void generateChunkCallback(int x, int z, BaseFullChunk chunk, boolean isPopulated) {
        long index = Level.chunkHash(x, z);
        boolean queuedGen;
        if (this.chunkPopulationQueue.remove(index)) {
            FullChunk oldChunk = this.getChunk(x, z, false);
            for (int xx = -1; xx <= 1; ++xx) {
                for (int zz = -1; zz <= 1; ++zz) {
                    this.chunkPopulationLock.remove(Level.chunkHash(x + xx, z + zz));
                }
            }
            chunk.setProvider(this.provider);
            this.setChunk(x, z, chunk, false);
            chunk = this.getChunk(x, z, false);
            if (chunk != null && (oldChunk == null || !isPopulated) && chunk.isPopulated()
                    && chunk.getProvider() != null) {
                this.server.getPluginManager().callEvent(new ChunkPopulateEvent(chunk));

                for (ChunkLoader loader : this.getChunkLoaders(x, z)) {
                    loader.onChunkPopulated(chunk);
                }
            }
        } else if ((queuedGen = this.chunkGenerationQueue.remove(index)) || this.chunkPopulationLock.remove(index)) {
            if (queuedGen) {
                this.chunkPopulationLock.remove(index);
            }
            chunk.setProvider(this.provider);
            this.setChunk(x, z, chunk, false);
        } else {
            chunk.setProvider(this.provider);
            this.setChunk(x, z, chunk, false);
        }
    }

    @Override
    public void setChunk(int chunkX, int chunkZ) {
        this.setChunk(chunkX, chunkZ, null);
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, BaseFullChunk chunk) {
        this.setChunk(chunkX, chunkZ, chunk, true);
    }

    public void setChunk(int chunkX, int chunkZ, BaseFullChunk chunk, boolean unload) {
        if (chunk == null) {
            return;
        }

        long index = Level.chunkHash(chunkX, chunkZ);
        FullChunk oldChunk = this.getChunk(chunkX, chunkZ, false);

        if (oldChunk != chunk) {
            if (unload && oldChunk != null) {
                this.unloadChunk(chunkX, chunkZ, false, false);

                if (provider != null) {
                    this.provider.setChunk(chunkX, chunkZ, chunk);
                }
            } else {
                Map<Long, Entity> oldEntities = oldChunk != null ? oldChunk.getEntities() : Long2ObjectMaps.emptyMap();

                Map<Long, BlockEntity> oldBlockEntities = oldChunk != null ? oldChunk.getBlockEntities() : Long2ObjectMaps.emptyMap();

                if (!oldEntities.isEmpty()) {
                    Iterator<Map.Entry<Long, Entity>> iter = oldEntities.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<Long, Entity> entry = iter.next();
                        Entity entity = entry.getValue();
                        chunk.addEntity(entity);
                        if (oldChunk != null) {
                            iter.remove();
                            oldChunk.removeEntity(entity);
                            entity.chunk = chunk;
                        }
                    }
                }

                if (!oldBlockEntities.isEmpty()) {
                    Iterator<Map.Entry<Long, BlockEntity>> iter = oldBlockEntities.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<Long, BlockEntity> entry = iter.next();
                        BlockEntity blockEntity = entry.getValue();
                        chunk.addBlockEntity(blockEntity);
                        if (oldChunk != null) {
                            iter.remove();
                            oldChunk.removeBlockEntity(blockEntity);
                            blockEntity.chunk = chunk;
                        }
                    }
                }

                if (provider != null) {
                    this.provider.setChunk(chunkX, chunkZ, chunk);
                }
            }
        }

        chunk.setChanged();

        if (!this.isChunkInUse(index)) {
            this.unloadChunkRequest(chunkX, chunkZ);
        } else {
            for (ChunkLoader loader : this.getChunkLoaders(chunkX, chunkZ)) {
                loader.onChunkChanged(chunk);
            }
        }
    }

    public int getHighestBlockAt(int x, int z) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        return chunk == null ? 0 : chunk.getHighestBlockAt(x & 0x0f, z & 0x0f);
    }

    public BlockColor getMapColorAt(int x, int z) {
        int y = getHighestBlockAt(x, z);
        while (y > 1) {
            Block block = getBlock(x, y, z);
            BlockColor blockColor = block.getColor();
            if (blockColor.getAlpha() == 0x00) {
                y--;
            } else {
                return blockColor;
            }
        }
        return BlockColor.VOID_BLOCK_COLOR;
    }

    public boolean isChunkLoaded(int x, int z) {
        long index = Level.chunkHash(x, z);
        if (server.isPrimaryThread()) {
            for (int i = 0; i < 4; i++) {
                if (index == lastChunkPos[i]) {
                    if (lastChunk[i] != null) {
                        return true;
                    }
                    break;
                }
            }
        }
        return this.chunks.containsKey(index) || this.provider.isChunkLoaded(x, z);
    }

    private boolean areNeighboringChunksLoaded(long hash) {
        return this.provider.isChunkLoaded(hash + 1) &&
                this.provider.isChunkLoaded(hash - 1) &&
                this.provider.isChunkLoaded(hash + (1L << 32)) &&
                this.provider.isChunkLoaded(hash - (1L << 32));
    }

    public boolean isChunkGenerated(int x, int z) {
        FullChunk chunk = this.getChunk(x, z);
        return chunk != null && chunk.isGenerated();
    }

    public boolean isChunkPopulated(int x, int z) {
        FullChunk chunk = this.getChunk(x, z);
        return chunk != null && chunk.isPopulated();
    }

    public Position getSpawnLocation() {
        return getSpawnLocation(() -> null);
    }

    public Position getSpawnLocation(Supplier<Position> defaultPos) {
        if (provider == null) {
            return defaultPos.get();
        }

        return Position.fromObject(this.provider.getSpawn(), this);
    }

    public void setSpawnLocation(Vector3 pos) {
        Position previousSpawn = this.getSpawnLocation();
        this.provider.setSpawn(pos);
        this.server.getPluginManager().callEvent(new SpawnChangeEvent(this, previousSpawn));
        SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
        pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
        pk.x = pos.getFloorX();
        pk.y = pos.getFloorY();
        pk.z = pos.getFloorZ();
        for (Player p : getPlayers().values()) p.dataPacket(pk);
    }

    public boolean requestSubChunks(int x, int z, Player player) {
        int loaderId = player.getLoaderId();
        if (loaderId <= 0 || provider == null || this.provider.getChunk(x, z, false) == null) {
            return false;
        }
        long index = Level.chunkHash(x, z);
        this.subChunkSendQueue.computeIfAbsent(index, key -> new Int2ObjectOpenHashMap<>()).put(loaderId, player);
        this.chunkSendQueue.computeIfAbsent(index, key -> new Int2ObjectOpenHashMap<>());
        return true;
    }

    public void requestChunk(int x, int z, Player player) {
        Preconditions.checkState(player.getLoaderId() > 0, player.getName() + " has no chunk loader");
        long index = Level.chunkHash(x, z);
        this.chunkSendQueue.computeIfAbsent(index, key -> new Int2ObjectOpenHashMap<>()).put(player.getLoaderId(), player);
    }

    private void processChunkRequest() {
        Iterator<Long2ObjectMap.Entry<Int2ObjectMap<Player>>> it = this.chunkSendQueue.long2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            Long2ObjectMap.Entry<Int2ObjectMap<Player>> entry = it.next();
            long index = entry.getLongKey();
            if (!this.chunkSendTasks.add(index)) {
                continue;
            }
            boolean requestFullChunk = false;
            boolean requestSubChunks = false;
            int x = getHashX(index);
            int z = getHashZ(index);
            BaseFullChunk chunk = getChunk(x, z);
            if (chunk != null) {
                ChunkCachedData cachedData = chunk.getCachedData();
                ChunkPacketCache packetCache;
                if (cachedData != null && (packetCache = cachedData.getPacketCache()) != null) {
                    int subChunkCount = cachedData.getSubChunkCount();

                    Iterator<Player> iter = entry.getValue().values().iterator();
                    while (iter.hasNext()) {
                        Player player = iter.next();

                        if (!player.isConnected() || !player.usedChunks.containsKey(index)) {
                            iter.remove();
                            continue;
                        }

                        StaticVersion blockVersion = player.getBlockVersion();
                        if (blockVersion != null && !packetCache.hasRequested(blockVersion)) {
                            requestFullChunk = true;
                            continue;
                        }

                        if (player.isSubChunkRequestAvailable()) {
                            int protocol = player.getProtocol();
                            if (protocol >= 649) {
                                player.sendChunk(x, z, subChunkCount, cachedData, packetCache.getSubRequestModeFullChunkPacketUncompressed());
                            } else {
                                player.sendChunk(x, z, subChunkCount, cachedData, packetCache.getSubRequestModeFullChunkPacket());
                            }
                        } else {
                            if (blockVersion == null) {
                                iter.remove();
                                continue;
                            }

                            int protocol = player.getProtocol();
                            if (protocol >= 649) {
                                player.sendChunk(x, z, subChunkCount, cachedData, packetCache.getFullChunkPacketUncompressed(blockVersion));
                            } else {
                                player.sendChunk(x, z, subChunkCount, cachedData, packetCache.getFullChunkPacket(blockVersion));
                            }
                        }

                        iter.remove();
                    }

                    Int2ObjectMap<Player> loaders = this.subChunkSendQueue.get(index);
                    if (loaders != null) {
                        Iterator<Player> iterator = loaders.values().iterator();
                        while (iterator.hasNext()) {
                            Player player = iterator.next();

                            if (!player.isConnected()) {
                                iterator.remove();
                                continue;
                            }

                            StaticVersion blockVersion = player.getBlockVersion();
                            if (blockVersion == null) {
                                iterator.remove();
                                continue;
                            }

                            if (!packetCache.hasRequested(blockVersion)) {
                                requestSubChunks = true;
                                continue;
                            }

                            player.sendSubChunks(this.getDimension().ordinal(), x, z, cachedData.getSubChunkCount(), cachedData, cachedData.getHeightMapType(), cachedData.getHeightMapData());
                            iterator.remove();
                        }

                        if (!requestSubChunks) {
                            this.subChunkSendQueue.remove(index);
                        }
                    }

                    if (!requestFullChunk) {
                        it.remove();
                        this.chunkSendTasks.remove(index);

                        if (!requestSubChunks) {
                            continue;
                        }
                    }
                }
            }

            AsyncTask<?> task = this.provider.requestChunkTask(x, z);
            if (task != null) {
                this.server.getScheduler().scheduleAsyncTask(null, task);
            } else { // no such chunk
                if (requestFullChunk) {
                    it.remove();
                    this.chunkSendTasks.remove(index);
                }

                if (requestSubChunks) {
                    Int2ObjectMap<Player> loaders = this.subChunkSendQueue.remove(index);
                    if (loaders != null) {
                        for (Player player : loaders.values()) {
                            player.onSubChunkRequestFail(this.getDimension().ordinal(), x, z);
                        }
                    }
                }
            }
        }
    }

    public boolean isCacheChunks() {
        return cacheChunks;
    }

    /**
     * Chunk request callback on main thread
     * If this.cacheChunks == false, the ChunkPacketCache can be null;
     */
    public void chunkRequestCallback(long timestamp, int x, int z, ChunkCachedData cachedData, Map<StaticVersion, byte[]> fullChunkPayloads, byte[] subRequestModeFullChunkPayload, Map<StaticVersion, byte[][]> subChunkPayloads) {
        long index = Level.chunkHash(x, z);
        int subChunkCount = cachedData.getSubChunkCount();
        byte[] heightMapType = cachedData.getHeightMapType();
        byte[][] heightMapData = cachedData.getHeightMapData();

        if (this.provider != null) {
            BaseFullChunk chunk = getChunk(x, z, false);
            if (chunk != null && chunk.getChanges() <= timestamp) {
                chunk.setCachedData(cachedData);
            }
        }

        ChunkPacketCache packetCache = cachedData.getPacketCache();
        if (packetCache != null) {
            if (this.chunkSendTasks.remove(index)) {
                Int2ObjectMap<Player> loaders = this.chunkSendQueue.remove(index);
                if (loaders != null) {
                    for (Player player : loaders.values()) {
                        if (!player.isConnected()) {
                            continue;
                        }

                        if (!player.usedChunks.containsKey(index)) {
                            continue;
                        }

                        if (player.isSubChunkRequestAvailable()) {
                            int protocol = player.getProtocol();
                            if (protocol >= 649) {
                                player.sendChunk(x, z, subChunkCount, cachedData, packetCache.getSubRequestModeFullChunkPacketUncompressed());
                            } else {
                                player.sendChunk(x, z, subChunkCount, cachedData, packetCache.getSubRequestModeFullChunkPacket());
                            }
                        } else {
                            StaticVersion blockVersion = player.getBlockVersion();
                            if (blockVersion == null) {
                                continue;
                            }

                            DataPacket packet;
                            int protocol = player.getProtocol();
                            if (protocol >= 649) {
                                packet = packetCache.getFullChunkPacketUncompressed(blockVersion);
                            } else {
                                packet = packetCache.getFullChunkPacket(blockVersion);
                            }

                            if (packet == null) {
                                requestChunk(x, z, player);
                                continue;
                            }

                            player.sendChunk(x, z, subChunkCount, cachedData, packet);
                        }
                    }
                }
            }

            Int2ObjectMap<Player> loaders = this.subChunkSendQueue.remove(index);
            if (loaders != null) {
                for (Player player : loaders.values()) {
                    if (!player.isConnected()) {
                        continue;
                    }

                    StaticVersion blockVersion = player.getBlockVersion();
                    if (blockVersion == null) {
                        continue;
                    }

                    if (!fullChunkPayloads.containsKey(blockVersion)) {
                        requestSubChunks(x, z, player);
                        continue;
                    }

                    player.sendSubChunks(this.getDimension().ordinal(), x, z, subChunkCount, cachedData, heightMapType, heightMapData);
                }
            }

            return;
        }

        Int2ObjectMap<Player> chunkLoaders;
        if (this.chunkSendTasks.remove(index) && (chunkLoaders = this.chunkSendQueue.remove(index)) != null) {
            for (Player player : chunkLoaders.values()) {
                if (player.isConnected() && player.usedChunks.containsKey(index)) {
                    StaticVersion blockVersion = player.getBlockVersion();
                    byte[] data;
                    if (blockVersion != null) {
                        data = fullChunkPayloads.get(blockVersion);
                        if (data == null) {
                            requestChunk(x, z, player);
                            continue;
                        }
                    } else {
                        continue;
                    }

                    player.sendChunk(x, z, subChunkCount, cachedData, data, subRequestModeFullChunkPayload);
                }
            }
        }

        Int2ObjectMap<Player> loaders = this.subChunkSendQueue.remove(index);
        if (loaders != null) {
            for (Player player : loaders.values()) {
                if (!player.isConnected()) {
                    continue;
                }

                StaticVersion blockVersion = player.getBlockVersion();
                if (blockVersion == null) {
                    continue;
                }

                if (!fullChunkPayloads.containsKey(blockVersion)) {
                    requestSubChunks(x, z, player);
                    continue;
                }

                player.sendSubChunks(this.getDimension().ordinal(), x, z, subChunkCount, cachedData, subChunkPayloads, heightMapType, heightMapData);
            }
        }
    }

    public void removeEntity(Entity entity) {
        if (entity.getLevel() != this) {
            throw new LevelException("Invalid Entity level");
        }

        if (entity instanceof Player) {
            this.players.remove(entity.getId());
            this.checkSleep();

            this.onPlayerRemove((Player) entity);
        } else {
            entity.close();
        }

        this.entities.remove(entity.getId());
        this.updateEntities.remove(entity.getId());
    }

    public void removeEntityDirect(Entity entity) {
        if (entity.getLevel() != this) {
            throw new LevelException("Invalid Entity level");
        }

        if (entity instanceof Player) {
            this.players.remove(entity.getId());
            this.checkSleep();

            this.onPlayerRemove((Player) entity);
        }

        this.entities.remove(entity.getId());
        this.updateEntities.remove(entity.getId());
    }

    public void addEntity(Entity entity) {
        if (entity.getLevel() != this) {
            throw new LevelException("Invalid Entity level");
        }

        if (entity instanceof Player) {
            this.players.put(entity.getId(), (Player) entity);
        }
        this.entities.put(entity.getId(), entity);
    }

    public void addBlockEntity(BlockEntity blockEntity) {
        if (blockEntity.getLevel() != this) {
            throw new LevelException("Invalid Block Entity level");
        }
        blockEntities.put(blockEntity.getId(), blockEntity);
    }

    public void scheduleBlockEntityUpdate(BlockEntity entity) {
        Preconditions.checkNotNull(entity, "entity");
        Preconditions.checkArgument(entity.getLevel() == this, "BlockEntity is not in this level");
        if (!updateBlockEntities.contains(entity)) {
            updateBlockEntities.add(entity);
        }
    }

    public void removeBlockEntity(BlockEntity entity) {
        Preconditions.checkNotNull(entity, "entity");
        Preconditions.checkArgument(entity.getLevel() == this, "BlockEntity is not in this level");
        blockEntities.remove(entity.getId());
        updateBlockEntities.remove(entity);
    }

    public boolean isChunkInUse(int x, int z) {
        return isChunkInUse(Level.chunkHash(x, z));
    }

    public boolean isChunkInUse(long hash) {
        Int2ObjectMap<ChunkLoader> loaders = this.chunkLoaders.get(hash);
        return loaders != null && !loaders.isEmpty();
    }

    public boolean loadChunk(int x, int z) {
        return this.loadChunk(x, z, true);
    }

    public boolean loadChunk(int x, int z, boolean generate) {
        long index = Level.chunkHash(x, z);
        for (int i = 0; i < 4; i++) {
            if (index == lastChunkPos[i]) {
                if (lastChunk[i] != null) {
                    return true;
                }
                break;
            }
        }
        if (this.chunks.containsKey(index)) {
            return true;
        }
        return this.loadChunkInternal(index, x, z, generate);
    }

    private boolean loadChunkInternal(long index, int x, int z, boolean generate) {
        this.cancelUnloadChunkRequest(x, z);

        BaseFullChunk chunk = provider != null ? this.provider.getChunk(x, z, generate) : null;

        if (chunk == null) {
            if (generate) {
                if (provider == null) {
//                    throw new IllegalStateException("Level has been unloaded");
                    return false;
                }
                throw new IllegalStateException("Could not create new Chunk");
            }
            return false;
        }

        this.chunks.put(index, chunk);
        chunk.initChunk();

        if (chunk.getProvider() != null) {
            this.server.getPluginManager().callEvent(new ChunkLoadEvent(chunk, !chunk.isGenerated()));
        } else {
            this.unloadChunk(x, z, false);
            return false;
        }

        if (false && //TODO: light
                !chunk.isLightPopulated() && chunk.isPopulated() && this.getServer().getConfiguration().isLightUpdates()) {
            this.getServer().getScheduler().scheduleAsyncTask(null, new LightPopulationTask(this, chunk));
        }

        if (this.isChunkInUse(x, z)) {
            for (ChunkLoader loader : this.getChunkLoaders(x, z)) {
                loader.onChunkLoaded(chunk);
            }
        } else {
            this.unloadChunkRequest(x, z);
        }
        return true;
    }

    private void queueUnloadChunk(int x, int z) {
        long index = Level.chunkHash(x, z);
        this.unloadQueue.put(index, System.currentTimeMillis());
        this.chunkTickList.remove(index);
    }

    public boolean unloadChunkRequest(int x, int z) {
        return this.unloadChunkRequest(x, z, true);
    }

    public boolean unloadChunkRequest(int x, int z, boolean safe) {
        if ((safe && this.isChunkInUse(x, z)) || this.isSpawnChunk(x, z)) {
            return false;
        }

        this.queueUnloadChunk(x, z);

        return true;
    }

    public void cancelUnloadChunkRequest(int x, int z) {
        this.unloadQueue.remove(Level.chunkHash(x, z));
    }

    public boolean unloadChunk(int x, int z) {
        return this.unloadChunk(x, z, true);
    }

    public boolean unloadChunk(int x, int z, boolean safe) {
        return this.unloadChunk(x, z, safe, true);
    }

    public boolean unloadChunk(int x, int z, boolean safe, boolean trySave) {
        if (safe && this.isChunkInUse(x, z)) {
            return false;
        }

        if (!this.isChunkLoaded(x, z)) {
            return true;
        }

        long index = Level.chunkHash(x, z);

        BaseFullChunk chunk = this.getChunk(x, z);

        if (chunk != null && chunk.getProvider() != null) {
            ChunkUnloadEvent ev = new ChunkUnloadEvent(chunk);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }
        }

        try {
            if (chunk != null) {
                if (provider != null && trySave && this.getAutoSave()) {
                    boolean entities = false;
                    for (Entity e : chunk.getEntities().values()) {
                        if (e instanceof Player) {
                            continue;
                        }
                        entities = true;
                        break;
                    }

                    if (chunk.hasChanged() || !chunk.getBlockEntities().isEmpty() || entities) {
                        this.provider.setChunk(x, z, chunk);
                        this.provider.saveChunk(x, z);
                    }
                }

                for (ChunkLoader loader : this.getChunkLoaders(x, z)) {
                    loader.onChunkUnloaded(chunk);
                }
            }
            if (provider != null) {
                this.provider.unloadChunk(x, z, safe);
            }
        } catch (Exception e) {
            log.error(this.server.getLanguage().translate("nukkit.level.chunkUnloadError"), e);
        }

        for (int i = 0; i < 4; i++) {
            if (index == lastChunkPos[i]) {
//                lastChunkPos[i] = ChunkPosition.INVALID_CHUNK_POSITION;
                lastChunk[i] = null;
                break;
            }
        }
        this.chunks.remove(index);
        this.chunkTickList.remove(index);

        return true;
    }

    public boolean isSpawnChunk(int X, int Z) {
        if (this.provider == null) {
            return false;
        }

        Vector3 spawn = this.provider.getSpawn();
        return Math.abs(X - spawn.getChunkX()) <= 1 && Math.abs(Z - spawn.getChunkZ()) <= 1;
    }

    public Position getSafeSpawn() {
        return this.getSafeSpawn(null);
    }

    public Position getSafeSpawn(Vector3 spawn) {
        if (spawn == null || spawn.y <= heightRange.getMinY()) {
            spawn = this.getSpawnLocation();
        }

        if (spawn != null) {
            BlockVector3 v = spawn.asBlockVector3();
            FullChunk chunk = this.getChunk(v.getChunkX(), v.getChunkZ(), false);
            int x = v.x & 0x0f;
            int z = v.z & 0x0f;
            if (chunk != null && chunk.isGenerated()) {
                int y = Mth.clamp(Math.min(chunk.getHighestBlockAt(x, z), v.y), heightRange.getMinY() + 1, heightRange.getMaxY() - 1);
                boolean wasAir = chunk.getBlockId(0, x, y + 1, z) == Block.AIR;
                for (; y > heightRange.getMinY(); --y) {
                    int b = chunk.getFullBlock(0, x, y, z);
                    Block block = Block.fromFullId(b);
                    if (this.isFullBlock(block)) {
                        if (wasAir) {
                            y++;
                            break;
                        }
                    } else {
                        wasAir = true;
                    }
                }

                for (; y >= heightRange.getMinY() && y <= heightRange.getMaxY(); y++) {
                    int b = chunk.getFullBlock(0, x, y + 1, z);
                    Block block = Block.fromFullId(b);
                    if (!this.isFullBlock(block)) {
                        b = chunk.getFullBlock(0, x, y, z);
                        block = Block.fromFullId(b);
                        if (!this.isFullBlock(block)) {
                            return new Position(spawn.x, y, spawn.z, this);
                        }
                    } else {
                        ++y;
                    }
                }

                v.y = y;
            }

            return new Position(spawn.x, v.y, spawn.z, this);
        }

        return null;
    }

    public int getTime() {
        return (int) time;
    }

    public boolean isDaytime() {
        return this.skyLightSubtracted < 4;
    }

    public long getCurrentTick() {
        return this.levelCurrentTick;
    }

    public String getName() {
        if (provider == null) {
            return "";
        }
        return this.provider.getName();
    }

    public String getFolderName() {
        return this.folderName;
    }

    public void setTime(int time) {
        this.time = time;
        this.sendTime();
    }

    public void stopTime() {
        this.stopTime = true;
        this.gameRules.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        this.broadcastGameRules(gameRules);
        this.sendTime();
    }

    public void startTime() {
        this.stopTime = false;
        this.gameRules.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        this.broadcastGameRules(gameRules);
        this.sendTime();
    }

    public void broadcastGameRules() {
        this.broadcastGameRules(this.gameRules);
    }

    public void broadcastGameRules(GameRules gameRules) {
        if (gameRules != null) {
            GameRulesChangedPacket gameRulesChangedPacket = new GameRulesChangedPacket();
            gameRulesChangedPacket.gameRules = this.gameRules;
            Server.broadcastPacket(this.players.values(), gameRulesChangedPacket);
        }
    }

    @Override
    public long getSeed() {
        return this.provider.getSeed();
    }

    public void setSeed(int seed) {
        this.provider.setSeed(seed);
    }

    public boolean populateChunk(int x, int z) {
        return this.populateChunk(x, z, false);
    }

    public boolean populateChunk(int x, int z, boolean force) {
        if (this.chunkPopulationQueue.size() >= this.chunkPopulationQueueSize && !force) {
            return false;
        }

        long index = Level.chunkHash(x, z);
        if (this.chunkPopulationQueue.containsKey(index)) {
            return false;
        }

        BaseFullChunk chunk = this.getChunk(x, z, true);
        if (chunk == null) {
            return false;
        }

        boolean populate;
        if (!chunk.isPopulated()) {
            populate = true;
            for (int xx = -1; xx <= 1; ++xx) {
                for (int zz = -1; zz <= 1; ++zz) {
                    if (this.chunkPopulationLock.containsKey(Level.chunkHash(x + xx, z + zz))) {
                        populate = false;
                        break;
                    }
                }
            }

            if (populate) {
                if (!this.chunkPopulationQueue.putIfAbsent(index, true)) {
                    for (int xx = -1; xx <= 1; ++xx) {
                        for (int zz = -1; zz <= 1; ++zz) {
                            this.chunkPopulationLock.put(Level.chunkHash(x + xx, z + zz), true);
                        }
                    }

                    PopulationTask task = new PopulationTask(this, chunk);
                    this.server.getScheduler().scheduleAsyncTask(null, task);
                }
            }
            return false;
        }

        return true;
    }

    public void generateChunk(int x, int z) {
        this.generateChunk(x, z, false);
    }

    public void generateChunk(int x, int z, boolean force) {
        if (this.chunkGenerationQueue.size() >= this.chunkGenerationQueueSize && !force) {
            return;
        }

        long index = Level.chunkHash(x, z);
        if (!this.chunkGenerationQueue.putIfAbsent(index, true)) {
            GenerationTask task = new GenerationTask(this, this.getChunk(x, z, true));
            this.server.getScheduler().scheduleAsyncTask(null, task);
        }
    }

    public void regenerateChunk(int x, int z) {
        this.unloadChunk(x, z, false, false);

        this.cancelUnloadChunkRequest(x, z);

        BaseFullChunk chunk = provider.getEmptyChunk(x, z);
        provider.setChunk(x, z, chunk);

        this.generateChunk(x, z, true);
    }

    public void doChunkGarbageCollection() {
        // remove all invaild block entities.
        List<BlockEntity> toClose = new ObjectArrayList<>();
        for (BlockEntity anBlockEntity : blockEntities.values()) {
            if (anBlockEntity == null)
                continue;
            if (anBlockEntity.isBlockEntityValid())
                continue;
            toClose.add(anBlockEntity);
        }
        for (BlockEntity be : toClose) {
            be.close();
        }

        for (long index : this.chunks.keySet()) {
            if (!this.unloadQueue.containsKey(index)) {
                int X = getHashX(index);
                int Z = getHashZ(index);
                if (!this.isSpawnChunk(X, Z)) {
                    this.unloadChunkRequest(X, Z, true);
                }
            }
        }

        for (FullChunk chunk : new ObjectArrayList<>(this.provider.getLoadedChunks().values())) {
            if (!this.chunks.containsKey(Level.chunkHash(chunk.getX(), chunk.getZ()))) {
                this.provider.unloadChunk(chunk.getX(), chunk.getZ(), false);
            }
        }

        this.provider.doGarbageCollection();
    }

    public void unloadChunks() {
        this.unloadChunks(false);
    }

    public void unloadChunks(boolean force) {
        if (!this.unloadQueue.isEmpty()) {
            int maxUnload = 96;
            long now = System.currentTimeMillis();

            for (long index : new ObjectArrayList<>(this.unloadQueue.keySet())) {
                long time = this.unloadQueue.get(index);

                int X = getHashX(index);
                int Z = getHashZ(index);

                if (!force) {
                    if (maxUnload <= 0) {
                        break;
                    } else if (time > (now - 30000)) {
                        continue;
                    }
                }

                if (this.unloadChunk(X, Z, true)) {
                    this.unloadQueue.remove(index);
                    --maxUnload;
                }
            }
        }
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) throws Exception {
        this.server.getLevelMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) throws Exception {
        return this.server.getLevelMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) throws Exception {
        return this.server.getLevelMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) throws Exception {
        this.server.getLevelMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    public void addEntityMotion(int chunkX, int chunkZ, long entityId, double x, double y, double z) {
        SetEntityMotionPacket pk = new SetEntityMotionPacket();
        pk.eid = entityId;
        pk.motionX = (float) x;
        pk.motionY = (float) y;
        pk.motionZ = (float) z;
        pk.setChannel(DataPacket.CHANNEL_MOVING);

        this.addChunkPacket(chunkX, chunkZ, pk);
    }

    public void addEntityMovement(int chunkX, int chunkZ, long entityId, double x, double y, double z, double yaw, double pitch, double headYaw) {
        MoveEntityPacket pk = new MoveEntityPacket();
        pk.eid = entityId;
        pk.x = (float) x;
        pk.y = (float) y;
        pk.z = (float) z;
        pk.yaw = (float) yaw;
        pk.headYaw = (float) headYaw;
        pk.pitch = (float) pitch;
        pk.setChannel(DataPacket.CHANNEL_MOVING);

        this.addChunkPacket(chunkX, chunkZ, pk);
    }

    public boolean isRaining() {
        return this.raining;
    }

    public boolean setRaining(boolean raining) {
        Random random = ThreadLocalRandom.current();
        return this.setRaining(raining, random.nextInt(50000) + 10000);
    }

    public boolean setRaining(boolean raining, int intensity) {
        WeatherChangeEvent ev = new WeatherChangeEvent(this, raining, intensity);
        this.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return false;
        }

        this.raining = raining;
        this.rainingIntensity = ev.getIntensity();

        LevelEventPacket pk = new LevelEventPacket();
        // These numbers are from Minecraft

        Random random = ThreadLocalRandom.current();
        if (raining) {
            pk.evid = LevelEventPacket.EVENT_START_RAIN;
            pk.data = this.rainingIntensity;
            setRainTime(random.nextInt(12000) + 12000);
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_RAIN;
            setRainTime(random.nextInt(168000) + 12000);
        }

        Server.broadcastPacket(this.getPlayers().values(), pk);

        return true;
    }

    public int getRainTime() {
        return this.rainTime;
    }

    public void setRainTime(int rainTime) {
        this.rainTime = rainTime;
    }

    public boolean isThundering() {
        return isRaining() && this.thundering;
    }

    public boolean setThundering(boolean thundering) {
        ThunderChangeEvent ev = new ThunderChangeEvent(this, thundering);
        this.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return false;
        }

        if (thundering && !isRaining()) {
            setRaining(true);
        }

        this.thundering = thundering;

        Random random = ThreadLocalRandom.current();
        LevelEventPacket pk = new LevelEventPacket();
        // These numbers are from Minecraft
        if (thundering) {
            pk.evid = LevelEventPacket.EVENT_START_THUNDER;
            pk.data = random.nextInt(50000) + 10000;
            setThunderTime(random.nextInt(12000) + 3600);
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_THUNDER;
            setThunderTime(random.nextInt(168000) + 12000);
        }

        Server.broadcastPacket(this.getPlayers().values(), pk);

        return true;
    }

    public int getThunderTime() {
        return this.thunderTime;
    }

    public void setThunderTime(int thunderTime) {
        this.thunderTime = thunderTime;
    }

    public void sendWeather(Player[] players) {
        if (players == null) {
            players = this.getPlayers().values().toArray(new Player[0]);
        }

        LevelEventPacket pk = new LevelEventPacket();

        Random random = ThreadLocalRandom.current();
        if (this.isRaining()) {
            pk.evid = LevelEventPacket.EVENT_START_RAIN;
            pk.data = this.rainingIntensity;
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_RAIN;
        }

        Server.broadcastPacket(players, pk);

        if (this.isThundering()) {
            pk.evid = LevelEventPacket.EVENT_START_THUNDER;
            pk.data = random.nextInt(50000) + 10000;
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_THUNDER;
        }

        Server.broadcastPacket(players, pk);
    }

    public void sendWeather(Player player) {
        if (player != null) {
            this.sendWeather(new Player[]{player});
        }
    }

    public void sendWeather(Collection<Player> players) {
        if (players == null) {
            players = this.getPlayers().values();
        }
        this.sendWeather(players.toArray(new Player[0]));
    }

    public Dimension getDimension() {
        return dimension;
    }

    public boolean canBlockSeeSky(Vector3 pos) {
        return this.canBlockSeeSky(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
    }

    public boolean canBlockSeeSky(BlockVector3 pos) {
        return this.canBlockSeeSky(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean canBlockSeeSky(int x, int y, int z) {
        return this.getHighestBlockAt(x, z) < y;
    }

    public int getStrongPower(Vector3 pos, BlockFace direction) {
        return this.getBlock(pos).getStrongPower(direction);
    }

    public int getStrongPower(Vector3 pos) {
        int i = 0;

        for (BlockFace face : BlockFace.getValues()) {
            i = Math.max(i, this.getStrongPower(pos.getSideVec(face), face));

            if (i >= 15) {
                return i;
            }
        }

        return i;
//        i = Math.max(i, this.getStrongPower(pos.down(), BlockFace.DOWN));
//
//        if (i >= 15) {
//            return i;
//        } else {
//            i = Math.max(i, this.getStrongPower(pos.up(), BlockFace.UP));
//
//            if (i >= 15) {
//                return i;
//            } else {
//                i = Math.max(i, this.getStrongPower(pos.north(), BlockFace.NORTH));
//
//                if (i >= 15) {
//                    return i;
//                } else {
//                    i = Math.max(i, this.getStrongPower(pos.south(), BlockFace.SOUTH));
//
//                    if (i >= 15) {
//                        return i;
//                    } else {
//                        i = Math.max(i, this.getStrongPower(pos.west(), BlockFace.WEST));
//
//                        if (i >= 15) {
//                            return i;
//                        } else {
//                            i = Math.max(i, this.getStrongPower(pos.east(), BlockFace.EAST));
//                            return i >= 15 ? i : i;
//                        }
//                    }
//                }
//            }
//        }
    }

    public boolean isSidePowered(Vector3 pos, BlockFace face) {
        return this.getRedstonePower(pos, face) > 0;
    }

    public int getRedstonePower(Vector3 pos, BlockFace face) {
        Block block;

        if (pos instanceof Block) {
            block = (Block) pos;
//            pos = pos.add(0);
        } else {
            block = this.getBlock(pos);
        }

        return block.isNormalBlock() ? this.getStrongPower(pos) : block.getWeakPower(face);
    }

    public boolean isBlockPowered(Vector3 pos) {
        for (BlockFace face : BlockFace.getValues()) {
            if (this.getRedstonePower(pos.getSide(face), face) > 0) {
                return true;
            }
        }

        return false;
    }

    public int isBlockIndirectlyGettingPowered(Vector3 pos) {
        int power = 0;

        for (BlockFace face : BlockFace.getValues()) {
            int blockPower = this.getRedstonePower(pos.getSide(face), face);

            if (blockPower >= 15) {
                return 15;
            }

            if (blockPower > power) {
                power = blockPower;
            }
        }

        return power;
    }

    public boolean isAreaLoaded(AxisAlignedBB bb) {
        if (bb.getMaxY() < heightRange.getMinY() || bb.getMinY() >= heightRange.getMaxY()) {
            return false;
        }
        int minX = Mth.floor(bb.getMinX()) >> 4;
        int minZ = Mth.floor(bb.getMinZ()) >> 4;
        int maxX = Mth.floor(bb.getMaxX()) >> 4;
        int maxZ = Mth.floor(bb.getMaxZ()) >> 4;

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                if (!this.isChunkLoaded(x, z)) {
                    return false;
                }
            }
        }

        return true;
    }

    public int getUpdateLCG() {
        return (this.updateLCG = (this.updateLCG * 3) + LCG_CONSTANT);
    }

    public boolean createPortal(Block target, CreateReason reason) {
        if (this.dimension == Dimension.END) return false;
        int maxPortalSize = 23;
        final int targX = target.getFloorX();
        final int targY = target.getFloorY();
        final int targZ = target.getFloorZ();
        //check if there's air above (at least 3 blocks)
        for (int i = 1; i < 4; i++) {
            if (this.getBlockIdAt(0, targX, targY + i, targZ) != BlockID.AIR) {
                return false;
            }
        }
        int sizePosX = 0;
        int sizeNegX = 0;
        int sizePosZ = 0;
        int sizeNegZ = 0;
        for (int i = 1; i < maxPortalSize; i++) {
            if (this.getBlockIdAt(0, targX + i, targY, targZ) == BlockID.OBSIDIAN) {
                sizePosX++;
            } else {
                break;
            }
        }
        for (int i = 1; i < maxPortalSize; i++) {
            if (this.getBlockIdAt(0, targX - i, targY, targZ) == BlockID.OBSIDIAN) {
                sizeNegX++;
            } else {
                break;
            }
        }
        for (int i = 1; i < maxPortalSize; i++) {
            if (this.getBlockIdAt(0, targX, targY, targZ + i) == BlockID.OBSIDIAN) {
                sizePosZ++;
            } else {
                break;
            }
        }
        for (int i = 1; i < maxPortalSize; i++) {
            if (this.getBlockIdAt(0, targX, targY, targZ - i) == BlockID.OBSIDIAN) {
                sizeNegZ++;
            } else {
                break;
            }
        }
        //plus one for target block
        int sizeX = sizePosX + sizeNegX + 1;
        int sizeZ = sizePosZ + sizeNegZ + 1;
        if (sizeX >= 2 && sizeX <= maxPortalSize) {
            //start scan from 1 block above base
            //find pillar or end of portal to start scan
            int scanX = targX;
            int scanY = targY + 1;
            int scanZ = targZ;
            for (int i = 0; i < sizePosX + 1; i++) {
                //this must be air
                if (this.getBlockIdAt(0, scanX + i, scanY, scanZ) != BlockID.AIR) {
                    return false;
                }
                if (this.getBlockIdAt(0, scanX + i + 1, scanY, scanZ) == BlockID.OBSIDIAN) {
                    scanX += i;
                    break;
                }
            }
            //make sure that the above loop finished
            if (this.getBlockIdAt(0, scanX + 1, scanY, scanZ) != BlockID.OBSIDIAN) {
                return false;
            }

            int innerWidth = 0;
            LOOP:
            for (int i = 0; i < maxPortalSize - 2; i++) {
                int id = this.getBlockIdAt(0, scanX - i, scanY, scanZ);
                switch (id) {
                    case BlockID.AIR:
                        innerWidth++;
                        break;
                    case BlockID.OBSIDIAN:
                        break LOOP;
                    default:
                        return false;
                }
            }
            int innerHeight = 0;
            LOOP:
            for (int i = 0; i < maxPortalSize - 2; i++) {
                int id = this.getBlockIdAt(0, scanX, scanY + i, scanZ);
                switch (id) {
                    case BlockID.AIR:
                        innerHeight++;
                        break;
                    case BlockID.OBSIDIAN:
                        break LOOP;
                    default:
                        return false;
                }
            }
            if (!(innerWidth <= maxPortalSize - 2
                    && innerWidth >= 2
                    && innerHeight <= maxPortalSize - 2
                    && innerHeight >= 3))   {
                return false;
            }

            for (int height = 0; height < innerHeight + 1; height++)    {
                if (height == innerHeight) {
                    for (int width = 0; width < innerWidth; width++) {
                        if (this.getBlockIdAt(0, scanX - width, scanY + height, scanZ) != BlockID.OBSIDIAN) {
                            return false;
                        }
                    }
                } else {
                    if (this.getBlockIdAt(0, scanX + 1, scanY + height, scanZ) != BlockID.OBSIDIAN
                            || this.getBlockIdAt(0, scanX - innerWidth, scanY + height, scanZ) != BlockID.OBSIDIAN) {
                        return false;
                    }

                    for (int width = 0; width < innerWidth; width++) {
                        if (this.getBlockIdAt(0, scanX - width, scanY + height, scanZ) != BlockID.AIR) {
                            return false;
                        }
                    }
                }
            }

            PortalCreateEvent event = new PortalCreateEvent(this, target, reason);
            event.call();
            if (event.isCancelled()) {
                return false;
            }

            for (int height = 0; height < innerHeight; height++)    {
                for (int width = 0; width < innerWidth; width++)    {
                    this.setBlock(new Vector3(scanX - width, scanY + height, scanZ), Block.get(BlockID.PORTAL), true);
                }
            }

            this.addLevelSoundEvent(target, LevelSoundEventPacket.SOUND_IGNITE);
            return true;
        } else if (sizeZ >= 2 && sizeZ <= maxPortalSize) {
            //start scan from 1 block above base
            //find pillar or end of portal to start scan
            int scanX = targX;
            int scanY = targY + 1;
            int scanZ = targZ;
            for (int i = 0; i < sizePosZ + 1; i++) {
                //this must be air
                if (this.getBlockIdAt(0, scanX, scanY, scanZ + i) != BlockID.AIR) {
                    return false;
                }
                if (this.getBlockIdAt(0, scanX, scanY, scanZ + i + 1) == BlockID.OBSIDIAN) {
                    scanZ += i;
                    break;
                }
            }
            //make sure that the above loop finished
            if (this.getBlockIdAt(0, scanX, scanY, scanZ + 1) != BlockID.OBSIDIAN) {
                return false;
            }

            int innerWidth = 0;
            LOOP:
            for (int i = 0; i < maxPortalSize - 2; i++) {
                int id = this.getBlockIdAt(0, scanX, scanY, scanZ - i);
                switch (id) {
                    case BlockID.AIR:
                        innerWidth++;
                        break;
                    case BlockID.OBSIDIAN:
                        break LOOP;
                    default:
                        return false;
                }
            }
            int innerHeight = 0;
            LOOP:
            for (int i = 0; i < maxPortalSize - 2; i++) {
                int id = this.getBlockIdAt(0, scanX, scanY + i, scanZ);
                switch (id) {
                    case BlockID.AIR:
                        innerHeight++;
                        break;
                    case BlockID.OBSIDIAN:
                        break LOOP;
                    default:
                        return false;
                }
            }
            if (!(innerWidth <= maxPortalSize - 2
                    && innerWidth >= 2
                    && innerHeight <= maxPortalSize - 2
                    && innerHeight >= 3))   {
                return false;
            }

            for (int height = 0; height < innerHeight + 1; height++)    {
                if (height == innerHeight) {
                    for (int width = 0; width < innerWidth; width++) {
                        if (this.getBlockIdAt(0, scanX, scanY + height, scanZ - width) != BlockID.OBSIDIAN) {
                            return false;
                        }
                    }
                } else {
                    if (this.getBlockIdAt(0, scanX, scanY + height, scanZ + 1) != BlockID.OBSIDIAN
                            || this.getBlockIdAt(0, scanX, scanY + height, scanZ - innerWidth) != BlockID.OBSIDIAN) {
                        return false;
                    }

                    for (int width = 0; width < innerWidth; width++) {
                        if (this.getBlockIdAt(0, scanX, scanY + height, scanZ - width) != BlockID.AIR) {
                            return false;
                        }
                    }
                }
            }

            PortalCreateEvent event = new PortalCreateEvent(this, target, reason);
            event.call();
            if (event.isCancelled()) {
                return false;
            }

            for (int height = 0; height < innerHeight; height++)    {
                for (int width = 0; width < innerWidth; width++)    {
                    this.setBlock(new Vector3(scanX, scanY + height, scanZ - width), Block.get(BlockID.PORTAL), true);
                }
            }

            this.addLevelSoundEvent(target, LevelSoundEventPacket.SOUND_IGNITE);
            return true;
        }

        return false;
    }

    public boolean isRedstoneEnabled() {
        return this.server.isRedstoneEnabled() && this.redstoneEnabled;
    }

    public void setRedstoneEnabled(boolean redstoneEnabled) {
        this.redstoneEnabled = redstoneEnabled;
    }

    public boolean isExtinguishFireIgnoreGameRule() {
        return this.extinguishFireIgnoreGameRule;
    }

    public void setExtinguishFireIgnoreGameRule(boolean ignore) {
        this.extinguishFireIgnoreGameRule = ignore;
    }

    public boolean isNewArmorMechanics() {
        return newArmorMechanics;
    }

    public void setNewArmorMechanics(boolean enable) {
        this.newArmorMechanics = enable;
    }

    public static BatchPacket getChunkCacheFromData(int x, int z, int subChunkCount, byte[] payload) {
        return getChunkCacheFromData(x, z, subChunkCount, 0, payload);
    }

    public static BatchPacket getChunkCacheFromData(int x, int z, int subChunkCount, int subChunkRequestLimit, byte[] payload) {
        LevelChunkPacket packet = new LevelChunkPacket();
        packet.chunkX = x;
        packet.chunkZ = z;
        packet.subChunkCount = subChunkCount;
        packet.subChunkRequestLimit = subChunkRequestLimit;
        packet.data = payload;
        packet.tryEncode();

        BatchPacket batch = new BatchPacket();
        byte[][] batchPayload = new byte[2][];
        byte[] buf = packet.getBuffer();
        batchPayload[0] = Binary.writeUnsignedVarInt(buf.length);
        batchPayload[1] = buf;
        byte[] data = Binary.appendBytes(batchPayload);
        try {
            batch.payload = Network.deflateRaw(data, Server.getInstance().networkCompressionLevel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        batch.tracks = new Track[]{new Track(packet.pid(), packet.getCount())};
        return batch;
    }

    public static BatchPacket getSubChunkCacheFromData(SubChunkPacket packet, int dimension, int subChunkX, int subChunkY, int subChunkZ, byte[] payload, byte heightMapType, byte[] heightMap) {
        packet.dimension = dimension;
        packet.subChunkX = subChunkX;
        packet.subChunkY = subChunkY;
        packet.subChunkZ = subChunkZ;
        packet.data = payload;
        packet.heightMapType = heightMapType;
        packet.heightMap = heightMap;
        packet.tryEncode();

        BatchPacket batch = new BatchPacket();
        byte[][] batchPayload = new byte[2][];
        byte[] buf = packet.getBuffer();
        batchPayload[0] = Binary.writeUnsignedVarInt(buf.length);
        batchPayload[1] = buf;
        byte[] data = Binary.appendBytes(batchPayload);
        try {
            batch.payload = Network.deflateRaw(data, Server.getInstance().networkCompressionLevel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        batch.tracks = new Track[]{new Track(packet.pid(), packet.getCount())};
        return batch;
    }

    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public HeightRange getHeightRange() {
        return heightRange;
    }

    public List<Block> getBlocks(AxisAlignedBB bb) {
        return getBlocks(bb, true);
    }

    public List<Block> getBlocks(AxisAlignedBB bb, boolean load) {
        int minX = Mth.floor(Math.min(bb.getMinX(), bb.getMaxX()));
        int minY = Mth.floor(Math.min(bb.getMinY(), bb.getMaxY()));
        int minZ = Mth.floor(Math.min(bb.getMinZ(), bb.getMaxZ()));
        int maxX = Mth.floor(Math.max(bb.getMinX(), bb.getMaxX()));
        int maxY = Mth.floor(Math.max(bb.getMinY(), bb.getMaxY()));
        int maxZ = Mth.floor(Math.max(bb.getMinZ(), bb.getMaxZ()));

        List<Block> blocks = new ObjectArrayList<>();
        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    blocks.add(getBlock(x, y, z, load));
                }
            }
        }
        return blocks;
    }

    public boolean containsBlock(AxisAlignedBB bb, Predicate<Block> predicate) {
        return containsBlock(bb, predicate, true);
    }

    public boolean containsBlock(AxisAlignedBB bb, Predicate<Block> predicate, boolean load) {
        int minX = Mth.floor(Math.min(bb.getMinX(), bb.getMaxX()));
        int minY = Mth.floor(Math.min(bb.getMinY(), bb.getMaxY()));
        int minZ = Mth.floor(Math.min(bb.getMinZ(), bb.getMaxZ()));
        int maxX = Mth.floor(Math.max(bb.getMinX(), bb.getMaxX()));
        int maxY = Mth.floor(Math.max(bb.getMinY(), bb.getMaxY()));
        int maxZ = Mth.floor(Math.max(bb.getMinZ(), bb.getMaxZ()));

        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    if (predicate.test(getBlock(x, y, z, load))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<Block> getExtraBlocks(AxisAlignedBB bb) {
        return getExtraBlocks(bb, true);
    }

    public List<Block> getExtraBlocks(AxisAlignedBB bb, boolean load) {
        int minX = Mth.floor(Math.min(bb.getMinX(), bb.getMaxX()));
        int minY = Mth.floor(Math.min(bb.getMinY(), bb.getMaxY()));
        int minZ = Mth.floor(Math.min(bb.getMinZ(), bb.getMaxZ()));
        int maxX = Mth.floor(Math.max(bb.getMinX(), bb.getMaxX()));
        int maxY = Mth.floor(Math.max(bb.getMinY(), bb.getMaxY()));
        int maxZ = Mth.floor(Math.max(bb.getMinZ(), bb.getMaxZ()));

        List<Block> blocks = new ObjectArrayList<>();
        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    blocks.add(getExtraBlock(x, y, z, load));
                }
            }
        }
        return blocks;
    }

    public boolean isAutoCompaction() {
        return autoCompaction && autoSave;
    }

    public void setAutoCompaction(boolean autoCompaction) {
        this.autoCompaction = autoCompaction;
    }

    @Nullable
    private BaseFullChunk getChunkFromCache(boolean isMainThread, long chunkIndex) {
        if (!isMainThread) {
            return this.chunks.get(chunkIndex);
        }

        for (int i = 0; i < CHUNK_CACHE_SIZE; i++) {
            if (chunkIndex != this.lastChunkPos[i]) {
                continue;
            }

            BaseFullChunk chunk = this.lastChunk[i];
            if (chunk != null) {
                return chunk;
            }
        }

        BaseFullChunk chunk = this.chunks.get(chunkIndex);
        if (chunk != null) {
            this.storeChunkInCache(chunkIndex, chunk);
        }
        return chunk;
    }

    private void storeChunkInCache(long chunkIndex, BaseFullChunk chunk) {
        for (int i = CHUNK_CACHE_SIZE - 1; i > 0; i--) {
            int newer = i - 1;
            this.lastChunkPos[i] = this.lastChunkPos[newer];
            this.lastChunk[i] = this.lastChunk[newer];
        }

        this.lastChunkPos[0] = chunkIndex;
        this.lastChunk[0] = chunk;
    }

    public Long2ObjectMap<Entity> getEntityUpdateQueue() {
        return updateEntities;
    }

    public Long2IntMap getChunkTickList() {
        return chunkTickList;
    }

    public Long2LongMap getChunkUnloadQueue() {
        return unloadQueue;
    }

    public void onPlayerAdd(Player player) {
        StaticVersion blockVersion = player.getBlockVersion();
        if (blockVersion != null) {
            requestChunkVersions.computeIfAbsent(blockVersion, key -> new LongOpenHashSet()).add(player.getId());
        }
    }

    public void onPlayerRemove(Player player) {
        StaticVersion blockVersion = player.getBlockVersion();
        if (blockVersion != null) {
            LongSet players = requestChunkVersions.get(blockVersion);
            if (players != null) {
                players.remove(player.getId());
                if (players.isEmpty()) {
                    requestChunkVersions.remove(blockVersion);
                }
            }
        }
    }

    public Map<StaticVersion, LongSet> getRequestChunkVersions() {
        return requestChunkVersions;
    }

    public void setDisableChunkTick(boolean disable) {
        disableChunkTick = disable;
    }

    public int addCallbackBlockSet(Consumer<Block> consumer) {
        int id = callbackIdCounter.incrementAndGet();
        callbackBlockSet.put(id, consumer);
        return id;
    }

    public void removeCallbackBlockSet(int id) {
        callbackBlockSet.remove(id);
    }

    public int addCallbackChunkPacketSend(BiConsumer<Long, DataPacket> consumer) {
        int id = callbackIdCounter.incrementAndGet();
        callbackChunkPacketSend.put(id, consumer);
        return id;
    }

    public void removeCallbackChunkPacketSend(int id) {
        callbackChunkPacketSend.remove(id);
    }

    /**
     * @param x local x
     * @param y world y
     * @param z local z
     * @param light light level
     */
    private record LightUpdateEntry(int x, int y, int z, int light) {
    }

    private static class LazyHolder {
        static final Item INVALID_ITEM = new Item(10000); //HACK
    }
}
