package cn.nukkit;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.*;
import cn.nukkit.command.*;
import cn.nukkit.command.exceptions.CommandExceptions;
import cn.nukkit.console.NukkitConsole;
import cn.nukkit.data.BlockItemConverter;
import cn.nukkit.data.ItemIdMap;
import cn.nukkit.data.ServerConfiguration;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.entity.Entities;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.attribute.Attribute;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.level.LevelInitEvent;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.event.player.SendPlayerListDataEvent;
import cn.nukkit.event.server.BatchPacketsEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.inventory.CraftingManager;
import cn.nukkit.inventory.Recipe;
import cn.nukkit.item.Item;
import cn.nukkit.item.armortrim.TrimMaterials;
import cn.nukkit.item.armortrim.TrimPatterns;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.LevelCreationOptions;
import cn.nukkit.level.Position;
import cn.nukkit.level.biome.Biomes;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.LevelProviderManager;
import cn.nukkit.level.format.LevelProviderManager.LevelProviderHandle;
import cn.nukkit.level.format.leveldb.LevelDB.DbInitData;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.Generators;
import cn.nukkit.math.Mth;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.metadata.EntityMetadataStore;
import cn.nukkit.metadata.LevelMetadataStore;
import cn.nukkit.metadata.PlayerMetadataStore;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.CompressBatchedTask;
import cn.nukkit.network.Network;
import cn.nukkit.network.RakNetInterface;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.BatchPacket.Track;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PlayerListPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.query.QueryHandler;
import cn.nukkit.network.rcon.RCON;
import cn.nukkit.permission.BanEntry;
import cn.nukkit.permission.BanList;
import cn.nukkit.permission.DefaultPermissions;
import cn.nukkit.permission.Permissible;
import cn.nukkit.plugin.JavaPluginLoader;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginLoadOrder;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.plugin.service.NKServiceManager;
import cn.nukkit.plugin.service.ServiceManager;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import cn.nukkit.resourcepacks.ResourcePackManager;
import cn.nukkit.scheduler.FileWriteTask;
import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.utils.*;
import cn.nukkit.utils.bugreport.ExceptionHandler;
import com.dosse.upnp.UPnP;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;

import javax.annotation.Nullable;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.Deflater;

/**
 * @author MagicDroidX
 * @author Box
 */
@Log4j2
public class Server {
    private static final boolean FORCE_ASYNC_SAVE_PLAYER_DATA = !Boolean.getBoolean("easecation.debugging");

    public static final String BROADCAST_CHANNEL_ADMINISTRATIVE = "nukkit.broadcast.admin";
    public static final String BROADCAST_CHANNEL_USERS = "nukkit.broadcast.user";

    private static Server instance = null;

    private final BanList banByName;

    private final BanList banByIP;

    private final Config operators;

    private final Config whitelist;

    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    private volatile boolean hasStopped = false;

    private final PluginManager pluginManager;

    private final ServerScheduler scheduler;

    private int tickCounter;

    private long nextTick;

    private final float[] tickAverage = {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
    private float tpsAverage = 20;

    private final float[] useAverage = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private float tickUsageAverage;

    private float maxTick = 20;
    private float tps = 20;

    private float maxUse = 0;
    private float tickUsage;
    private long tickUseNano;

    private int sendUsageTicker = 0;

    private final NukkitConsole console;
    private final ConsoleThread consoleThread;

    private final SimpleCommandMap commandMap;

    @Setter
    private CraftingManager craftingManager;

    private final ResourcePackManager resourcePackManager;

    private final ConsoleCommandSender consoleSender;

    private int maxPlayers;

    private boolean autoSave;
    private boolean scheduledAutoSave = true;

    private boolean redstoneEnabled;

    private RCON rcon;

    private final EntityMetadataStore entityMetadata;

    private final PlayerMetadataStore playerMetadata;

    private final LevelMetadataStore levelMetadata;

    private final Network network;

    private final boolean networkCompressionAsync;
    public int networkCompressionLevel;
    private final int networkZlibProvider;

    private final boolean autoTickRate;
    private final int autoTickRateLimit;
    private final boolean alwaysTickPlayers;
    private final int baseTickRate;
    private final boolean allowFlight;

    private int autoSaveTicker = 0;
    private int autoSaveTicks = 6000;

    private final boolean autoCompaction;
    private int autoCompactionTicks;

    private final BaseLang baseLang;

    private final boolean forceLanguage;

    private final UUID serverID;

    private final String filePath;
    private final String dataPath;
    private final String pluginPath;

//    private final Set<UUID> uniquePlayers = new ObjectOpenHashSet<>();

    private boolean upnpEnabled;

    private QueryHandler queryHandler;

    private QueryRegenerateEvent queryRegenerateEvent;

    @Getter
    private final ServerConfiguration configuration;
    private final Config properties;
    private final Config config;

    private final Map<InetSocketAddress, Player> players = new ConcurrentHashMap<>();

    private final Map<UUID, Player> playerList = new ConcurrentHashMap<>();

    private final Map<Integer, Level> levels = new ConcurrentHashMap<>();

    private final ServiceManager serviceManager = new NKServiceManager();

    private Level defaultLevel = null;
    @Nullable
    private Level tickingLevel;

    private final Thread currentThread;

    private Watchdog watchdog;

    private final boolean enableJmxMonitoring;
    /**
     * 过去 100 tick 的耗时 (ns). 用于 JMX Monitoring.
     */
    public final long[] tickTimes = new long[100];
    /**
     * 过去 100 tick 的平均耗时 (ms). 用于 JMX Monitoring.
     */
    public float averageTickTime;

    Server(final String filePath, String dataPath, String pluginPath) {
        Preconditions.checkState(instance == null, "Already initialized!");
        currentThread = Thread.currentThread(); // Saves the current thread instance as a reference, used in Server#isPrimaryThread()
        instance = this;

        this.filePath = filePath;
        if (!new File(dataPath + "worlds/").exists()) {
            new File(dataPath + "worlds/").mkdirs();
        }

        if (!new File(dataPath + "players/").exists()) {
            new File(dataPath + "players/").mkdirs();
        }

        if (!new File(pluginPath).exists()) {
            new File(pluginPath).mkdirs();
        }

        this.dataPath = new File(dataPath).getAbsolutePath() + "/";
        this.pluginPath = new File(pluginPath).getAbsolutePath() + "/";

        this.console = new NukkitConsole(this);
        this.consoleThread = new ConsoleThread();
        this.consoleThread.start();

        if (!new File(this.dataPath + "nukkit.yml").exists()) {
            log.info(TextFormat.GREEN + "Welcome! Please choose a language first!");
            try {
                String[] lines = Utils.readFile(this.getClass().getClassLoader().getResourceAsStream("lang/language.list")).split("\n");
                for (String line : lines) {
                    log.info(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String fallback = BaseLang.FALLBACK_LANGUAGE;
            String language = null;
            while (language == null) {
                String lang = this.console.readLine();
                InputStream conf = this.getClass().getClassLoader().getResourceAsStream("lang/" + lang + "/lang.ini");
                if (conf != null) {
                    language = lang;
                }
            }

            InputStream advacedConf = this.getClass().getClassLoader().getResourceAsStream("lang/" + language + "/nukkit.yml");
            if (advacedConf == null) {
                advacedConf = this.getClass().getClassLoader().getResourceAsStream("lang/" + fallback + "/nukkit.yml");
            }

            try {
                Utils.writeFile(this.dataPath + "nukkit.yml", advacedConf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.console.setExecutingCommands(true);

        log.info("Loading {} ...", TextFormat.GREEN + "nukkit.yml" + TextFormat.WHITE);
        this.config = new Config(this.dataPath + "nukkit.yml", Config.YAML);

        log.info("Loading {} ...", TextFormat.GREEN + "server properties" + TextFormat.WHITE);
        this.properties = new Config(this.dataPath + "server.properties", Config.PROPERTIES, new ConfigSection() {
            {
                put("motd", "Nukkit Server For Minecraft: BE");
                put("sub-motd", "Powered by Nukkit");
                put("server-port", 19132);
                put("server-ip", "0.0.0.0");
                put("view-distance", 10);
                put("white-list", false);
                put("max-players", 20);
                put("allow-flight", false);
                put("spawn-animals", true);
                put("spawn-mobs", true);
                put("gamemode", 0);
                put("force-gamemode", false);
                put("hardcore", false);
                put("pvp", true);
                put("difficulty", 1);
                put("generator-settings", "");
                put("level-name", "world");
                put("level-seed", "");
                put("level-type", "DEFAULT");
                put("enable-upnp", false);
                put("enable-query", true);
                put("enable-rcon", false);
                put("rcon.password", Base64.getEncoder().encodeToString(UUID.randomUUID().toString().replace("-", "").getBytes()).substring(3, 13));
                put("auto-save", true);
                put("force-resources", false);
                put("bug-report", true);
                put("enable-jmx-monitoring", false);
            }
        });

        this.configuration = ServerConfiguration.builder()
                .serverIp(getPropertyString("server-ip", "0.0.0.0"))
                .serverPort(getPropertyInt("server-port", 19132))
                .enableWhitelist(getPropertyBoolean("white-list", false))
                .motd(getPropertyString("motd", "Nukkit Server For Minecraft: BE"))
                .forceResources(getPropertyBoolean("force-resources", false))
                .gameMode(getPropertyInt("gamemode", 0) & 0b11)
                .forceGameMode(getPropertyBoolean("force-gamemode", false))
                .hardcore(getPropertyBoolean("hardcore", false))
                .difficulty(getDifficultyFromString(properties.isInt("difficulty") ? String.valueOf(getPropertyInt("difficulty", 1)) : getPropertyString("difficulty", "1")))
                .pvp(getPropertyBoolean("pvp"))
                .viewDistance(Mth.clamp(getPropertyInt("view-distance", 10), 5, 96))
                .levelType(getPropertyString("level-type", "DEFAULT"))
                .generatorSettings(getPropertyString("generator-settings", ""))
                .chunkSpawnThreshold(getConfig("chunk-sending.spawn-threshold", 56))
                .chunkSendingPerTick(getConfig("chunk-sending.per-tick", 4))
                .chunkTickingPerTick(getConfig("chunk-ticking.per-tick", 40))
                .chunkTickRadius(getConfig("chunk-ticking.tick-radius", 4))
                .chunkGenerationQueueSize(getConfig("chunk-generation.queue-size", 8))
                .chunkPopulationQueueSize(getConfig("chunk-generation.population-queue-size", 2))
                .clearChunksOnTick(getConfig("chunk-ticking.clear-tick-list", true))
                .cacheChunks(getConfig("chunk-sending.cache-chunks", false))
                .lightUpdates(getConfig("chunk-ticking.light-updates", false))
                .savePlayerData(getConfig("player.save-player-data", true))
                .build();

        this.forceLanguage = this.getConfig("settings.force-language", false);
        this.baseLang = new BaseLang(this.getConfig("settings.language", BaseLang.FALLBACK_LANGUAGE));
        log.info(this.getLanguage().translate("nukkit.language.selected", getLanguage().getName()));
        log.info(getLanguage().translate("nukkit.server.start", TextFormat.AQUA + this.getVersion() + TextFormat.RESET));

        int corePoolSize;
        Object poolSize = this.getConfig("settings.async-workers", "auto");
        try {
            corePoolSize = Math.max(Integer.parseInt(String.valueOf(poolSize)), 0);
        } catch (Exception e) {
            corePoolSize = Math.max(Runtime.getRuntime().availableProcessors() + 1, 4);
        }
        int maximumPoolSize = this.getConfig("settings.max-async-workers", 0);
        if (maximumPoolSize > 0) {
            maximumPoolSize = Math.max(corePoolSize, maximumPoolSize);
        } else {
            maximumPoolSize = Integer.MAX_VALUE;
        }
        int keepAliveSeconds = this.getConfig("settings.async-worker-keep-alive", 60);
        log.info("AsyncPool Workers: minimum {} threads, maximum {} threads, keep alive {} seconds", corePoolSize, maximumPoolSize, keepAliveSeconds);

        this.networkZlibProvider = this.getConfig("network.zlib-provider", 2);
        Zlib.setProvider(this.networkZlibProvider);

        this.networkCompressionLevel = Mth.clamp(this.getConfig("network.compression-level", 7), Deflater.BEST_SPEED, Deflater.BEST_COMPRESSION);
        this.networkCompressionAsync = this.getConfig("network.async-compression", true);

        this.autoTickRate = this.getConfig("level-settings.auto-tick-rate", true);
        this.autoTickRateLimit = this.getConfig("level-settings.auto-tick-rate-limit", 20);
        this.alwaysTickPlayers = this.getConfig("level-settings.always-tick-players", false);
        this.baseTickRate = this.getConfig("level-settings.base-tick-rate", 1);
        this.redstoneEnabled = this.getConfig("level-settings.tick-redstone", true);

        this.autoCompaction = this.getConfig("level-settings.auto-compression", true);
        this.autoCompactionTicks = Math.max(1, this.getConfig("ticks-per.auto-compaction", 30 * 60 * 20));

        this.scheduler = new ServerScheduler(corePoolSize, maximumPoolSize, keepAliveSeconds);

        log.info("Force async save player data: {}", FORCE_ASYNC_SAVE_PLAYER_DATA);

        if (this.getPropertyBoolean("enable-rcon", false)) {
            try {
                this.rcon = new RCON(this, this.getPropertyString("rcon.password", ""), !this.getIp().isEmpty() ? this.getIp() : "0.0.0.0", this.getPropertyInt("rcon.port", this.getPort()));
            } catch (IllegalArgumentException e) {
                log.error(getLanguage().translate(e.getMessage(), e.getCause().getMessage()), e);
            }
        }

        this.entityMetadata = new EntityMetadataStore();
        this.playerMetadata = new PlayerMetadataStore();
        this.levelMetadata = new LevelMetadataStore();

        this.operators = new Config(this.dataPath + "ops.txt", Config.ENUM);
        this.whitelist = new Config(this.dataPath + "white-list.txt", Config.ENUM);
        this.banByName = new BanList(this.dataPath + "banned-players.json");
        this.banByName.load();
        this.banByIP = new BanList(this.dataPath + "banned-ips.json");
        this.banByIP.load();

        this.maxPlayers = this.getPropertyInt("max-players", 20);
        this.setAutoSave(this.getPropertyBoolean("auto-save", true));

        if (this.getPropertyBoolean("hardcore", false) && this.getDifficulty() < 3) {
            this.setPropertyInt("difficulty", 3);
        }

//        this.allowFlight = this.getPropertyBoolean("allow-flight", false);
        this.allowFlight = true;

        this.enableJmxMonitoring = this.getPropertyBoolean("enable-jmx-monitoring", false);
        if (this.enableJmxMonitoring) {
            ServerStatistics.registerJmxMonitoring(this);
        }

        Nukkit.DEBUG = Mth.clamp(this.getConfig("debug.level", 1), 1, 3);

        int logLevel = (Nukkit.DEBUG + 3) * 100;
        org.apache.logging.log4j.Level currentLevel = Nukkit.getLogLevel();
        for (org.apache.logging.log4j.Level level : org.apache.logging.log4j.Level.values()) {
            if (level.intLevel() == logLevel && level.intLevel() > currentLevel.intLevel()) {
                Nukkit.setLogLevel(level);
                break;
            }
        }

        if (this.getConfig().getBoolean("bug-report", true)) {
            ExceptionHandler.registerExceptionHandler();
        }

        log.info(this.getLanguage().translate("nukkit.server.networkStart", "".equals(this.getIp()) ? "*" : this.getIp(), this.getPort()));
        this.serverID = UUID.randomUUID();

        GameVersion.setFeatureVersion(GameVersion.byName(getConfig("base-game-version", GameVersion.getFeatureVersion().toString())));
        log.info("Base Game Version: {}", GameVersion.getFeatureVersion());

        this.network = new Network(this);
        this.network.setName(this.getMotd());
        this.network.setSubName(this.getSubMotd());

        log.info(this.getLanguage().translate("nukkit.server.info", this.getName(), TextFormat.YELLOW + this.getNukkitVersion() + TextFormat.WHITE, TextFormat.AQUA + this.getCodename() + TextFormat.WHITE, this.getApiVersion()));
        log.info(this.getLanguage().translate("nukkit.server.license", this.getName()));

        CommandExceptions.init();

        ItemIdMap.init();
        BlockItemConverter.init();
        Entities.registerVanillaEntities();
        BlockEntities.registerVanillaBlockEntities();
        Block.init();
        Enchantment.init();
        Item.init();
        TrimMaterials.registerVanillaTrimMaterials();
        TrimPatterns.registerVanillaTrimPatterns();
        Biomes.registerVanillaBiomes();
        Effect.init();
        Potion.init();
        Attribute.init();
        DispenseBehaviorRegister.init();
        //GlobalBlockPalette.getOrCreateRuntimeId(0, 0); //Force it to load

        this.commandMap = new SimpleCommandMap(this);
        this.consoleSender = new ConsoleCommandSender();

        this.craftingManager = new CraftingManager();
        this.resourcePackManager = new ResourcePackManager(new File(Nukkit.DATA_PATH, "resource_packs"));

        this.pluginManager = new PluginManager(this, this.commandMap);
        this.pluginManager.subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this.consoleSender);

        this.pluginManager.registerInterface(JavaPluginLoader.class);

        this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5);

        if (!Boolean.getBoolean("nukkit.disableRak")) {
            this.network.registerInterface(new RakNetInterface(this));
        }

        this.pluginManager.loadPlugins(this.pluginPath);

        this.enablePlugins(PluginLoadOrder.STARTUP);

        LevelProviderManager.registerBuiltinProviders();

        Generators.registerBuiltinGenerators();

        for (String name : this.getConfig("worlds", new HashMap<String, Object>()).keySet()) {
            if (!this.loadLevel(name)) {
                long seed;
                try {
                    seed = ((Number) this.getConfig("worlds." + name + ".seed")).longValue();
                } catch (Exception e) {
                    seed = System.currentTimeMillis();
                }

                Map<String, Object> options = new Object2ObjectOpenHashMap<>();
                String[] opts = this.getConfig("worlds." + name + ".generator", Generators.getGenerator("default").getSimpleName()).split(":");
                Class<? extends Generator> generator = Generators.getGenerator(opts[0]);
                if (opts.length > 1) {
                    StringBuilder preset = new StringBuilder();
                    for (int i = 1; i < opts.length; i++) {
                        preset.append(opts[i]).append(":");
                    }
                    preset = new StringBuilder(preset.substring(0, preset.length() - 1));

                    options.put("preset", preset.toString());
                }

                this.generateLevel(name, LevelCreationOptions.builder()
                        .seed(seed)
                        .generator(generator)
                        .options(options)
                        .build());
            }
        }

        if (this.getDefaultLevel() == null) {
            String defaultName = this.getPropertyString("level-name", "world");
            if (defaultName == null || defaultName.trim().isEmpty()) {
                log.warn("level-name cannot be null, using default");
                defaultName = "world";
                this.setPropertyString("level-name", defaultName);
            }

            if (Boolean.getBoolean("nukkit.randomTempDefaultWorld")) {
                defaultName = "temp_" + UUID.randomUUID();

                File tempWorld = new File(dataPath + "worlds/" +  defaultName + "/");
                tempWorld.mkdirs();
                tempWorld.deleteOnExit();
            }

            if (!this.loadLevel(defaultName)) {
                long seed;
                String seedString = String.valueOf(this.getProperty("level-seed", System.currentTimeMillis()));
                try {
                    seed = Long.parseLong(seedString);
                } catch (NumberFormatException e) {
                    seed = seedString.hashCode();
                }
                LevelCreationOptions options = LevelCreationOptions.builder()
                        .seed(seed == 0 ? System.currentTimeMillis() : seed)
                        .generator(Generators.getGenerator(this.getLevelType()))
//                        .difficulty(Difficulty.byId(this.getDifficulty()))
                        .build();
                this.generateLevel(defaultName, options);
            }

            this.setDefaultLevel(this.getLevelByName(defaultName));
        }

        this.properties.save(true);

        if (this.getDefaultLevel() == null) {
            log.fatal(this.getLanguage().translate("nukkit.level.defaultError"));
            this.forceShutdown();

            return;
        }

        if (this.getConfig("ticks-per.autosave", 6000) > 0) {
            this.autoSaveTicks =  this.getConfig("ticks-per.autosave", 6000);
        }

        this.enablePlugins(PluginLoadOrder.POSTWORLD);

        if (Nukkit.DEBUG < 2) {
            this.watchdog = new Watchdog(this, 60000);
            this.watchdog.start();
        }

        this.start();
    }

    public int broadcastMessage(String message) {
        return this.broadcast(message, BROADCAST_CHANNEL_USERS);
    }

    public int broadcastMessage(TextContainer message) {
        return this.broadcast(message, BROADCAST_CHANNEL_USERS);
    }

    public int broadcastMessage(String message, CommandSender[] recipients) {
        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.length;
    }

    public int broadcastMessage(String message, Collection<? extends CommandSender> recipients) {
        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.size();
    }

    public int broadcastMessage(TextContainer message, Collection<? extends CommandSender> recipients) {
        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.size();
    }

    public int broadcast(String message, String permissions) {
        Set<CommandSender> recipients = new HashSet<>();

        for (String permission : permissions.split(";")) {
            for (Permissible permissible : this.pluginManager.getPermissionSubscriptions(permission)) {
                if (permissible instanceof CommandSender && permissible.hasPermission(permission)) {
                    recipients.add((CommandSender) permissible);
                }
            }
        }

        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.size();
    }

    public int broadcast(TextContainer message, String permissions) {
        Set<CommandSender> recipients = new HashSet<>();

        for (String permission : permissions.split(";")) {
            for (Permissible permissible : this.pluginManager.getPermissionSubscriptions(permission)) {
                if (permissible instanceof CommandSender && permissible.hasPermission(permission)) {
                    recipients.add((CommandSender) permissible);
                }
            }
        }

        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.size();
    }

    public static void broadcastPacket(Collection<Player> players, DataPacket packet) {
//        if (players.size() > 1) {
//            packet.tryEncode();
//        }

        for (Player player : players) {
            player.dataPacket(packet);
        }
    }

    public static void broadcastPacket(Player[] players, DataPacket packet) {
//        if (players.length > 1) {
//            packet.tryEncode();
//        }

        for (Player player : players) {
            player.dataPacket(packet);
        }
    }

    @Deprecated
    public void batchPackets(Player[] players, DataPacket[] packets) {
        this.batchPackets(players, packets, false);
    }

    @Deprecated
    public void batchPackets(Player[] players, DataPacket[] packets, boolean forceSync) {
        if (players == null || packets == null || players.length == 0 || packets.length == 0) {
            return;
        }

        BatchPacketsEvent ev = new BatchPacketsEvent(players, packets, forceSync);
        getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        Track[] tracks = new Track[packets.length];

        byte[][] payload = new byte[packets.length * 2][];
        for (int i = 0; i < packets.length; i++) {
            DataPacket p = packets[i];
            int idx = i * 2;
            p.tryEncode();
            byte[] buf = p.getBuffer();
            payload[idx] = Binary.writeUnsignedVarInt(buf.length);
            payload[idx + 1] = buf;

            tracks[i] = new Track(p.pid(), p.getCount());
        }
        byte[] data;
        data = Binary.appendBytes(payload);

        List<InetSocketAddress> targets = new ArrayList<>();
        for (Player p : players) {
            if (p.isConnected()) {
                targets.add(p.getSocketAddress());
            }
        }

        if (!forceSync && this.networkCompressionAsync) {
            this.getScheduler().scheduleAsyncTask(null, new CompressBatchedTask(data, targets, this.networkCompressionLevel, tracks));
        } else {
            try {
                this.broadcastPacketsCallback(Zlib.deflate(data, this.networkCompressionLevel), targets, tracks);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void broadcastPacketsCallback(byte[] data, List<InetSocketAddress> targets) {
        broadcastPacketsCallback(data, targets, null);
    }

    public void broadcastPacketsCallback(byte[] data, List<InetSocketAddress> targets, Track[] tracks) {
        BatchPacket pk = new BatchPacket();
        pk.payload = data;
        pk.tracks = tracks;

        for (InetSocketAddress i : targets) {
            Player player = this.players.get(i);
            if (player != null) {
                player.dataPacket(pk);
            }
        }
    }

    public void enablePlugins(PluginLoadOrder type) {
        for (Plugin plugin : new ArrayList<>(this.pluginManager.getPlugins().values())) {
            if (!plugin.isEnabled() && type == plugin.getDescription().getOrder()) {
                this.enablePlugin(plugin);
            }
        }

        if (type == PluginLoadOrder.POSTWORLD) {
            this.commandMap.registerServerAliases();
            DefaultPermissions.registerCorePermissions();
        }
    }

    public void enablePlugin(Plugin plugin) {
        this.pluginManager.enablePlugin(plugin);
    }

    public void disablePlugins() {
        this.pluginManager.disablePlugins();
    }

    public boolean dispatchCommand(CommandSender sender, String commandLine) throws ServerException {
        // First we need to check if this command is on the main thread or not, if not, warn the user
        if (!this.isPrimaryThread()) {
            log.warn("Command Dispatched Async: " + commandLine);
            log.warn("Please notify author of plugin causing this execution to fix this bug!", new Throwable());
            // TODO: We should sync the command to the main thread too!
        }
        if (sender == null) {
            throw new ServerException("CommandSender is not valid");
        }

        if (this.commandMap.dispatch(sender, commandLine)) {
            return true;
        }

        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.unknown", commandLine));

        return false;
    }

    //todo: use ticker to check console
    public ConsoleCommandSender getConsoleSender() {
        return consoleSender;
    }

    public void reload() {
        log.info("Reloading...");

        log.info("Saving levels...");

        for (Level level : this.levels.values()) {
            level.save();
        }

        this.pluginManager.disablePlugins();
        this.pluginManager.clearPlugins();
        this.commandMap.clearCommands();

        log.info("Reloading properties...");
        this.properties.reload();
        this.maxPlayers = this.getPropertyInt("max-players", 20);

        if (this.getPropertyBoolean("hardcore", false) && this.getDifficulty() < 3) {
            this.setPropertyInt("difficulty", 3);
        }

        this.banByIP.load();
        this.banByName.load();
        this.reloadWhitelist();
        this.operators.reload();

        for (BanEntry entry : this.getIPBans().getEntires().values()) {
            try {
                this.getNetwork().blockAddress(InetAddress.getByName(entry.getName()), -1);
            } catch (UnknownHostException e) {
                // ignore
            }
        }

        this.pluginManager.registerInterface(JavaPluginLoader.class);
        this.pluginManager.loadPlugins(this.pluginPath);
        this.enablePlugins(PluginLoadOrder.STARTUP);
        this.enablePlugins(PluginLoadOrder.POSTWORLD);
    }

    public void shutdown() {
        isRunning.compareAndSet(true, false);
    }

    public void forceShutdown() {
        if (this.hasStopped) {
            return;
        }

        try {
            isRunning.compareAndSet(true, false);

            this.hasStopped = true;

            if (this.rcon != null) {
                this.rcon.close();
            }

            log.debug("Disabling all plugins");
            this.pluginManager.disablePlugins();

            String shutdownMessage = this.getConfig("settings.shutdown-message", "Server closed");
            for (Player player : new ArrayList<>(this.players.values())) {
                player.close(player.getLeaveMessage(), shutdownMessage);
            }

            log.debug("Unloading all levels");
            for (Level level : new ArrayList<>(this.getLevels().values())) {
                this.unloadLevel(level, true);
            }

            log.debug("Removing event handlers");
            HandlerList.unregisterAll();

            log.debug("Stopping all tasks");
            this.scheduler.cancelAllTasks();
            this.scheduler.mainThreadHeartbeat(Integer.MAX_VALUE);

            log.debug("Closing console");
            this.consoleThread.interrupt();

            if (this.upnpEnabled) {
                log.debug("Closing UPnP port");
                if (UPnP.closePortUDP(this.getPort())) {
                    log.info(this.getLanguage().translate("nukkit.server.upnp.closed", getPort()));
                }
            }

            log.debug("Stopping network interfaces");
            for (SourceInterface interfaz : this.network.getInterfaces()) {
                interfaz.shutdown();
                this.network.unregisterInterface(interfaz);
            }

            if (this.watchdog != null) {
                this.watchdog.kill();
            }
        } catch (Exception e) {
            log.fatal("Exception happened while shutting down", e);
        }
    }

    public void start() {
        if (this.getPropertyBoolean("enable-upnp", false)) {
            if (UPnP.isUPnPAvailable()) {
                log.debug(this.getLanguage().translate("nukkit.server.upnp.enabled"));
                if (UPnP.openPortUDP(getPort(), "Nukkit")) {
                    this.upnpEnabled = true; // Saved to disable the port-forwarding on shutdown
                    log.info(this.getLanguage().translate("nukkit.server.upnp.success", getPort()));
                } else {
                    this.upnpEnabled = false;
                    log.warn(this.getLanguage().translate("nukkit.server.upnp.fail", getPort()));
                }
            } else {
                this.upnpEnabled = false;
                log.warn(this.getLanguage().translate("nukkit.server.upnp.unavailable"));
            }
        } else {
            this.upnpEnabled = false;
            log.debug(this.getLanguage().translate("nukkit.server.upnp.disabled"));
        }

        if (this.getPropertyBoolean("enable-query", true)) {
            this.queryHandler = new QueryHandler();
        }

        for (BanEntry entry : this.getIPBans().getEntires().values()) {
            try {
                this.network.blockAddress(InetAddress.getByName(entry.getName()), -1);
            } catch (UnknownHostException e) {
                // ignore
            }
        }

        this.tickCounter = 0;

        if (Boolean.getBoolean("nukkit.docker")) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                shutdown();

                System.out.println("Shutdown hook triggered...");
                while (!Nukkit.STOPPED) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Server Stopped.");
            }, "Nukkit Shutdown Hook"));
        }

        log.info(this.getLanguage().translate("nukkit.server.defaultGameMode", getGamemodeString(this.getGamemode())));

        log.info(this.getLanguage().translate("nukkit.server.startFinished", (double) (System.currentTimeMillis() - Nukkit.START_TIME) / 1000));

        this.tickProcessor();
        this.forceShutdown();
    }

    public void handlePacket(InetSocketAddress address, ByteBuf payload) {
        try {
            if (!payload.isReadable(3)) {
                return;
            }
            byte[] prefix = new byte[2];
            payload.readBytes(prefix);

            if (!Arrays.equals(prefix, new byte[]{(byte) 0xfe, (byte) 0xfd})) {
                return;
            }
            if (this.queryHandler != null) {
                this.queryHandler.handle(address, payload);
            }
        } catch (Exception e) {
            log.error("Error whilst handling packet", e);

            this.network.blockAddress(address.getAddress(), -1);
        }
    }

    public void tickProcessor() {
        this.nextTick = System.currentTimeMillis();
        try {
            while (this.isRunning.get()) {
                try {
                    this.tick();
                } catch (RuntimeException e) {
                    log.warn("tickProcessor ROOT RuntimeException", e);
                } finally {
                    long next = this.nextTick;
                    long current = System.currentTimeMillis();
                    if (next - 0.1 > current) {
                        Thread.sleep(next - current - 1, 900000);
                    }
                }
            }
        } catch (Throwable e) {
            log.fatal("Exception happened while ticking server", e);
            log.fatal(Utils.getAllThreadDumps());
        }
    }

    public void onPlayerCompleteLoginSequence(Player player) {
        //在EntityHuman中会发送各自的皮肤的，这里不需要发送全部
        //this.sendFullPlayerListData(player);
        // 发给自己自己的皮肤
        this.updatePlayerListData(player.getUniqueId(), player.getId(), player.getDisplayName(), player.getSkin(), player.getLoginChainData().getXUID(), new Player[]{player});
        player.sentSkins.add(player.getUniqueId());
    }

    public void onPlayerLogin(Player player) {
        if (this.sendUsageTicker > 0) {
//            this.uniquePlayers.add(player.getUniqueId());
        }
    }

    public void addPlayer(InetSocketAddress socketAddress, Player player) {
        this.players.put(socketAddress, player);
    }

    public void addOnlinePlayer(Player player) {
        this.playerList.put(player.getUniqueId(), player);
        //移到了玩家的生成中，这里不发送全部
        //this.updatePlayerListData(player.getUniqueId(), player.getId(), player.getDisplayName(), player.getSkin(), player.getLoginChainData().getXUID(), player.getLevel().getPlayers().values());
    }

    public void removeOnlinePlayer(Player player) {
        if (player.getUniqueId() == null) {
            return;
        }
        if (this.playerList.remove(player.getUniqueId()) != null) {
            PlayerListPacket pk = new PlayerListPacket();
            pk.type = PlayerListPacket.TYPE_REMOVE;
            pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(player.getUniqueId())};

            Server.broadcastPacket(this.playerList.values(), pk);
        }
    }

    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", this.playerList.values());
    }

    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, String xboxUserId) {
        this.updatePlayerListData(uuid, entityId, name, skin, xboxUserId, this.playerList.values());
    }

    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, Player[] players) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", players);
    }

    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, String xboxUserId, Player[] players) {
        Set<Player> playersSet = null;
        for (Player player : players) {
            SendPlayerListDataEvent event = new SendPlayerListDataEvent(player, uuid, entityId, name, skin, xboxUserId);
            event.call();
            if (event.isCancelled()) {
                if (playersSet == null) {
                    playersSet = new HashSet<>(Arrays.asList(players));
                }
                playersSet.remove(player);
            } else if (event.isDirty()) {
                if (playersSet == null) {
                    playersSet = new HashSet<>(Arrays.asList(players));
                }
                playersSet.remove(player);
                PlayerListPacket pk = new PlayerListPacket();
                pk.type = PlayerListPacket.TYPE_ADD;
                pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(event.getUuid(), event.getEntityId(), event.getName(), event.getSkin(), event.getXboxUserId())};
                player.dataPacket(pk);
            }
        }
        if (playersSet != null) {
            if (!playersSet.isEmpty()) {
                PlayerListPacket pk = new PlayerListPacket();
                pk.type = PlayerListPacket.TYPE_ADD;
                pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid, entityId, name, skin, xboxUserId)};
                Server.broadcastPacket(playersSet, pk);
            }
        } else {
            PlayerListPacket pk = new PlayerListPacket();
            pk.type = PlayerListPacket.TYPE_ADD;
            pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid, entityId, name, skin, xboxUserId)};
            Server.broadcastPacket(players, pk);
        }
    }

    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, String xboxUserId, Collection<Player> players) {
        this.updatePlayerListData(uuid, entityId, name, skin, xboxUserId,
                players.toArray(new Player[0]));
    }

    public void removePlayerListData(UUID uuid) {
        this.removePlayerListData(uuid, this.playerList.values());
    }

    public void removePlayerListData(UUID uuid, Player[] players) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_REMOVE;
        pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid)};
        Server.broadcastPacket(players, pk);
    }

    public void removePlayerListData(UUID uuid, Collection<Player> players) {
        this.removePlayerListData(uuid, players.toArray(new Player[0]));
    }

    public void sendFullPlayerListData(Player player) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_ADD;
        pk.entries = this.playerList.values().stream()
                .map(p -> new PlayerListPacket.Entry(
                p.getUniqueId(),
                p.getId(),
                p.getDisplayName(),
                p.getSkin(),
                p.getLoginChainData().getXUID()))
                .toArray(PlayerListPacket.Entry[]::new);

        player.dataPacket(pk);
    }

    /* 魔改迁移至Player::sendRecipeList
    public void sendRecipeList(Player player) {
        player.dataPacket(CraftingManager.packet);
    }*/

    private void checkTickUpdates(int currentTick, long tickTime) {
        for (Player p : new ArrayList<>(this.players.values())) {
            /*if (!p.loggedIn && (tickTime - p.creationTime) >= 10000 && p.kick(PlayerKickEvent.Reason.LOGIN_TIMEOUT, "Login timeout")) {
                continue;
            }

            client freezes when applying resource packs
            todo: fix*/

            if (this.alwaysTickPlayers) {
                try {
                    p.onUpdate(currentTick);
                } catch (Exception e) {

                }
            }
        }

        //Do level ticks
        for (Level level : this.getLevels().values()) {
            if (level.getTickRate() > this.baseTickRate && --level.tickRateCounter > 0) {
                continue;
            }

            tickingLevel = level;
            try {
                long levelTime = System.currentTimeMillis();
                level.doTick(currentTick);
                int tickMs = (int) (System.currentTimeMillis() - levelTime);
                level.tickRateTime = tickMs;

                if (this.autoTickRate) {
                    if (tickMs < 50 && level.getTickRate() > this.baseTickRate) {
                        int r;
                        level.setTickRate(r = level.getTickRate() - 1);
                        if (r > this.baseTickRate) {
                            level.tickRateCounter = level.getTickRate();
                        }
                        log.debug("Raising level \"" + level.getName() + "\" tick rate to " + level.getTickRate() + " ticks");
                    } else if (tickMs >= 50) {
                        if (level.getTickRate() == this.baseTickRate) {
                            level.setTickRate(Math.max(this.baseTickRate + 1, Math.min(this.autoTickRateLimit, tickMs / 50)));
                            log.debug("Level \"" + level.getName() + "\" took " + NukkitMath.round(tickMs, 2) + "ms, setting tick rate to " + level.getTickRate() + " ticks");
                        } else if ((tickMs / level.getTickRate()) >= 50 && level.getTickRate() < this.autoTickRateLimit) {
                            level.setTickRate(level.getTickRate() + 1);
                            log.debug("Level \"" + level.getName() + "\" took " + NukkitMath.round(tickMs, 2) + "ms, setting tick rate to " + level.getTickRate() + " ticks");
                        }
                        level.tickRateCounter = level.getTickRate();
                    }
                }
            } catch (Exception e) {
                log.fatal(this.getLanguage().translate("nukkit.level.tickError", level.getName(), e.toString()));
                this.getLogger().logException(e);
            }
        }
        tickingLevel = null;
    }

    public void doAutoSave() {
        if (this.getAutoSave()) {
            for (Player player : new ArrayList<>(this.players.values())) {
                if (player.isOnline()) {
                    player.save(true);
                } else if (!player.isConnected()) {
                    this.removePlayer(player);
                }
            }

            for (Level level : this.getLevels().values()) {
                level.save();
            }
        }
    }

    private boolean tick() {
        long tickTime = System.currentTimeMillis();
        long tickTimeNano = System.nanoTime();
        if ((tickTime - this.nextTick) < -25) {
            return false;
        }

        ++this.tickCounter;

        this.network.processInterfaces();

        if (this.rcon != null) {
            this.rcon.check();
        }

        this.scheduler.mainThreadHeartbeat(this.tickCounter);

        this.checkTickUpdates(this.tickCounter, tickTime);

        for (Player player : new ArrayList<>(this.players.values())) {
            player.checkNetwork();
        }

        if ((this.tickCounter & 0b1111) == 0) {
            this.titleTick();
            this.network.resetStatistics();
            this.maxTick = 20;
            this.tps = 20;
            this.maxUse = 0;
            this.tickUsage = 0;

            if ((this.tickCounter & 0b111111111) == 0) {
                try {
                    this.getPluginManager().callEvent(this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5));
                    if (this.queryHandler != null) {
                        this.queryHandler.regenerateInfo();
                    }
                } catch (Exception e) {
                    this.getLogger().logException(e);
                }
            }

            this.getNetwork().updateName();
        }

        if (scheduledAutoSave && this.autoSave && ++this.autoSaveTicker >= this.autoSaveTicks) {
            this.autoSaveTicker = 0;
            this.doAutoSave();
        }

        if (this.sendUsageTicker > 0 && --this.sendUsageTicker == 0) {
            this.sendUsageTicker = 6000;
            //todo sendUsage
        }

        if (this.tickCounter % 100 == 0) {
            for (Level level : this.levels.values()) {
                level.doChunkGarbageCollection();
            }
        }

        //long now = System.currentTimeMillis();
        long nowNano = System.nanoTime();
        //float tick = Math.min(20, 1000 / Math.max(1, now - tickTime));
        //float use = Math.min(1, (now - tickTime) / 50);

        float tick = (float) Math.min(20, 1000000000 / Math.max(1000000, ((double) nowNano - tickTimeNano)));
        this.tickUseNano = nowNano - tickTimeNano;  // record the tick time in nano seconds
        float use = (float) Math.min(1, ((double) tickUseNano) / 50000000);

        if (this.maxTick > tick) {
            this.maxTick = tick;
            this.tps = Math.round(this.maxTick * 100) / 100f;
        }

        if (this.maxUse < use) {
            this.maxUse = use;
            this.tickUsage = Math.round(this.maxUse * 100) / 100f;
        }

        System.arraycopy(this.tickAverage, 1, this.tickAverage, 0, this.tickAverage.length - 1);
        this.tickAverage[this.tickAverage.length - 1] = tick;
        this.tpsAverage = getAverage(this.tickAverage);

        System.arraycopy(this.useAverage, 1, this.useAverage, 0, this.useAverage.length - 1);
        this.useAverage[this.useAverage.length - 1] = use;
        this.tickUsageAverage = getAverage(this.useAverage);

        if (this.enableJmxMonitoring) {
            long diffNano = nowNano - tickTimeNano;
            this.tickTimes[this.tickCounter % 100] = diffNano;
            this.averageTickTime = this.averageTickTime * .8f + (float) diffNano / 1000000f * .19999999f;
        }

        if ((this.nextTick - tickTime) < -1000) {
            this.nextTick = tickTime;
        } else {
            this.nextTick += 50;
        }

        return true;
    }

    public long getNextTick() {
        return nextTick;
    }

    // TODO: Fix title tick
    public void titleTick() {
        if (!Nukkit.TITLE) {
            return;
        }

        Runtime runtime = Runtime.getRuntime();
        double used = NukkitMath.round((double) (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024, 2);
        double max = NukkitMath.round(((double) runtime.maxMemory()) / 1024 / 1024, 2);
        String usage = Math.round(used / max * 100) + "%";
        String title = (char) 0x1b + "]0;" + this.getName() + " " +
                this.getNukkitVersion() +
                " | Online " + this.players.size() + "/" + this.getMaxPlayers() +
                " | Memory " + usage;
        if (!Nukkit.shortTitle) {
            title += " | U " + NukkitMath.round((this.network.getUpload() / 1024 * 1000), 2)
                    + " D " + NukkitMath.round((this.network.getDownload() / 1024 * 1000), 2) + " kB/s";
        }
        title += " | TPS " + this.getTicksPerSecondAverage() +
                " | Load " + this.getTickUsageAverage() + "%" + (char) 0x07;

        System.out.print(title);
    }

    public QueryRegenerateEvent getQueryInformation() {
        return this.queryRegenerateEvent;
    }

    public String getName() {
        return "Nukkit";
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public String getNukkitVersion() {
        return Nukkit.VERSION;
    }

    public String getCodename() {
        return Nukkit.CODENAME;
    }

    public String getVersion() {
        return ProtocolInfo.MINECRAFT_VERSION;
    }

    public String getApiVersion() {
        return Nukkit.API_VERSION;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getDataPath() {
        return dataPath;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getPort() {
        return this.configuration.getServerPort();
    }

    public int getViewDistance() {
        return this.configuration.getViewDistance();
    }

    public String getIp() {
        return this.configuration.getServerIp();
    }

    public UUID getServerUniqueId() {
        return this.serverID;
    }

    public boolean getAutoSave() {
        return this.autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
        for (Level level : this.getLevels().values()) {
            level.setAutoSave(this.autoSave);
        }
    }

    public void setScheduledAutoSave(boolean enable) {
        this.scheduledAutoSave = enable;
    }

    public String getLevelType() {
        return this.configuration.getLevelType();
    }

    public boolean getGenerateStructures() {
        return this.getPropertyBoolean("generate-structures", true);
    }

    public int getGamemode() {
        return this.configuration.getGameMode();
    }

    public boolean getForceGamemode() {
        return this.configuration.isForceGameMode();
    }

    public static String getGamemodeString(int mode) {
        return getGamemodeString(mode, false);
    }

    public static String getGamemodeString(int mode, boolean direct) {
        switch (mode) {
            case Player.SURVIVAL:
                return direct ? "Survival" : "%gameMode.survival";
            case Player.CREATIVE:
                return direct ? "Creative" : "%gameMode.creative";
            case Player.ADVENTURE:
                return direct ? "Adventure" : "%gameMode.adventure";
            case Player.SPECTATOR:
                return direct ? "Spectator" : "%gameMode.spectator";
        }
        return "UNKNOWN";
    }

    public static int getGamemodeFromString(String str) {
        switch (str.trim().toLowerCase()) {
            case "0":
            case "survival":
            case "s":
                return Player.SURVIVAL;

            case "1":
            case "creative":
            case "c":
                return Player.CREATIVE;

            case "2":
            case "adventure":
            case "a":
                return Player.ADVENTURE;

            case "3":
            case "spectator":
            case "spc":
            case "view":
            case "v":
                return Player.SPECTATOR;
        }
        return -1;
    }

    public static int getDifficultyFromString(String str) {
        switch (str.trim().toLowerCase()) {
            case "0":
            case "peaceful":
            case "p":
                return 0;

            case "1":
            case "easy":
            case "e":
                return 1;

            case "2":
            case "normal":
            case "n":
                return 2;

            case "3":
            case "hard":
            case "h":
                return 3;
        }
        return -1;
    }

    public int getDifficulty() {
        return configuration.getDifficulty();
    }

    public boolean hasWhitelist() {
        return this.configuration.isEnableWhitelist();
    }

    public boolean getAllowFlight() {
        return allowFlight;
    }

    public boolean isHardcore() {
        return this.configuration.isHardcore();
    }

    public int getDefaultGamemode() {
        return this.configuration.getGameMode();
    }

    public String getMotd() {
        return this.configuration.getMotd();
    }

    public String getSubMotd() {
        String subMotd = this.getPropertyString("sub-motd", "Powered by Nukkit");
        if (subMotd.isEmpty()) {
            subMotd = "Powered by Nukkit"; // The client doesn't allow empty sub-motd in 1.16.210
        }
        return subMotd;
    }

    public boolean getForceResources() {
        return this.configuration.isForceResources();
    }

    public MainLogger getLogger() {
        return MainLogger.getLogger();
    }

    public EntityMetadataStore getEntityMetadata() {
        return entityMetadata;
    }

    public PlayerMetadataStore getPlayerMetadata() {
        return playerMetadata;
    }

    public LevelMetadataStore getLevelMetadata() {
        return levelMetadata;
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public CraftingManager getCraftingManager() {
        return craftingManager;
    }

    public ResourcePackManager getResourcePackManager() {
        return resourcePackManager;
    }

    public ServerScheduler getScheduler() {
        return scheduler;
    }

    public int getTick() {
        return tickCounter;
    }

    public float getTicksPerSecond() {
        return tps;
    }

    public float getTicksPerSecondRaw() {
        return this.maxTick;
    }

    public float getTicksPerSecondAverage() {
        return tpsAverage;
    }

    public float getTickUsage() {
        return tickUsage;
    }

    public float getTickUsageRaw() {
        return this.maxUse;
    }

    public float getTickUsageAverage() {
        return tickUsageAverage;
    }

    public long getTickUseNano() {
        return tickUseNano;
    }

    private static float getAverage(float[] array) {
        float sum = 0;
        int count = array.length;
        for (float num : array) {
            sum += num;
        }
        return Math.round(sum / count * 100) / 100f;
    }

    public SimpleCommandMap getCommandMap() {
        return commandMap;
    }

    public Map<UUID, Player> getOnlinePlayers() {
        return new HashMap<>(playerList);
    }

    public Map<UUID, Player> getOnlinePlayersUnsafe() {
        return playerList;
    }

    public Player[] getOnlinePlayerList() {
        return playerList.values().toArray(new Player[0]);
    }

    public int getOnlinePlayerCount() {
        return playerList.size();
    }

    public void addRecipe(Recipe recipe) {
        this.craftingManager.registerRecipe(recipe);
    }

    public IPlayer getOfflinePlayer(String name) {
        IPlayer result = this.getPlayerExact(name.toLowerCase());
        if (result == null) {
            return new OfflinePlayer(this, name);
        }

        return result;
    }

    public CompoundTag getOfflinePlayerData(String name) {
        name = name.toLowerCase();
        String path = this.getDataPath() + "players/";
        Path filePath = Paths.get(path, name + ".dat");

        if (this.shouldSavePlayerData() && Files.exists(filePath)) {
            try {
                return NBTIO.readCompressed(Files.newInputStream(filePath));
            } catch (Exception e) {
                try {
                    Files.move(filePath, Paths.get(path, name + ".dat.bak"), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
//                    throw new RuntimeException(ex);
                }
                log.warn(this.getLanguage().translate("nukkit.data.playerCorrupted", name), e);
            }
        } else {
            log.warn(this.getLanguage().translate("nukkit.data.playerNotFound", name));
        }

        Position spawn = this.getDefaultLevel().getSafeSpawn();
        CompoundTag nbt = Entity.getDefaultNBT(spawn)
                .putLong("firstPlayed", System.currentTimeMillis() / 1000)
                .putLong("lastPlayed", System.currentTimeMillis() / 1000)
                .putString("Level", this.getDefaultLevel().getName())
                .putList(new ListTag<>("Inventory"))
                .putInt("playerGameType", this.getGamemode())
                .putFloat("FallDistance", 0)
                .putShort("Fire", 0)
                .putShort("Air", 300)
                .putBoolean("OnGround", true)
                .putBoolean("Invulnerable", false)
                .putString("NameTag", name);

        this.saveOfflinePlayerData(name, nbt);
        return nbt;
    }

    public void saveOfflinePlayerData(String name, CompoundTag tag) {
        this.saveOfflinePlayerData(name, tag, false);
    }

    private static final GzipParameters GZIP_PARAMETERS = new GzipParameters();

    static {
        GZIP_PARAMETERS.setCompressionLevel(Deflater.BEST_SPEED);
    }

    public void saveOfflinePlayerData(String name, CompoundTag tag, boolean async) {
        if (this.shouldSavePlayerData()) {
            try {
                // EC force async
                if (async || FORCE_ASYNC_SAVE_PLAYER_DATA) {
                    byte[] bytes = NBTIO.write(tag, ByteOrder.BIG_ENDIAN);
                    this.getScheduler().scheduleAsyncTask(null, new FileWriteTask(this.getDataPath() + "players/" + name.toLowerCase() + ".dat", () -> {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                        if (bytes.length != 0) {
                            try (OutputStream os = new GzipCompressorOutputStream(baos, GZIP_PARAMETERS)) {
                                os.write(bytes);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return new ByteArrayInputStream(baos.toByteArray());
                    }));
                } else {
                    Utils.writeFile(this.getDataPath() + "players/" + name.toLowerCase() + ".dat", new ByteArrayInputStream(NBTIO.writeGZIPCompressed(tag, ByteOrder.BIG_ENDIAN)));
                }
            } catch (Exception e) {
                log.fatal(this.getLanguage().translate("nukkit.data.saveError", name), e);
            }
        }
    }

    public Player getPlayer(String name) {
        Player found = null;
        name = name.toLowerCase();
        int delta = Integer.MAX_VALUE;
        for (Player player : this.getOnlinePlayerList()) {
            if (player.getName().toLowerCase().startsWith(name)) {
                int curDelta = player.getName().length() - name.length();
                if (curDelta < delta) {
                    found = player;
                    delta = curDelta;
                }
                if (curDelta == 0) {
                    break;
                }
            }
        }

        return found;
    }

    public Player getPlayerExact(String name) {
        name = name.toLowerCase();
        for (Player player : this.getOnlinePlayerList()) {
            if (player.getName().toLowerCase().equals(name)) {
                return player;
            }
        }

        return null;
    }

    public Player[] matchPlayer(String partialName) {
        partialName = partialName.toLowerCase();
        List<Player> matchedPlayer = new ArrayList<>();
        for (Player player : this.getOnlinePlayerList()) {
            if (player.getName().toLowerCase().equals(partialName)) {
                return new Player[]{player};
            } else if (player.getName().toLowerCase().contains(partialName)) {
                matchedPlayer.add(player);
            }
        }

        return matchedPlayer.toArray(new Player[0]);
    }

    public void removePlayer(Player player) {
        Player toRemove = this.players.remove(player.getSocketAddress());
        if (toRemove != null) {
            return;
        }

        for (InetSocketAddress socketAddress : new ArrayList<>(this.players.keySet())) {
            Player p = this.players.get(socketAddress);
            if (player == p) {
                this.players.remove(socketAddress);
                break;
            }
        }
    }

    public Map<Integer, Level> getLevels() {
        return levels;
    }

    public Level getDefaultLevel() {
        return defaultLevel;
    }

    public void setDefaultLevel(Level defaultLevel) {
        if (defaultLevel == null || (this.isLevelLoaded(defaultLevel.getFolderName()) && defaultLevel != this.defaultLevel)) {
            this.defaultLevel = defaultLevel;
        }
    }

    public boolean isLevelLoaded(String name) {
        return this.getLevelByName(name) != null;
    }

    public Level getLevel(int levelId) {
        return this.levels.get(levelId);
    }

    public Level getLevelByName(String name) {
        for (Level level : this.getLevels().values()) {
            if (level.getFolderName().equalsIgnoreCase(name)) {
                return level;
            }
        }

        return null;
    }

    public boolean unloadLevel(Level level) {
        return this.unloadLevel(level, false);
    }

    public boolean unloadLevel(Level level, boolean forceUnload) {
        if (level == this.getDefaultLevel() && !forceUnload) {
            throw new IllegalStateException("The default level cannot be unloaded while running, please switch levels.");
        }

        return level.unload(forceUnload);
    }

    @Nullable
    public CompletableFuture<Void> destroyLevel(Level level) {
        return level.destroy();
    }

    public boolean loadLevel(String name) {
        return loadLevel(name, null);
    }

    public boolean loadLevel(String name, DbInitData dbInitData) {
        if (Objects.equals(name.trim(), "")) {
            throw new LevelException("Invalid empty level name");
        }
        if (this.isLevelLoaded(name)) {
            return true;
        } else if (!this.isLevelGenerated(name)) {
            log.warn(this.getLanguage().translate("nukkit.level.notFound", name));

            return false;
        }

        String path;

        if (new File(name).isAbsolute()) {
            path = name;
        } else {
            path = this.getDataPath() + "worlds/" + name + "/";
        }

        LevelProviderHandle provider = LevelProviderManager.getProvider(path);

        if (provider == null) {
            log.error(this.getLanguage().translate("nukkit.level.loadError", name, "Unknown provider"));

            return false;
        }

        Level level;
        try {
            level = new Level(this, name, path, provider, dbInitData);
        } catch (Exception e) {
            log.error(this.getLanguage().translate("nukkit.level.loadError.exception", name), e);
            return false;
        }

        this.levels.put(level.getId(), level);

        level.initLevel();

        this.getPluginManager().callEvent(new LevelLoadEvent(level));

        level.setTickRate(this.baseTickRate);

        return true;
    }

    public boolean generateLevel(String name) {
        return this.generateLevel(name, LevelCreationOptions.builder().build());
    }

    /**
     * @deprecated use {#generateLevel(String, LevelCreationOptions)} instead
     */
    @Deprecated
    public boolean generateLevel(String name, long seed) {
        return this.generateLevel(name, seed, null);
    }

    /**
     * @deprecated use {#generateLevel(String, LevelCreationOptions)} instead
     */
    @Deprecated
    public boolean generateLevel(String name, long seed, Class<? extends Generator> generator) {
        return this.generateLevel(name, seed, generator, new HashMap<>());
    }

    /**
     * @deprecated use {#generateLevel(String, LevelCreationOptions)} instead
     */
    @Deprecated
    public boolean generateLevel(String name, long seed, Class<? extends Generator> generator, Map<String, Object> options) {
        return generateLevel(name, seed, generator, options, null);
    }

    /**
     * @deprecated use {#generateLevel(String, LevelCreationOptions, LevelProviderHandle)} instead
     */
    @Deprecated
    public boolean generateLevel(String name, long seed, Class<? extends Generator> generator, Map<String, Object> options, @Nullable Class<? extends LevelProvider> provider) {
        return generateLevel(name, LevelCreationOptions.builder()
                .seed(seed)
                .generator(generator)
                .options(options)
//                .difficulty(Difficulty.byId(this.getDifficulty()))
                .build(), provider);
    }

    public boolean generateLevel(String name, LevelCreationOptions options) {
        return generateLevel(name, options, (LevelProviderHandle) null);
    }

    /**
     * @deprecated use {#generateLevel(String, LevelCreationOptions, LevelProviderHandle)} instead
     */
    @Deprecated
    public boolean generateLevel(String name, LevelCreationOptions options, @Nullable Class<? extends LevelProvider> provider) {
        return generateLevel(name, options, LevelProviderManager.getProviderByClass(provider));
    }

    public boolean generateLevel(String name, LevelCreationOptions options, @Nullable LevelProviderHandle provider) {
        if (Objects.equals(name.trim(), "") || this.isLevelGenerated(name)) {
            return false;
        }

        options.getOptions().computeIfAbsent("preset", key -> this.configuration.getGeneratorSettings());

        if (options.getGenerator() == null) {
            options.setGenerator(Generators.getGenerator(this.getLevelType()));
        }

        if (provider == null) {
//            provider = LevelProviderManager.getProviderByName(this.getConfig().get("level-settings.default-format", "leveldb"));
            //provider = LevelProviderManager.getProviderByName("leveldb"); // force LevelDB
            provider = LevelProviderManager.DEFAULT;
        }

        String path;

        if (new File(name).isAbsolute()) {
            path = name;
        } else {
            path = this.getDataPath() + "worlds/" + name + "/";
        }

        Level level;
        try {
            provider.getInitializer().generate(path, name, options);

            level = new Level(this, name, path, provider);
            this.levels.put(level.getId(), level);

            level.initLevel();
            level.setTickRate(this.baseTickRate);
        } catch (Exception e) {
            log.error(this.getLanguage().translate("nukkit.level.generationError", name), e);
            return false;
        }

        this.getPluginManager().callEvent(new LevelInitEvent(level));

        this.getPluginManager().callEvent(new LevelLoadEvent(level));

        /*this.getLogger().notice(this.getLanguage().translateString("nukkit.level.backgroundGeneration", name));

        int centerX = (int) level.getSpawnLocation().getX() >> 4;
        int centerZ = (int) level.getSpawnLocation().getZ() >> 4;

        TreeMap<String, Integer> order = new TreeMap<>();

        for (int X = -3; X <= 3; ++X) {
            for (int Z = -3; Z <= 3; ++Z) {
                int distance = X * X + Z * Z;
                int chunkX = X + centerX;
                int chunkZ = Z + centerZ;
                order.put(Level.chunkHash(chunkX, chunkZ), distance);
            }
        }

        List<Map.Entry<String, Integer>> sortList = new ArrayList<>(order.entrySet());

        Collections.sort(sortList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        for (String index : order.keySet()) {
            Chunk.Entry entry = Level.getChunkXZ(index);
            level.populateChunk(entry.chunkX, entry.chunkZ, true);
        }*/
        return true;
    }

    public boolean isLevelGenerated(String name) {
        if (Objects.equals(name.trim(), "")) {
            return false;
        }
        String path;
        if (new File(name).isAbsolute()) {
            path = name;
        } else {
            path = this.getDataPath() + "worlds/" + name + "/";
        }

        if (this.getLevelByName(name) == null) {
            if (LevelProviderManager.getProvider(path) == null) {
                return false;
            }
        }

        return true;
    }

    public BaseLang getLanguage() {
        return baseLang;
    }

    public boolean isLanguageForced() {
        return forceLanguage;
    }

    public boolean isRedstoneEnabled() {
        return redstoneEnabled;
    }

    public void setRedstoneEnabled(boolean redstoneEnabled) {
        this.redstoneEnabled = redstoneEnabled;
    }

    public Network getNetwork() {
        return network;
    }

    //Revising later...
    public Config getConfig() {
        return this.config;
    }

    public <T> T getConfig(String variable) {
        return this.getConfig(variable, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfig(String variable, T defaultValue) {
        Object value = this.config.get(variable);
        return value == null ? defaultValue : (T) value;
    }

    public Config getProperties() {
        return this.properties;
    }

    public Object getProperty(String variable) {
        return this.getProperty(variable, null);
    }

    public Object getProperty(String variable, Object defaultValue) {
        return this.properties.exists(variable) ? this.properties.get(variable) : defaultValue;
    }

    public void setPropertyString(String variable, String value) {
        this.properties.set(variable, value);
        this.properties.save();
    }

    public String getPropertyString(String variable) {
        return this.getPropertyString(variable, null);
    }

    public String getPropertyString(String key, String defaultValue) {
        return this.properties.exists(key) ? this.properties.get(key).toString() : defaultValue;
    }

    public int getPropertyInt(String variable) {
        return this.getPropertyInt(variable, null);
    }

    public int getPropertyInt(String variable, Integer defaultValue) {
        return this.properties.exists(variable) ? (!this.properties.get(variable).equals("") ? Integer.parseInt(String.valueOf(this.properties.get(variable))) : defaultValue) : defaultValue;
    }

    public void setPropertyInt(String variable, int value) {
        this.properties.set(variable, value);
        this.properties.save();
    }

    public boolean getPropertyBoolean(String variable) {
        return this.getPropertyBoolean(variable, null);
    }

    public boolean getPropertyBoolean(String variable, Object defaultValue) {
        Object value = this.properties.exists(variable) ? this.properties.get(variable) : defaultValue;
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        switch (String.valueOf(value)) {
            case "on":
            case "true":
            case "1":
            case "yes":
                return true;
        }
        return false;
    }

    public void setPropertyBoolean(String variable, boolean value) {
        this.properties.set(variable, value ? "1" : "0");
        this.properties.save();
    }

    public PluginIdentifiableCommand getPluginCommand(String name) {
        Command command = this.commandMap.getCommand(name);
        if (command instanceof PluginIdentifiableCommand) {
            return (PluginIdentifiableCommand) command;
        } else {
            return null;
        }
    }

    public BanList getNameBans() {
        return this.banByName;
    }

    public BanList getIPBans() {
        return this.banByIP;
    }

    public void addOp(String name) {
        this.operators.set(name.toLowerCase(), true);
        Player player = this.getPlayerExact(name);
        if (player != null) {
            player.recalculatePermissions();
        }
        this.operators.save(true);
    }

    public void removeOp(String name) {
        this.operators.remove(name.toLowerCase());
        Player player = this.getPlayerExact(name);
        if (player != null) {
            player.recalculatePermissions();
        }
        this.operators.save();
    }

    public void addWhitelist(String name) {
        this.whitelist.set(name.toLowerCase(), true);
        this.whitelist.save(true);
    }

    public void removeWhitelist(String name) {
        this.whitelist.remove(name.toLowerCase());
        this.whitelist.save(true);
    }

    public boolean isWhitelisted(String name) {
        return !this.hasWhitelist() || this.operators.exists(name, true) || this.whitelist.exists(name, true);
    }

    public boolean isOp(String name) {
        return name != null && this.operators.exists(name, true);
    }

    public Config getWhitelist() {
        return whitelist;
    }

    public Config getOps() {
        return operators;
    }

    public void reloadWhitelist() {
        this.whitelist.reload();
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public Map<String, List<String>> getCommandAliases() {
        Object section = this.getConfig("aliases");
        Map<String, List<String>> result = new LinkedHashMap<>();
        if (section instanceof Map) {
            for (Map.Entry entry : (Set<Map.Entry>) ((Map) section).entrySet()) {
                List<String> commands = new ArrayList<>();
                String key = (String) entry.getKey();
                Object value = entry.getValue();
                if (value instanceof List) {
                    commands.addAll((List<String>) value);
                } else {
                    commands.add((String) value);
                }

                result.put(key, commands);
            }
        }

        return result;

    }

    public boolean shouldSavePlayerData() {
        return this.configuration.isSavePlayerData();
    }

    public int getPlayerSkinChangeCooldown() {
        return this.getConfig("player.skin-change-cooldown", 30);
    }

    public boolean isAutoCompactionEnabled() {
        return autoCompaction;
    }

    public void setAutoCompactionTicks(int ticks) {
        Preconditions.checkArgument(ticks > 0, "ticks");
        autoCompactionTicks = ticks;
    }

    public int getAutoCompactionTicks() {
        return autoCompactionTicks;
    }

    @Nullable
    public Level getTickingLevel() {
        return tickingLevel;
    }

    /**
     * Checks the current thread against the expected primary thread for the
     * server.
     * <p>
     * <b>Note:</b> this method should not be used to indicate the current
     * synchronized state of the runtime. A current thread matching the main
     * thread indicates that it is synchronized, but a mismatch does not
     * preclude the same assumption.
     *
     * @return true if the current thread matches the expected primary thread,
     * false otherwise
     */
    public boolean isPrimaryThread() {
        return Thread.currentThread() == currentThread;
    }

    public Thread getPrimaryThread() {
        return currentThread;
    }

    public static Server getInstance() {
        return instance;
    }

    private class ConsoleThread extends Thread implements InterruptibleThread {
        ConsoleThread() {
            super("Console");
            setDaemon(true);
        }

        @Override
        public void run() {
            console.start();
        }
    }

}
