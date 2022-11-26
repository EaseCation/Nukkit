package cn.nukkit;

import cn.nukkit.AdventureSettings.Type;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.entity.*;
import cn.nukkit.entity.data.*;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDamageEvent.DamageModifier;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryPickupArrowEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.inventory.InventoryPickupTridentEvent;
import cn.nukkit.event.level.ChunkLoadExceptionEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.inventory.*;
import cn.nukkit.inventory.transaction.CraftingTransaction;
import cn.nukkit.inventory.transaction.EnchantTransaction;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.RepairItemTransaction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.data.ReleaseItemData;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData;
import cn.nukkit.item.*;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.food.Food;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.*;
import cn.nukkit.level.GlobalBlockPaletteInterface.StaticVersion;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.ChunkBlobCache;
import cn.nukkit.level.format.generic.ChunkPacketCache;
import cn.nukkit.level.particle.PunchBlockParticle;
import cn.nukkit.level.sound.LaunchSound;
import cn.nukkit.level.util.AroundPlayerChunkComparator;
import cn.nukkit.math.*;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.Network;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import cn.nukkit.resourcepacks.ResourcePack;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.*;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * author: MagicDroidX & Box
 * Nukkit Project
 */
@Log4j2
public class Player extends EntityHuman implements CommandSender, InventoryHolder, ChunkLoader, IPlayer {

    public static final int SURVIVAL = 0;
    public static final int CREATIVE = 1;
    public static final int ADVENTURE = 2;
    public static final int SPECTATOR = 3;
    public static final int VIEWER = SPECTATOR;

    public static final int SURVIVAL_SLOTS = 36;

    public static final int CRAFTING_SMALL = 0;
    public static final int CRAFTING_BIG = 1;

    public static final int CRAFTING_STONECUTTER = (1 << 3) | 2;
    public static final int CRAFTING_CARTOGRAPHY = (1 << 3) | 3;
    public static final int CRAFTING_LOOM = (1 << 3) | 4;
    public static final int CRAFTING_ANVIL = 2 << 3;
    public static final int CRAFTING_GRINDSTONE = (2 << 3) | 1;
    public static final int CRAFTING_SMITHING_TABLE = (2 << 3) | 2;
    public static final int CRAFTING_ENCHANT = 3 << 3;

    public static final float DEFAULT_SPEED = 0.1f;
    public static final float MAXIMUM_SPEED = 0.5f;

    public static final int PERMISSION_CUSTOM = 3;
    public static final int PERMISSION_OPERATOR = 2;
    public static final int PERMISSION_MEMBER = 1;
    public static final int PERMISSION_VISITOR = 0;

    public static final int WORKBENCH_WINDOW_ID = 1; // crafting_table, stonecutter, loom, cartography_table
    public static final int ANVIL_WINDOW_ID = 2; // anvil, grindstone, smithing_table
    public static final int ENCHANT_WINDOW_ID = 3;
    public static final int BEACON_WINDOW_ID = 4;
    public static final int TRADE_WINDOW_ID = 5;
    public static final int FIRST_AVAILABLE_WINDOW_ID = 6;

    protected static final int RESOURCE_PACK_CHUNK_SIZE = 8 * 1024; // 8KB

    /**
     * Prevent the player inside into unloaded chunks.
     */
    public static final int TELEPORT_TEMP_Y = Short.MAX_VALUE;

    protected final SourceInterface interfaz;

    public boolean playedBefore;
    public boolean spawned = false;
    public boolean loggedIn = false;
    public boolean locallyInitialized = false;
    public int gamemode;
    public long lastBreak;

    protected int windowCnt = FIRST_AVAILABLE_WINDOW_ID;

    protected int closingWindowId = Integer.MIN_VALUE;

    protected final BiMap<Inventory, Integer> windows = HashBiMap.create();
    protected final BiMap<Integer, Inventory> windowIndex = windows.inverse();
    protected IntSet permanentWindows = new IntOpenHashSet();
    protected int messageCounter = 2;

    private String clientSecret;

    public Vector3 speed = null;
    public int attackCriticalThisJump = 0;

//    public final Set<String> achievements = new ObjectOpenHashSet<>();

    public int craftingType = CRAFTING_SMALL;
    public RecipeTag recipeTag = RecipeTag.CRAFTING_TABLE;

    protected PlayerUIInventory playerUIInventory;
    protected CraftingGrid craftingGrid;
    protected CraftingTransaction craftingTransaction;
    protected EnchantTransaction enchantTransaction;
    protected RepairItemTransaction repairItemTransaction;

    public long creationTime;

    protected long randomClientId;

    protected Vector3 forceMovement = null;

    protected Vector3 teleportPosition = null;
    protected long teleportChunkIndex;
    protected boolean teleportChunkLoaded = true;
    protected boolean lastImmobile = false;

    protected boolean connected = true;
    protected final InetSocketAddress socketAddress;
    protected boolean removeFormat = true;

    protected String username;
    protected String iusername;
    protected String displayName;

    protected int startAction = -1;
    protected long startActionTimestamp = -1;

    protected Vector3 sleeping = null;
    protected Long clientID;

    private Integer loaderId;

    protected float stepHeight = 0.6f;

    public Long2BooleanMap usedChunks = new Long2BooleanOpenHashMap();

    protected int chunkLoadCount = 0;
    protected Long2IntMap loadQueue = new Long2IntOpenHashMap();
    protected int nextChunkOrderRun = 5;
    protected final AroundPlayerChunkComparator chunkComparator = new AroundPlayerChunkComparator(this);

    protected Map<UUID, Player> hiddenPlayers = new ConcurrentHashMap<>();

    protected Vector3 newPosition = null;

    protected int chunkRadius;
    protected int viewDistance;
    protected int chunksPerTick;
    protected final int spawnThreshold;

    protected Position spawnPosition;
    protected Position spawnBlockPosition;

    protected int inAirTicks = 0;
    protected int startAirTicks = 5;

    protected AdventureSettings adventureSettings;

    protected boolean checkMovement = true;

    private PermissibleBase perm;

    private int exp = 0;
    private int expLevel = 0;

    protected PlayerFood foodData = null;

    private Entity killer = null;

    private final AtomicReference<Locale> locale = new AtomicReference<>(null);

    private int hash;

    private String buttonText = "";

    protected boolean enableClientCommand = true;

    private BlockEnderChest viewingEnderChest = null;

    //TODO: use itemCooldownMap
    protected int lastEnderPearl = 20;
    protected int lastChorusFruitTeleport = 20;
    protected int lastGoatHornPlay = 140;

    private LoginChainData loginChainData;

    public Block breakingBlock = null;

    public int pickedXPOrb = 0;

    protected int protocol;

    protected int formWindowCount = 0;
    protected Int2ObjectMap<FormWindow> formWindows = new Int2ObjectOpenHashMap<>();
    protected Int2ObjectMap<FormWindow> serverSettings = new Int2ObjectOpenHashMap<>();

    protected Long2ObjectMap<DummyBossBar> dummyBossBars = new Long2ObjectOpenHashMap<>();

    protected Map<String, ResourcePack> resourcePacks = new Object2ObjectLinkedOpenHashMap<>();
    protected Map<String, ResourcePack> behaviourPacks = new Object2ObjectLinkedOpenHashMap<>();
    protected boolean forceResources = false;

    protected AsyncTask preLoginEventTask = null;
    protected boolean shouldLogin = false;

    public EntityFishingHook fishing = null;

    public long lastSkinChange;

    protected double lastRightClickTime = 0.0;
    protected Vector3 lastRightClickPos = null;

    protected boolean isNetEaseClient = false;

    // EC：确保在一个服务器中，每个玩家的皮肤只发一遍
    public final List<UUID> sentSkins = new ObjectArrayList<>();

    public int getStartActionTick() {
        return startAction;
    }

    public long getStartActionTimestamp() {
        return startActionTimestamp;
    }

    public void startAction() {
        this.startAction = this.server.getTick();
        this.startActionTimestamp = System.currentTimeMillis();
    }

    public void stopAction() {
        this.startAction = -1;
        this.startActionTimestamp = -1;
    }

    public int getLastEnderPearlThrowingTick() {
        return lastEnderPearl;
    }

    public void onThrowEnderPearl() {
        this.lastEnderPearl = this.server.getTick();
    }

    public int getLastChorusFruitTeleport() {
        return lastChorusFruitTeleport;
    }

    public void onChorusFruitTeleport() {
        this.lastChorusFruitTeleport = this.server.getTick();
    }

    public int getLastGoatHornPlay() {
        return lastGoatHornPlay;
    }

    public void onGoatHornPlay() {
        this.lastGoatHornPlay = this.server.getTick();
    }

    public BlockEnderChest getViewingEnderChest() {
        return viewingEnderChest;
    }

    public void setViewingEnderChest(BlockEnderChest chest) {
        if (chest == null && this.viewingEnderChest != null) {
            this.viewingEnderChest.getViewers().remove(this);
        } else if (chest != null) {
            chest.getViewers().add(this);
        }
        this.viewingEnderChest = chest;
    }

    public TranslationContainer getLeaveMessage() {
        return new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.left", this.getDisplayName());
    }

    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * This might disappear in the future.
     * Please use getUniqueId() instead (IP + clientId + name combo, in the future it'll change to real UUID for online auth)
     * @return random client id
     */
    @Deprecated
    public Long getClientId() {
        return randomClientId;
    }

    @Override
    public boolean isBanned() {
        return this.server.getNameBans().isBanned(this.getName());
    }

    @Override
    public void setBanned(boolean value) {
        if (value) {
            this.server.getNameBans().addBan(this.getName(), null, null, null);
            this.kick(PlayerKickEvent.Reason.NAME_BANNED, "Banned by admin");
        } else {
            this.server.getNameBans().remove(this.getName());
        }
    }

    @Override
    public boolean isWhitelisted() {
        return this.server.isWhitelisted(this.getName().toLowerCase());
    }

    @Override
    public void setWhitelisted(boolean value) {
        if (value) {
            this.server.addWhitelist(this.getName().toLowerCase());
        } else {
            this.server.removeWhitelist(this.getName().toLowerCase());
        }
    }

    @Override
    public Player getPlayer() {
        return this;
    }

    @Override
    public Long getFirstPlayed() {
        return this.namedTag != null ? this.namedTag.getLong("firstPlayed") : null;
    }

    @Override
    public Long getLastPlayed() {
        return this.namedTag != null ? this.namedTag.getLong("lastPlayed") : null;
    }

    @Override
    public boolean hasPlayedBefore() {
        return this.playedBefore;
    }

    public AdventureSettings getAdventureSettings() {
        return adventureSettings;
    }

    public void setAdventureSettings(AdventureSettings adventureSettings) {
        this.adventureSettings = adventureSettings.clone(this);
        this.adventureSettings.update();
    }

    public void resetInAirTicks() {
        this.inAirTicks = 0;
    }

    @Deprecated
    public void setAllowFlight(boolean value) {
        this.getAdventureSettings().set(Type.ALLOW_FLIGHT, value);
        this.getAdventureSettings().update();
    }

    @Deprecated
    public boolean getAllowFlight() {
        return this.getAdventureSettings().get(Type.ALLOW_FLIGHT);
    }

    public void setAllowModifyWorld(boolean value) {
        this.getAdventureSettings().set(Type.WORLD_IMMUTABLE, !value);
        this.getAdventureSettings().set(Type.MINE, value);
        this.getAdventureSettings().set(Type.BUILD, value);
        this.getAdventureSettings().update();
    }

    public void setAllowInteract(boolean value) {
        setAllowInteract(value, value);
    }

    public void setAllowInteract(boolean value, boolean containers) {
        this.getAdventureSettings().set(Type.WORLD_IMMUTABLE, !value);
        this.getAdventureSettings().set(Type.DOORS_AND_SWITCHED, value);
        this.getAdventureSettings().set(Type.OPEN_CONTAINERS, containers);
        this.getAdventureSettings().update();
    }

    @Deprecated
    public void setAutoJump(boolean value) {
        this.getAdventureSettings().set(Type.AUTO_JUMP, value);
        this.getAdventureSettings().update();
    }

    @Deprecated
    public boolean hasAutoJump() {
        return this.getAdventureSettings().get(Type.AUTO_JUMP);
    }

    @Override
    public void spawnTo(Player player) {
        if (this.spawned && player.spawned && this.isAlive() && player.isAlive() && player.getLevel() == this.level && player.canSee(this) && !this.isSpectator()) {
            super.spawnTo(player);
        }
    }

    @Override
    public Server getServer() {
        return this.server;
    }

    public boolean getRemoveFormat() {
        return removeFormat;
    }

    public void setRemoveFormat() {
        this.setRemoveFormat(true);
    }

    public void setRemoveFormat(boolean remove) {
        this.removeFormat = remove;
    }

    public boolean canSee(Player player) {
        return !this.hiddenPlayers.containsKey(player.getUniqueId());
    }

    public void hidePlayer(Player player) {
        if (this == player) {
            return;
        }
        this.hiddenPlayers.put(player.getUniqueId(), player);
        player.despawnFrom(this);
    }

    public void showPlayer(Player player) {
        if (this == player) {
            return;
        }
        this.hiddenPlayers.remove(player.getUniqueId());
        if (player.isOnline()) {
            player.spawnTo(this);
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        if ((this.isSurvival() || this.isAdventure()) && entity instanceof EntityFishingHook) return true;
        return false;
    }

    @Override
    public void resetFallDistance() {
        super.resetFallDistance();
        if (this.inAirTicks != 0) {
            this.startAirTicks = 5;
        }
        this.inAirTicks = 0;
        this.highestPosition = this.y;
        this.attackCriticalThisJump = 0;
    }

    @Override
    public boolean isOnline() {
        return this.connected && this.loggedIn;
    }

    @Override
    public boolean isOp() {
        return this.server.isOp(this.getName());
    }

    @Override
    public void setOp(boolean value) {
        if (value == this.isOp()) {
            return;
        }

        if (value) {
            this.server.addOp(this.getName());
        } else {
            this.server.removeOp(this.getName());
        }

        this.recalculatePermissions();
        this.getAdventureSettings().set(Type.TELEPORT, hasPermission("nukkit.command.teleport"));
        this.getAdventureSettings().set(Type.OPERATOR, value);
        this.getAdventureSettings().update();
        this.sendCommandData();
    }

    @Override
    public boolean isPermissionSet(String name) {
        return this.perm.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return this.perm.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String name) {
        return this.perm != null && this.perm.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return this.perm.hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.addAttachment(plugin, null);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name) {
        return this.addAttachment(plugin, name, null);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        return this.perm.addAttachment(plugin, name, value);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        this.perm.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_USERS, this);
        this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);

        if (this.perm == null) {
            return;
        }

        this.perm.recalculatePermissions();

        if (this.hasPermission(Server.BROADCAST_CHANNEL_USERS)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this);
        }

        if (this.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);
        }

        if (this.isEnableClientCommand() && spawned) this.sendCommandData();
    }

    public boolean isEnableClientCommand() {
        return this.enableClientCommand;
    }

    public void setEnableClientCommand(boolean enable) {
        this.enableClientCommand = enable;
        SetCommandsEnabledPacket pk = new SetCommandsEnabledPacket();
        pk.enabled = enable;
        this.dataPacket(pk);
        if (enable) this.sendCommandData();
    }

    public void sendCommandData() {
        AvailableCommandsPacket pk = new AvailableCommandsPacket();
        Map<String, CommandDataVersions> data = new Object2ObjectOpenHashMap<>();
        int count = 0;
        for (Command command : this.server.getCommandMap().getCommands().values()) {
            if (!command.testPermissionSilent(this) || !command.isRegistered() || "help".equals(command.getName())) {
                continue;
            }
            ++count;
            CommandDataVersions data0 = command.generateCustomCommandData(this);
            data.put(command.getName(), data0);
        }
        if (count > 0) {
            pk.commands = data;
            this.dataPacket(pk);
        }
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.perm.getEffectivePermissions();
    }

    public Player(SourceInterface interfaz, Long clientID, InetSocketAddress socketAddress) {
        super(null, new CompoundTag());
        this.interfaz = interfaz;
        this.perm = new PermissibleBase(this);
        this.server = Server.getInstance();
        this.lastBreak = Long.MAX_VALUE;
        this.socketAddress = socketAddress;
        this.clientID = clientID;
        this.loaderId = Level.generateChunkLoaderId(this);
        this.chunksPerTick = this.server.getConfig("chunk-sending.per-tick", 4);
        this.spawnThreshold = this.server.getConfig("chunk-sending.spawn-threshold", 56);
        this.spawnPosition = null;
        this.gamemode = this.server.getGamemode();
        this.setLevel(this.server.getDefaultLevel());
        this.viewDistance = this.server.getViewDistance();
        this.chunkRadius = viewDistance;
        //this.newPosition = new Vector3(0, 0, 0);
        this.boundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);
        this.lastSkinChange = -1;

        this.uuid = null;
        this.rawUUID = null;

        this.creationTime = System.currentTimeMillis();
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.addDefaultWindows();
    }

    public boolean isPlayer() {
        return true;
    }

    /**
     * @deprecated will be removed in the future
     */
    @Deprecated
    public void removeAchievement(String achievementId) {
//        achievements.remove(achievementId);
    }

    /**
     * @deprecated will be removed in the future
     */
    @Deprecated
    public boolean hasAchievement(String achievementId) {
//        return achievements.contains(achievementId);
        return false;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        if (this.spawned) {
            //TODO this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getDisplayName(), this.getSkin(), this.getLoginChainData().getXUID());
        }
    }

    @Override
    public void setSkin(Skin skin) {
        super.setSkin(skin);
        if (this.spawned) {
            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getDisplayName(), skin, this.getLoginChainData().getXUID(), this.getServer().getOnlinePlayers().values().stream().filter(p -> p.sentSkins.contains(this.getUniqueId())).collect(Collectors.toList()));
        }
    }

    public String getAddress() {
        return this.socketAddress.isUnresolved() ? this.socketAddress.getHostName() : this.socketAddress.getAddress().getHostAddress();
    }

    public int getPort() {
        return this.socketAddress.getPort();
    }

    public InetSocketAddress getSocketAddress() {
        return this.socketAddress;
    }

    public Position getNextPosition() {
        return this.newPosition != null ? new Position(this.newPosition.x, this.newPosition.y, this.newPosition.z, this.level) : this.getPosition();
    }

    public boolean isSleeping() {
        return this.sleeping != null;
    }

    public int getInAirTicks() {
        return this.inAirTicks;
    }

    /**
     * Returns whether the player is currently using an item (right-click and hold).
     *
     * @return bool
     */
    public boolean isUsingItem() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_ACTION) && this.startAction > -1;
    }

    public void setUsingItem(boolean value) {
        this.startAction = value ? this.server.getTick() : -1;
        this.startActionTimestamp = value ? System.currentTimeMillis() : -1;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, value);
    }

    public String getButtonText() {
        return this.buttonText;
    }

    public void setButtonText(String text) {
        if (this.buttonText.equals(text)) {
            return;
        }
        this.buttonText = text;
        this.setDataProperty(new StringEntityData(Entity.DATA_INTERACTIVE_TAG, this.buttonText));
    }

    @Override
    protected boolean switchLevel(Level targetLevel) {
        Level oldLevel = this.level;
        if (super.switchLevel(targetLevel)) {
            // Remove old chunks
            for (long index : new LongArrayList(this.usedChunks.keySet())) {
                int chunkX = Level.getHashX(index);
                int chunkZ = Level.getHashZ(index);
                this.unloadChunk(chunkX, chunkZ, oldLevel);
            }

            this.usedChunks = new Long2BooleanOpenHashMap();

            if (this.onLevelSwitch()) {
                return true;
            }

            SetSpawnPositionPacket spawnPosition = new SetSpawnPositionPacket();
            spawnPosition.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
            Position spawn = level.getSpawnLocation();
            spawnPosition.x = spawn.getFloorX();
            spawnPosition.y = spawn.getFloorY();
            spawnPosition.z = spawn.getFloorZ();
            this.dataPacket(spawnPosition);

            //forceSendEmptyChunks();

            SetTimePacket pk = new SetTimePacket();
            pk.time = targetLevel.getTime();
            this.dataPacket(pk);

            GameRulesChangedPacket gameRulesChanged = new GameRulesChangedPacket();
            gameRulesChanged.gameRules = level.getGameRules();
            this.dataPacket(gameRulesChanged);

            //int distance = this.viewDistance * 2 * 16 * 2;
            //this.sendPosition(this.add(distance, 0, distance), this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);
            return true;
        }
        return false;
    }

    // Remove old chunks
    public void removeAllChunks() {
        for (long index :new LongArrayList(this.usedChunks.keySet())) {
            int chunkX = Level.getHashX(index);
            int chunkZ = Level.getHashZ(index);
            this.unloadChunk(chunkX, chunkZ, this.level);
        }
    }

    public void unloadChunk(int x, int z) {
        this.unloadChunk(x, z, null);
    }

    public void unloadChunk(int x, int z, Level level) {
        level = level == null ? this.level : level;
        long index = Level.chunkHash(x, z);
        if (this.usedChunks.containsKey(index)) {
            for (Entity entity : level.getChunkEntities(x, z).values()) {
                if (entity != this) {
                    entity.despawnFrom(this);
                }
            }

            this.usedChunks.remove(index);
        }
        level.unregisterChunkLoader(this, x, z);
        this.loadQueue.remove(index);
    }

    public Position getSpawn() {
        if (this.spawnBlockPosition != null && this.spawnBlockPosition.level != null && this.spawnBlockPosition.equalsVec(this.spawnPosition) && this.spawnBlockPosition.level == this.spawnPosition.level) {
            Block block = this.spawnBlockPosition.level.getBlock(this.spawnBlockPosition);
            if (block.getId() == Block.BLOCK_BED && (block.getDamage() & 0x8) == 0x8) {
                return this.spawnPosition.floor().add(0.5, 0.1, 0.5);
            } else {
                this.setSpawnBlockPosition(null);
            }
        }

        if (this.spawnPosition != null && this.spawnPosition.getLevel() != null) {
            return this.spawnPosition.floor().add(0.5, 0.1, 0.5);
        } else {
            return this.server.getDefaultLevel().getSafeSpawn().floor().add(0.5, 0.1, 0.5);
        }
    }

    public void sendChunk(int x, int z, int subChunkCount, ChunkBlobCache chunkBlobCache, DataPacket packet) {
        if (!this.connected) {
            return;
        }

        this.usedChunks.put(Level.chunkHash(x, z), true);
        this.chunkLoadCount++;

        this.dataPacket(packet);

        for (BlockEntity blockEntity : this.level.getChunkBlockEntities(x, z).values()) {
            if (!(blockEntity instanceof BlockEntitySpawnable)) {
                continue;
            }
            ((BlockEntitySpawnable) blockEntity).spawnTo(this);
        }

        if (this.spawned) {
            for (Entity entity : this.level.getChunkEntities(x, z).values()) {
                if (this != entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }
    }

    public void sendChunk(int x, int z, int subChunkCount, ChunkBlobCache chunkBlobCache, byte[] payload, byte[] subModePayload) {
        if (!this.connected) {
            return;
        }

        this.usedChunks.put(Level.chunkHash(x, z), true);
        this.chunkLoadCount++;

        FullChunkDataPacket pk = new FullChunkDataPacket();
        pk.chunkX = x;
        pk.chunkZ = z;
        pk.data = payload;
        this.dataPacket(pk);

        for (BlockEntity entity : this.level.getChunkBlockEntities(x, z).values()) {
            if (!(entity instanceof BlockEntitySpawnable)) {
                continue;
            }
            ((BlockEntitySpawnable) entity).spawnTo(this);
        }

        if (this.spawned) {
            for (Entity entity : this.level.getChunkEntities(x, z).values()) {
                if (this != entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }
    }

    protected void sendNextChunk() {
        if (!this.connected) {
            return;
        }

        Timings.playerChunkSendTimer.startTiming();

        this.sendQueuedChunk();

        if ((this.canDoFirstSpawn() || System.currentTimeMillis() - this.creationTime > 15000) && !this.spawned && this.teleportPosition == null) {
            this.doFirstSpawn();
        }
        Timings.playerChunkSendTimer.stopTiming();
    }

    protected boolean canDoFirstSpawn() {
        return this.chunkLoadCount >= this.spawnThreshold;
    }

    protected boolean canSendQueuedChunk() {
        return true;
    }

    protected boolean sendQueuedChunk() {
        if (!this.canSendQueuedChunk()) {
            return false;
        }
        boolean success = false;
        int count = 0;

        /*List<Long2IntMap.Entry> entryList = new ObjectArrayList<>(this.loadQueue.long2IntEntrySet());
        if (entryList.size() + chunkLoadCount > spawnThreshold) {
            entryList.sort(Comparator.comparingInt(Map.Entry::getValue));
        }*/
        LongList indexes = new LongArrayList(this.loadQueue.keySet());
        indexes.unstableSort(this.chunkComparator);

        for (long index : indexes) {
            if (count >= this.chunksPerTick) {
                // You should count the active transactions for each client, and only send new LevelChunkPackets if there aren't too many active transactions.
                // In Vanilla, depending on the connection quality, we only allow between 1 and 8 concurrent transactions.
                break;
            }
            int chunkX = Level.getHashX(index);
            int chunkZ = Level.getHashZ(index);

            ++count;

            this.usedChunks.put(index, false);
            this.level.registerChunkLoader(this, chunkX, chunkZ, false);

            try {
                if (!this.level.populateChunk(chunkX, chunkZ)) {
                    if (this.spawned && this.teleportPosition == null) {
                        continue;
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                this.sendMessage(TextFormat.RED + "Chunk " + chunkX + "," + chunkZ + " load&send failed!");
                this.sendMessage(e.getMessage());
                if (e.getCause() != null) this.sendMessage(Utils.getExceptionMessage(e.getCause()));
                log.warn("Chunk " + chunkX + "," + chunkZ + " load&send failed!", e);
                getServer().getPluginManager().callEvent(new ChunkLoadExceptionEvent(chunk, e));
            }

            this.loadQueue.remove(index);

            PlayerChunkRequestEvent ev = new PlayerChunkRequestEvent(this, chunkX, chunkZ);
            this.server.getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                this.level.requestChunk(chunkX, chunkZ, this);
                success = true;
            }
        }

        return success;
    }

    protected void sendRecipeList() {
        this.dataPacket(CraftingManager.packet);
    }

    protected void firstRespawn(Position pos) {
        RespawnPacket respawnPacket = new RespawnPacket();
        respawnPacket.x = (float) pos.x;
        respawnPacket.y = (float) pos.y + this.getEyeHeight();
        respawnPacket.z = (float) pos.z;
        this.dataPacket(respawnPacket);
    }

    protected void doFirstSpawn() {
        this.locallyInitialized = true;  // 在1.6及以上版本，doFirstSpawn 后，收到客户端的 SetLocalPositionAndLookPacket 后，会设置locallyInitialized为true。
        this.sendPotionEffects(this);
        this.sendData(this);

        SetTimePacket setTimePacket = new SetTimePacket();
        setTimePacket.time = this.level.getTime();
        this.dataPacket(setTimePacket);

        Position pos = this.level.getSafeSpawn(this);

        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(this, pos);

        this.server.getPluginManager().callEvent(respawnEvent);

        pos = respawnEvent.getRespawnPosition();

        if (this.getHealth() < 1) {
            pos = this.getSpawn();
        }

        this.firstRespawn(pos);

        this.sendPlayStatus(PlayStatusPacket.PLAYER_SPAWN);

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(this,
                new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.joined", new String[]{
                        this.getDisplayName()
                })
        );

        this.server.getPluginManager().callEvent(playerJoinEvent);

        this.spawned = true;

        if (playerJoinEvent.getJoinMessage().toString().trim().length() > 0) {
            this.server.broadcastMessage(playerJoinEvent.getJoinMessage());
        }

        this.inventory.sendContents(this);
        this.inventory.sendArmorContents(this);

        this.noDamageTicks = 60;

        this.sendRecipeList();

        if (this.gamemode == Player.SPECTATOR) {
            InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.inventoryId = ContainerIds.CREATIVE;
            this.dataPacket(inventoryContentPacket);
        } else {
            this.sendCreativeContents();
        }

        for (long index : this.usedChunks.keySet()) {
            int chunkX = Level.getHashX(index);
            int chunkZ = Level.getHashZ(index);
            for (Entity entity : this.level.getChunkEntities(chunkX, chunkZ).values()) {
                if (this != entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }

        int experience = this.getExperience();
        if (experience != 0) {
            this.sendExperience(experience);
        }

        int level = this.getExperienceLevel();
        if (level != 0) {
            this.sendExperienceLevel(this.getExperienceLevel());
        }

        this.teleport(pos, null); // Prevent PlayerTeleportEvent during player spawn

        if (!this.isSpectator()) {
            this.spawnToAll();
        }

        //Weather
        this.getLevel().sendWeather(this);

        //FoodLevel
        PlayerFood food = this.getFoodData();
        if (food.getLevel() != food.getMaxLevel()) {
            food.sendFoodLevel();
        }
    }

    protected boolean orderChunks() {
        if (!this.connected) {
            return false;
        }

        Timings.playerChunkOrderTimer.startTiming();

        this.nextChunkOrderRun = 200;

        loadQueue.clear();
        Long2BooleanMap lastChunk = new Long2BooleanOpenHashMap(this.usedChunks);

        int centerX = (int) this.x >> 4;
        int centerZ = (int) this.z >> 4;

        int radius = spawned ? this.chunkRadius : Mth.ceil(Math.sqrt(spawnThreshold));

        int radiusSqr = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            int xx = x * x;
            int chunkX = x + centerX;
            for (int z = -radius; z <= radius; z++) {
                int distanceSqr = xx + z * z;
                if (distanceSqr <= radiusSqr) {
                    int chunkZ = z + centerZ;
                    long index = Level.chunkHash(chunkX, chunkZ);
                    Boolean value = this.usedChunks.get(index);
                    if (value == null || !value) {
                        this.loadQueue.put(index, distanceSqr);
                    }
                    lastChunk.remove(index);
                }
            }
        }

        for (long index : lastChunk.keySet()) {
            this.unloadChunk(Level.getHashX(index), Level.getHashZ(index));
        }

        Timings.playerChunkOrderTimer.stopTiming();
        return true;
    }

    @Deprecated
    public boolean batchDataPacket(DataPacket packet) {
        return this.dataPacket(packet);
    }

    /**
     * 0 is true
     * -1 is false
     * other is identifer
     * @param packet packet to send
     * @return packet successfully sent
     */
    public boolean dataPacket(DataPacket packet) {
        if (!this.connected) {
            return false;
        }

        try (Timing timing = Timings.getSendDataPacketTiming(packet)) {
            DataPacketSendEvent ev = new DataPacketSendEvent(this, packet);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }

            this.interfaz.putPacket(this, packet, false, true);
        }
        return true;
    }

    @Deprecated
    public int dataPacket(DataPacket packet, boolean needACK) {
        return dataPacket(packet) ? 1 : 0;
    }

    /**
     * 0 is true
     * -1 is false
     * other is identifer
     * @param packet packet to send
     * @return packet successfully sent
     */
    @Deprecated
    public boolean directDataPacket(DataPacket packet) {
        return this.dataPacket(packet);
    }

    @Deprecated
    public int directDataPacket(DataPacket packet, boolean needACK) {
        return this.dataPacket(packet) ? 1 : 0;
    }

    public int getPing() {
        return this.interfaz.getNetworkLatency(this);
    }

    public boolean sleepOn(Vector3 pos) {
        if (!this.isOnline()) {
            return false;
        }

        for (Entity p : this.level.getNearbyEntities(this.boundingBox.grow(2, 1, 2), this)) {
            if (p instanceof Player) {
                if (((Player) p).sleeping != null && pos.distance(((Player) p).sleeping) <= 0.1) {
                    return false;
                }
            }
        }

        PlayerBedEnterEvent ev = new PlayerBedEnterEvent(this, this.level.getBlock(pos));
        if (this.riding != null && !this.riding.dismountEntity(this)) {
            ev.setCancelled();
        }
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }

        this.sleeping = pos.clone();

        this.setDataProperty(new FloatEntityData(DATA_BOUNDING_BOX_WIDTH, 0.2f));
        this.setDataProperty(new FloatEntityData(DATA_BOUNDING_BOX_HEIGHT, 0.2f));
        this.setDataProperty(new IntPositionEntityData(DATA_PLAYER_BED_POSITION, (int) pos.x, (int) pos.y, (int) pos.z));
        this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, true);

        this.setSpawn(pos);

        this.level.sleepTicks = 100;

        return true;
    }

    public void setSpawn(Vector3 pos) {
        Level level;
        if (!(pos instanceof Position)) {
            level = this.level;
        } else {
            level = ((Position) pos).getLevel();
        }
        this.spawnPosition = new Position(pos.x, pos.y, pos.z, level);
        if (this.isOnline()) {
            SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
            pk.spawnType = SetSpawnPositionPacket.TYPE_PLAYER_SPAWN;
            pk.x = (int) this.spawnPosition.x;
            pk.y = (int) this.spawnPosition.y;
            pk.z = (int) this.spawnPosition.z;
            this.dataPacket(pk);
        }
    }

    public void setSpawnBlockPosition(Position pos) {
        if (pos == null) {
            if (this.spawnBlockPosition != null && this.spawnBlockPosition.level != null && this.spawnBlockPosition.equalsVec(this.spawnPosition) && this.spawnBlockPosition.level == this.spawnPosition.level) {
                this.setSpawn(this.server.getDefaultLevel().getSafeSpawn());
            }
            this.spawnBlockPosition = null;
            return;
        }

        if (pos.level == null) {
            pos.level = this.level;
        }
        this.spawnBlockPosition = pos;
        this.setSpawn(pos);
        this.sendMessage(new TranslationContainer("tile.bed.respawnSet"));
    }

    public void stopSleep() {
        if (this.sleeping != null) {
            this.server.getPluginManager().callEvent(new PlayerBedLeaveEvent(this, this.level.getBlock(this.sleeping)));

            this.sleeping = null;
            this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, false);
            this.setDataProperty(new FloatEntityData(DATA_BOUNDING_BOX_WIDTH, this.getWidth()));
            this.setDataProperty(new FloatEntityData(DATA_BOUNDING_BOX_HEIGHT, this.getHeight()));

            this.level.sleepTicks = 0;

            AnimatePacket pk = new AnimatePacket();
            pk.eid = this.id;
            pk.action = AnimatePacket.Action.WAKE_UP;
            this.dataPacket(pk);
        }
    }

    /**
     * @deprecated will be removed in the future
     */
    @Deprecated
    public boolean awardAchievement(String achievementId) {
        return false;
        /*if (!Server.getInstance().getPropertyBoolean("achievements", true)) {
            return false;
        }

        Achievement achievement = Achievement.achievements.get(achievementId);

        if (achievement == null || hasAchievement(achievementId)) {
            return false;
        }

        for (String id : achievement.requires) {
            if (!this.hasAchievement(id)) {
                return false;
            }
        }
        PlayerAchievementAwardedEvent event = new PlayerAchievementAwardedEvent(this, achievementId);
        this.server.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        this.achievements.add(achievementId);
        achievement.broadcast(this);
        return true;*/
    }

    public int getGamemode() {
        return gamemode;
    }

    /**
     * Returns a client-friendly gamemode of the specified real gamemode
     * This function takes care of handling gamemodes known to MCPE (as of 1.1.0.3, that includes Survival, Creative and Adventure)
     * <p>
     * TODO: remove this when Spectator Mode gets added properly to MCPE
     */
    private static int getClientFriendlyGamemode(int gamemode) {
        gamemode &= 0x03;
        if (gamemode == Player.SPECTATOR) {
            return Player.CREATIVE;
        }
        return gamemode;
    }

    public static int vanillaGamemodeToNukkitGamemode(int vanillaGameType) {
        switch (vanillaGameType) {
            case 0:
                return SURVIVAL;
            case 1:
                return CREATIVE;
            case 2:
                return ADVENTURE;
            case 3:
            case 4:
            case 6:
                return SPECTATOR;
            case 5:
            default:
                return -1;
        }
    }

    public boolean setGamemode(int gamemode) {
        return this.setGamemode(gamemode, false, null);
    }

    public boolean setGamemode(int gamemode, boolean clientSide) {
        return this.setGamemode(gamemode, clientSide, null);
    }

    public boolean setGamemode(int gamemode, boolean clientSide, AdventureSettings newSettings) {
        if (gamemode < 0 || gamemode > 3 || this.gamemode == gamemode) {
            return false;
        }

        if (newSettings == null) {
            newSettings = this.getAdventureSettings().clone(this);
            newSettings.set(Type.WORLD_IMMUTABLE, gamemode == ADVENTURE || gamemode == SPECTATOR);
            newSettings.set(Type.MINE, gamemode != ADVENTURE && gamemode != SPECTATOR);
            newSettings.set(Type.BUILD, gamemode != ADVENTURE && gamemode != SPECTATOR);
            newSettings.set(Type.ALLOW_FLIGHT, gamemode == CREATIVE || gamemode == SPECTATOR);
            newSettings.set(Type.NO_CLIP, gamemode == SPECTATOR);
            if (gamemode == SPECTATOR) {
                newSettings.set(Type.FLYING, true);
            } else if (gamemode != CREATIVE) {
                newSettings.set(Type.FLYING, false);
            }
            newSettings.set(Type.NO_PVM, gamemode == SPECTATOR);
            newSettings.set(Type.NO_MVP, gamemode == SPECTATOR);
            newSettings.set(Type.DOORS_AND_SWITCHED, gamemode != SPECTATOR);
            newSettings.set(Type.OPEN_CONTAINERS, gamemode != SPECTATOR);
            newSettings.set(Type.ATTACK_PLAYERS, gamemode != SPECTATOR);
            newSettings.set(Type.ATTACK_MOBS, gamemode != SPECTATOR);
            newSettings.set(Type.INSTABUILD, gamemode == CREATIVE);
            newSettings.set(Type.INVULNERABLE, gamemode == CREATIVE || gamemode == SPECTATOR);
        }

        PlayerGameModeChangeEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerGameModeChangeEvent(this, gamemode, newSettings));

        if (ev.isCancelled()) {
            return false;
        }

        this.gamemode = gamemode;

        if (this.isSpectator()) {
            this.keepMovement = true;
            this.onGround = false;
            this.despawnFromAll();
        } else {
            this.keepMovement = false;
            this.spawnToAll();
        }

        this.namedTag.putInt("playerGameType", this.gamemode);

        if (!clientSide) {
            SetPlayerGameTypePacket pk = new SetPlayerGameTypePacket();
            pk.gamemode = getClientFriendlyGamemode(gamemode);
            this.dataPacket(pk);
        }

        this.setAdventureSettings(ev.getNewAdventureSettings());

        if (this.isSpectator()) {
            this.teleport(this, null);
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_SILENT, true);
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, false);

            InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.inventoryId = ContainerIds.CREATIVE;
            this.dataPacket(inventoryContentPacket);
        } else {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_SILENT, false);
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, true);

            InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.inventoryId = ContainerIds.CREATIVE;
            inventoryContentPacket.slots = this.getCreativeItems().toArray(new Item[0]);
            this.dataPacket(inventoryContentPacket);
        }

        this.resetFallDistance();

        if (this.inventory != null) {
            this.inventory.sendContents(this);
            this.inventory.sendHeldItem(this.hasSpawned.values());

            this.sendCreativeContents();
        }

        return true;
    }

    public boolean isSurvival() {
        return (this.gamemode & 0x01) == 0;
    }

    public boolean isCreative() {
        return (this.gamemode & 0x01) > 0;
    }

    public boolean isSpectator() {
        return this.gamemode == 3;
    }

    public boolean isAdventure() {
        return (this.gamemode & 0x02) > 0;
    }

    @Override
    public Item[] getDrops() {
        if (!this.isCreative()) {
            return super.getDrops();
        }

        return new Item[0];
    }

    @Override
    public boolean fastMove(double dx, double dy, double dz) {
        if (dx == 0 && dy == 0 && dz == 0) {
            return true;
        }

        Timings.entityMoveTimer.startTiming();

        AxisAlignedBB newBB = this.boundingBox.getOffsetBoundingBox(dx, dy, dz);

        if (this.isSpectator() || server.getAllowFlight() || !this.level.hasCollision(this, newBB, false)) {
            this.boundingBox = newBB;
        }

        this.x = (this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2;
        this.y = this.boundingBox.getMinY() - this.ySize;
        this.z = (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2;

        this.checkChunks();

        if (!this.isSpectator()) {
            if (!this.onGround || dy != 0) {
                AxisAlignedBB bb = this.boundingBox.clone();
                bb.setMinY(bb.getMinY() - 0.75);

                this.onGround = this.level.getCollisionBlocks(bb).length > 0;
            }
            this.isCollided = this.onGround;
            this.updateFallState(this.onGround);
        }

        Timings.entityMoveTimer.stopTiming();
        return true;
    }

    @Override
    protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        if (!this.onGround || movX != 0 || movY != 0 || movZ != 0) {
            boolean onGround = false;

            AxisAlignedBB bb = this.boundingBox.clone();
            bb.setMaxY(bb.getMinY() + 0.5);
            bb.setMinY(bb.getMinY() - 1);

            AxisAlignedBB realBB = this.boundingBox.clone();
            realBB.setMaxY(realBB.getMinY() + 0.1);
            realBB.setMinY(realBB.getMinY() - 0.2);

            int minX = Mth.floor(bb.getMinX());
            int minY = Mth.floor(bb.getMinY());
            int minZ = Mth.floor(bb.getMinZ());
            int maxX = Mth.floor(bb.getMaxX());
            int maxY = Mth.floor(bb.getMaxY());
            int maxZ = Mth.floor(bb.getMaxZ());

            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.level.getBlock(this.temporalVector.setComponents(x, y, z));

                        if (!block.canPassThrough() && block.collidesWithBB(realBB)) {
                            onGround = true;
                            break;
                        }
                    }
                }
            }

            this.onGround = onGround;
        }

        this.isCollided = this.onGround;
    }

    @Override
    protected void checkBlockCollision() {
        if (this.isSpectator()) {
            if (this.blocksAround == null) {
                this.blocksAround = new ObjectArrayList<>();
            }
            if (this.collisionBlocks == null) {
                this.collisionBlocks = new ObjectArrayList<>();
            }
            return;
        }

        boolean portal = false;

        for (Block block : this.getCollisionBlocks()) {
            if (block.getId() == Block.PORTAL) {
                portal = true;
                continue;
            }

            block.onEntityCollide(this);
        }

        if (portal) {
            inPortalTicks++;
        } else {
            this.inPortalTicks = 0;
        }
    }

    protected void checkNearEntities() {
        for (Entity entity : this.level.getNearbyEntities(this.boundingBox.grow(1, 0.5, 1), this)) {
            entity.scheduleUpdate();

            if (!entity.isAlive() || !this.isAlive()) {
                continue;
            }

            this.pickupEntity(entity, true);
        }
    }

    protected void processMovement(int tickDiff) {
        if (!this.isAlive() || !this.spawned || this.newPosition == null || this.teleportPosition != null || this.isSleeping()) {
            return;
        }
        Vector3 newPos = this.newPosition;
        double distanceSquared = newPos.distanceSquared(this);
        boolean revert = false;
        if ((distanceSquared / ((double) (tickDiff * tickDiff))) > 100 && (newPos.y - this.y) > -5) {
            revert = true;
        } else {
            if (this.chunk == null || !this.chunk.isGenerated()) {
                BaseFullChunk chunk = this.level.getChunk((int) newPos.x >> 4, (int) newPos.z >> 4, false);
                if (chunk == null || !chunk.isGenerated()) {
                    revert = true;
                    this.nextChunkOrderRun = 0;
                } else {
                    if (this.chunk != null) {
                        this.chunk.removeEntity(this);
                    }
                    this.chunk = chunk;
                }
            }
        }

        double tdx = newPos.x - this.x;
        double tdz = newPos.z - this.z;
        double distance = Math.sqrt(tdx * tdx + tdz * tdz);

        if (!revert && distanceSquared != 0) {
            double dx = newPos.x - this.x;
            double dy = newPos.y - this.y;
            double dz = newPos.z - this.z;

            this.fastMove(dx, dy, dz);
            if (this.newPosition == null) {
                return; //maybe solve that in better way
            }

            double diffX = this.x - newPos.x;
            double diffY = this.y - newPos.y;
            double diffZ = this.z - newPos.z;

            double yS = 0.5 + this.ySize;
            if (diffY >= -yS || diffY <= yS) {
                //diffY = 0;
            }

            if (diffX != 0 || diffY != 0 || diffZ != 0) {
                if (this.checkMovement && !server.getAllowFlight() && this.isSurvival()) {
                    // Some say: I cant move my head when riding because the server
                    // blocked my movement
                    if (!this.isSleeping() && this.riding == null && !this.hasEffect(Effect.LEVITATION) && !this.hasEffect(Effect.SLOW_FALLING)) {
                        double diffHorizontalSqr = (diffX * diffX + diffZ * diffZ) / ((double) (tickDiff * tickDiff));
                        if (diffHorizontalSqr > 0.5) {
                            PlayerInvalidMoveEvent ev;
                            this.getServer().getPluginManager().callEvent(ev = new PlayerInvalidMoveEvent(this, true));
                            if (!ev.isCancelled()) {
                                revert = ev.isRevert();

                                if (revert) {
                                    log.warn(this.getServer().getLanguage().translateString("nukkit.player.invalidMove", this.getName()));
                                }
                            }
                        }
                    }
                }

                this.x = newPos.x;
                this.y = newPos.y;
                this.z = newPos.z;
                double radius = this.getWidth() / 2;
                this.boundingBox.setBounds(this.x - radius, this.y, this.z - radius, this.x + radius, this.y + this.getHeight(), this.z + radius);
            }
        }

        Location from = new Location(
                this.lastX,
                this.lastY,
                this.lastZ,
                this.lastYaw,
                this.lastPitch,
                this.level);
        Location to = this.getLocation();

        double delta = Math.pow(this.lastX - to.x, 2) + Math.pow(this.lastY - to.y, 2) + Math.pow(this.z - to.z, 2);
        double deltaAngle = Math.abs(this.lastYaw - to.yaw) + Math.abs(this.lastPitch - to.pitch);

        if (!revert && (delta > 0.0001d || deltaAngle > 1d)) {
            boolean isFirst = this.firstMove;

            this.firstMove = false;
            this.lastX = to.x;
            this.lastY = to.y;
            this.lastZ = to.z;

            this.lastYaw = to.yaw;
            this.lastPitch = to.pitch;

            if (!isFirst) {
                List<Block> blocksAround = new ObjectArrayList<>(this.blocksAround);
                List<Block> collidingBlocks = new ObjectArrayList<>(this.collisionBlocks);

                PlayerMoveEvent ev = new PlayerMoveEvent(this, from, to);

                this.blocksAround = null;
                this.collisionBlocks = null;

                this.server.getPluginManager().callEvent(ev);

                if (!(revert = ev.isCancelled())) { //Yes, this is intended
                    if (!to.equals(ev.getTo())) { //If plugins modify the destination
                        this.teleport(ev.getTo(), null);
                    } else {
                        this.addMovement(this.x, this.y, this.z, this.yaw, this.pitch, this.yaw);
                    }
                } else {
                    this.blocksAround = blocksAround;
                    this.collisionBlocks = collidingBlocks;
                }
            }

            if (this.speed == null) speed = new Vector3(from.x - to.x, from.y - to.y, from.z - to.z);
            else this.speed.setComponents(from.x - to.x, from.y - to.y, from.z - to.z);
        } else {
            if (this.speed == null) speed = new Vector3(0, 0, 0);
            else this.speed.setComponents(0, 0, 0);
        }

        if (!revert && (this.isFoodEnabled() || this.getServer().getDifficulty() == 0)) {
            if ((this.isSurvival() || this.isAdventure())/* && !this.getRiddingOn() instanceof Entity*/) {
                //UpdateFoodExpLevel
                if (distance >= 0.05) {
                    double jump = 0;
                    double swimming = this.isInsideOfWater() ? 0.015 * distance : 0;
                    if (swimming != 0) distance = 0;
                    if (this.isSprinting()) {  //Running
                        if (this.inAirTicks == 3 && swimming == 0) {
                            jump = 0.7;
                        }
                        this.getFoodData().updateFoodExpLevel(0.06 * distance + jump + swimming);
                    } else {
                        if (this.inAirTicks == 3 && swimming == 0) {
                            jump = 0.2;
                        }
                        this.getFoodData().updateFoodExpLevel(0.01 * distance + jump + swimming);
                    }
                }
            }
        }

        if (revert) {
            this.lastX = from.x;
            this.lastY = from.y;
            this.lastZ = from.z;

            this.lastYaw = from.yaw;
            this.lastPitch = from.pitch;

            // We have to send slightly above otherwise the player will fall into the ground.
            this.sendPosition(from.add(0, 0.00001, 0), from.yaw, from.pitch, MovePlayerPacket.MODE_RESET);
            //this.sendSettings();
            this.forceMovement = new Vector3(from.x, from.y + 0.00001, from.z);
        } else {
            this.forceMovement = null;
            if (distanceSquared != 0 && this.nextChunkOrderRun > 20) {
                this.nextChunkOrderRun = 20;
            }
        }

        this.newPosition = null;
    }

    @Override
    public boolean setMotion(Vector3 motion) {
        if (super.setMotion(motion)) {
            if (this.chunk != null) {
                this.getLevel().addEntityMotion(this.chunk.getX(), this.chunk.getZ(), this.getId(), this.motionX, this.motionY, this.motionZ);  //Send to others
                SetEntityMotionPacket pk = new SetEntityMotionPacket();
                pk.eid = this.id;
                pk.motionX = (float) motion.x;
                pk.motionY = (float) motion.y;
                pk.motionZ = (float) motion.z;
                this.dataPacket(pk);  //Send to self
            }

            if (this.motionY > 0) {
                //todo: check this
                this.startAirTicks = (int) ((-(Math.log(this.getGravity() / (this.getGravity() + this.getDrag() * this.motionY))) / this.getDrag()) * 2 + 5);
            }

            return true;
        }

        return false;
    }

    public void sendAttributes() {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entityId = this.getId();
        pk.entries = new Attribute[]{
                Attribute.getAttribute(Attribute.HEALTH).setMaxValue(this.getMaxHealth()).setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0),
                Attribute.getAttribute(Attribute.PLAYER_HUNGER).setValue(this.getFoodData().getLevel()),
                Attribute.getAttribute(Attribute.MOVEMENT).setValue(this.getMovementSpeed()),
                Attribute.getAttribute(Attribute.PLAYER_LEVEL).setValue(this.getExperienceLevel()),
                Attribute.getAttribute(Attribute.PLAYER_EXPERIENCE).setValue(((float) this.getExperience()) / calculateRequireExperience(this.getExperienceLevel()))
        };
        this.dataPacket(pk);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (!this.loggedIn) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0) {
            return true;
        }

        this.messageCounter = 2;

        this.lastUpdate = currentTick;

        if (this.fishing != null && this.server.getTick() % 20 == 0) {
            if (this.distance(fishing) > 33) {
                this.stopFishing(false);
            }
        }

        if (!this.isAlive() && this.spawned) {
            ++this.deadTicks;
            if (this.deadTicks >= 10) {
                this.despawnFromAll();
            }
            return true;
        }

        if (this.spawned) {
            this.processMovement(tickDiff);

            if (!this.isSpectator()) {
                this.checkNearEntities();
            }

            this.entityBaseTick(tickDiff);

            if (this.getServer().getDifficulty() == 0 && this.level.getGameRules().getBoolean(GameRule.NATURAL_REGENERATION)) {
                if (this.getHealth() < this.getMaxHealth() && this.ticksLived % 20 == 0) {
                    this.heal(1);
                }

                PlayerFood foodData = this.getFoodData();

                if (foodData.getLevel() < 20 && this.ticksLived % 10 == 0) {
                    foodData.addFoodLevel(1, 0);
                }
            }

            if (this.isOnFire() && this.lastUpdate % 10 == 0) {
                if (this.isCreative() && !this.isInsideOfFire()) {
                    this.extinguish();
                } else if (this.getLevel().isRaining()) {
                    if (this.getLevel().canBlockSeeSky(this)) {
                        this.extinguish();
                    }
                }
            }

            if (!this.isSpectator() && this.speed != null) {
                if (this.onGround) {
                    if (this.inAirTicks != 0) {
                        this.startAirTicks = 5;
                    }
                    this.inAirTicks = 0;
                    this.highestPosition = this.y;
                } else {
                    if (this.checkMovement && !this.isGliding() && !server.getAllowFlight() && !this.getAdventureSettings().get(Type.ALLOW_FLIGHT) && this.inAirTicks > 20 && !this.isSleeping() && !this.isImmobile() && !this.isSwimming() && this.riding == null && !this.hasEffect(Effect.LEVITATION) && !this.hasEffect(Effect.SLOW_FALLING)) {
                        double expectedVelocity = (-this.getGravity()) / ((double) this.getDrag()) - ((-this.getGravity()) / ((double) this.getDrag())) * Math.exp(-((double) this.getDrag()) * ((double) (this.inAirTicks - this.startAirTicks)));
                        double diff = (this.speed.y - expectedVelocity) * (this.speed.y - expectedVelocity);

                        int block = level.getBlockIdAt(0, this.getFloorX(), this.getFloorY(), this.getFloorZ());
                        boolean ignore = block == Block.LADDER || block == Block.VINE || block == Block.WEB || block == BlockID.BUBBLE_COLUMN || block == BlockID.SCAFFOLDING || block == BlockID.SWEET_BERRY_BUSH || block == BlockID.WEEPING_VINES || block == BlockID.TWISTING_VINES || block == BlockID.CAVE_VINES || block == BlockID.CAVE_VINES_BODY_WITH_BERRIES || block == BlockID.CAVE_VINES_HEAD_WITH_BERRIES || block == BlockID.POWDER_SNOW || block == BlockID.BIG_DRIPLEAF
                                || block == BlockID.HONEY_BLOCK; //TODO: slide down

                        if (!this.hasEffect(Effect.JUMP_BOOST) && diff > 0.6 && expectedVelocity < this.speed.y && !ignore) {
                            if (this.inAirTicks < 150) {
                                this.setMotion(new Vector3(0, expectedVelocity, 0));
                            } else if (this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server")) {
                                return false;
                            }
                        }
                        if (ignore) {
                            this.resetFallDistance();
                        }
                    }

                    if (this.y > highestPosition) {
                        this.highestPosition = this.y;
                    }

                    if (this.isGliding()) this.resetFallDistance();

                    ++this.inAirTicks;

                }

                if (this.isSurvival() || this.isAdventure()) {
                    if (this.getFoodData() != null) this.getFoodData().update(tickDiff);
                }
            }

            if (this.isSleeping() && spawnBlockPosition != null) {
                Block block = this.spawnBlockPosition.level.getBlock(this.spawnBlockPosition);
                if (block.getId() != Block.BLOCK_BED || (block.getDamage() & 0x8) != 0x8) {
                    this.stopSleep();
                }
            }
        }

        this.checkTeleportPosition();
        /*if (currentTick % 10 == 0) {
            this.checkInteractNearby(); // 在客户端计算, 结果通过 action 为 mouseover 的 InteractPacket 发给服务端 (runtimeEntityId 为 0 表示移开)
        }*/

        if (this.spawned && this.dummyBossBars.size() > 0 && currentTick % 100 == 0) {
            this.dummyBossBars.values().forEach(DummyBossBar::updateBossEntityPosition);
        }

        return true;
    }

    public boolean checkInteractNearby = true;

    public void checkInteractNearby() {
        if (!checkInteractNearby) return;
        int interactDistance = isCreative() ? 5 : 3;
        if (canInteract(this, interactDistance)) {
            if (getEntityPlayerLookingAt(interactDistance) != null) {
                EntityInteractable onInteract = getEntityPlayerLookingAt(interactDistance);
                setButtonText(onInteract.getInteractButtonText());
            } else {
                setButtonText("");
            }
        } else {
            setButtonText("");
        }
    }

    /**
     * Returns the Entity the player is looking at currently
     *
     * @param maxDistance the maximum distance to check for entities
     * @return Entity|null    either NULL if no entity is found or an instance of the entity
     */
    public EntityInteractable getEntityPlayerLookingAt(int maxDistance) {
        timing.startTiming();

        EntityInteractable entity = null;

        // just a fix because player MAY not be fully initialized
        if (temporalVector != null) {
            Entity[] nearbyEntities = level.getNearbyEntities(boundingBox.grow(maxDistance, maxDistance, maxDistance), this);

            // get all blocks in looking direction until the max interact distance is reached (it's possible that startblock isn't found!)
            try {
                BlockIterator itr = new BlockIterator(level, getPosition(), getDirectionVector(), getEyeHeight(), maxDistance);
                if (itr.hasNext()) {
                    Block block;
                    while (itr.hasNext()) {
                        block = itr.next();
                        entity = getEntityAtPosition(nearbyEntities, block.getFloorX(), block.getFloorY(), block.getFloorZ());
                        if (entity != null) {
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                // nothing to log here!
            }
        }

        timing.stopTiming();

        return entity;
    }

    private EntityInteractable getEntityAtPosition(Entity[] nearbyEntities, int x, int y, int z) {
        for (Entity nearestEntity : nearbyEntities) {
            if (nearestEntity.getFloorX() == x && nearestEntity.getFloorY() == y && nearestEntity.getFloorZ() == z
                    && nearestEntity instanceof EntityInteractable
                    && ((EntityInteractable) nearestEntity).canDoInteraction()) {
                return (EntityInteractable) nearestEntity;
            }
        }
        return null;
    }

    public void checkNetwork() {
        if (!this.isOnline()) {
            return;
        }

        if (this.nextChunkOrderRun-- <= 0 || this.chunk == null) {
            this.orderChunks();
        }

        if (!this.loadQueue.isEmpty() || !this.spawned) {
            this.sendNextChunk();
        }

        if (this.hasSubChunkRequest()) {
            this.processSubChunkRequest();
        }
    }

    public boolean canInteract(Vector3 pos, double maxDistance) {
        return this.canInteract(pos, maxDistance, 6.0);
    }

    public boolean canInteract(Vector3 pos, double maxDistance, double maxDiff) {
        if (this.distanceSquared(pos) > maxDistance * maxDistance) {
            return false;
        }

        Vector2 dV = this.getDirectionPlane();
        double dot = dV.dot(new Vector2(this.x, this.z));
        double dot1 = dV.dot(new Vector2(pos.x, pos.z));
        return (dot1 - dot) >= -maxDiff;
    }

    protected void processLogin() {
        if (!this.server.isWhitelisted((this.getName()).toLowerCase())) {
            this.kick(PlayerKickEvent.Reason.NOT_WHITELISTED, "Server is white-listed");

            return;
        } else if (this.isBanned()) {
            this.kick(PlayerKickEvent.Reason.NAME_BANNED, "You are banned");
            return;
        } else if (this.server.getIPBans().isBanned(this.getAddress())) {
            this.kick(PlayerKickEvent.Reason.IP_BANNED, "You are banned");
            return;
        }

        if (this.hasPermission(Server.BROADCAST_CHANNEL_USERS)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this);
        }
        if (this.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);
        }

        for (Player p : new ObjectArrayList<>(this.server.getOnlinePlayers().values())) {
            if (p != this && p.getName() != null && p.getName().equalsIgnoreCase(this.getName())) {
                if (!p.kick(PlayerKickEvent.Reason.NEW_CONNECTION, "logged in from another location")) {
                    this.close(this.getLeaveMessage(), "Already connected");
                    return;
                }
            } else if (p.loggedIn && this.getUniqueId().equals(p.getUniqueId())) {
                if (!p.kick(PlayerKickEvent.Reason.NEW_CONNECTION, "logged in from another location")) {
                    this.close(this.getLeaveMessage(), "Already connected");
                    return;
                }
            }
        }

        CompoundTag nbt = this.server.getOfflinePlayerData(this.username);
        if (nbt == null) {
            this.close(this.getLeaveMessage(), "Invalid data");

            return;
        }

        this.playedBefore = (nbt.getLong("lastPlayed") - nbt.getLong("firstPlayed")) > 1;

        boolean alive = true;

        nbt.putString("NameTag", this.username);

        if (0 >= nbt.getShort("Health")) {
            alive = false;
        }

        int exp = nbt.getInt("EXP");
        int expLevel = nbt.getInt("expLevel");
        this.setExperience(exp, expLevel);

        this.gamemode = nbt.getInt("playerGameType") & 0x03;
        if (this.server.getForceGamemode()) {
            this.gamemode = this.server.getGamemode();
            nbt.putInt("playerGameType", this.gamemode);
        }

        this.adventureSettings = new AdventureSettings(this)
                .set(Type.WORLD_IMMUTABLE, isAdventure() || isSpectator())
                .set(Type.MINE, !isAdventure() && !isSpectator())
                .set(Type.BUILD, !isAdventure() && !isSpectator())
                .set(Type.AUTO_JUMP, true)
                .set(Type.ALLOW_FLIGHT, isCreative() || isSpectator())
                .set(Type.NO_CLIP, isSpectator())
                .set(Type.FLYING, isSpectator())
                .set(Type.NO_PVM, isSpectator())
                .set(Type.NO_MVP, isSpectator())
                .set(Type.DOORS_AND_SWITCHED, !isSpectator())
                .set(Type.OPEN_CONTAINERS, !isSpectator())
                .set(Type.ATTACK_PLAYERS, !isSpectator())
                .set(Type.ATTACK_MOBS, !isSpectator())
                .set(Type.INSTABUILD, isCreative())
                .set(Type.INVULNERABLE, isCreative() || isSpectator())
                .set(Type.OPERATOR, isOp())
                .set(Type.TELEPORT, hasPermission("nukkit.command.teleport"));

        Level level;
        if ((level = this.server.getLevelByName(nbt.getString("Level"))) == null || !alive) {
            this.setLevel(this.server.getDefaultLevel());
            nbt.putString("Level", this.level.getName());
            nbt.getList("Pos", DoubleTag.class)
                    .add(new DoubleTag("", this.level.getSpawnLocation().x))
                    .add(new DoubleTag("", this.level.getSpawnLocation().y))
                    .add(new DoubleTag("", this.level.getSpawnLocation().z));
        } else {
            this.setLevel(level);
        }

        /*for (Tag achievement : nbt.getCompound("Achievements").getAllTags()) {
            if (!(achievement instanceof ByteTag)) {
                continue;
            }

            if (((ByteTag) achievement).getData() > 0) {
                this.achievements.add(achievement.getName());
            }
        }*/

        nbt.putLong("lastPlayed", System.currentTimeMillis() / 1000);

        UUID uuid = getUniqueId();
        nbt.putLong("UUIDLeast", uuid.getLeastSignificantBits());
        nbt.putLong("UUIDMost", uuid.getMostSignificantBits());

        if (this.server.getAutoSave()) {
            this.server.saveOfflinePlayerData(this.username, nbt, true);
        }

        this.sendPlayStatus(PlayStatusPacket.LOGIN_SUCCESS);
        this.server.onPlayerLogin(this);

        ListTag<DoubleTag> posList = nbt.getList("Pos", DoubleTag.class);

        super.init(this.level.getChunk((int) posList.get(0).data >> 4, (int) posList.get(2).data >> 4, true), nbt);

        if (!this.namedTag.contains("foodLevel")) {
            this.namedTag.putInt("foodLevel", 20);
        }
        int foodLevel = this.namedTag.getInt("foodLevel");
        if (!this.namedTag.contains("FoodSaturationLevel")) {
            this.namedTag.putFloat("FoodSaturationLevel", 20);
        }
        float foodSaturationLevel = this.namedTag.getFloat("foodSaturationLevel");
        this.foodData = new PlayerFood(this, foodLevel, foodSaturationLevel);

        if (this.isSpectator()) {
            this.keepMovement = true;
            this.onGround = false;
        }

        this.forceMovement = this.teleportPosition = this.getPosition();

        PlayerRequestResourcePackEvent resourcePackEvent = new PlayerRequestResourcePackEvent(this, this.server.getResourcePackManager().getResourcePacksMap(), this.server.getResourcePackManager().getBehaviorPacksMap(), this.server.getForceResources());
        this.server.getPluginManager().callEvent(resourcePackEvent);
        this.resourcePacks = resourcePackEvent.getResourcePacks();
        this.behaviourPacks = resourcePackEvent.getResourcePacks();
        this.forceResources = resourcePackEvent.isMustAccept();

        ResourcePacksInfoPacket infoPacket = new ResourcePacksInfoPacket();
        infoPacket.resourcePackEntries = this.resourcePacks.values().toArray(new ResourcePack[0]);
        infoPacket.behaviourPackEntries = this.behaviourPacks.values().toArray(new ResourcePack[0]);
        infoPacket.mustAccept = this.forceResources;
        this.dataPacket(infoPacket);
    }

    public void completePreLoginEventTask(PlayerAsyncPreLoginEvent e) {
        if (!this.closed) {
            if (e.getLoginResult() == PlayerAsyncPreLoginEvent.LoginResult.KICK) {
                this.close(e.getKickMessage(), e.getKickMessage());
            } else if (this.shouldLogin) {
                this.completeLoginSequence();
            }

            for (Consumer<Server> action : e.getScheduledActions()) {
                action.accept(server);
            }
        }
    }

    protected void completeLoginSequence() {
        PlayerLoginEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerLoginEvent(this, "Plugin reason"));
        if (ev.isCancelled()) {
            this.close(this.getLeaveMessage(), ev.getKickMessage());
            return;
        }

        Level level;
        if (this.spawnPosition == null && this.namedTag.contains("SpawnLevel") && (level = this.server.getLevelByName(this.namedTag.getString("SpawnLevel"))) != null) {
            this.spawnPosition = new Position(this.namedTag.getInt("SpawnX"), this.namedTag.getInt("SpawnY"), this.namedTag.getInt("SpawnZ"), level);
        }
        if (this.spawnBlockPosition == null && this.namedTag.contains("SpawnBlockPositionLevel")) {
            level = this.server.getLevelByName(this.namedTag.getString("SpawnBlockPositionLevel"));
            if (level != null) {
                this.spawnBlockPosition = new Position(this.namedTag.getInt("SpawnBlockPositionX"), this.namedTag.getInt("SpawnBlockPositionY"), this.namedTag.getInt("SpawnBlockPositionZ"), level);
            }
        }

        Position spawnPosition = this.getSpawn();

        StartGamePacket startGamePacket = new StartGamePacket();
        startGamePacket.entityUniqueId = this.id;
        startGamePacket.entityRuntimeId = this.id;
        startGamePacket.playerGamemode = getClientFriendlyGamemode(this.gamemode);
        startGamePacket.x = (float) this.x;
        startGamePacket.y = (float) this.y;
        startGamePacket.z = (float) this.z;
        startGamePacket.yaw = (float) this.yaw;
        startGamePacket.pitch = (float) this.pitch;
        startGamePacket.seed = -1;
        startGamePacket.dimension = (byte) (spawnPosition.level.getDimension().ordinal() & 0xff);
        startGamePacket.worldGamemode = getClientFriendlyGamemode(this.gamemode);
        startGamePacket.difficulty = this.server.getDifficulty();
        startGamePacket.spawnX = (int) spawnPosition.x;
        startGamePacket.spawnY = (int) spawnPosition.y;
        startGamePacket.spawnZ = (int) spawnPosition.z;
        startGamePacket.hasAchievementsDisabled = true;
        startGamePacket.dayCycleStopTime = -1;
        startGamePacket.eduMode = false;
        startGamePacket.rainLevel = 0;
        startGamePacket.lightningLevel = 0;
        startGamePacket.commandsEnabled = this.isEnableClientCommand();
        startGamePacket.levelId = "";
        startGamePacket.worldName = this.getServer().getNetwork().getName();
        startGamePacket.generator = 1; //0 old, 1 infinite, 2 flat
        startGamePacket.gameRules = this.level.gameRules;
        startGamePacket.enchantmentSeed = ThreadLocalRandom.current().nextInt();
        this.dataPacket(startGamePacket);

        this.loggedIn = true;

        spawnPosition.level.sendTime(this);

        this.sendAttributes();
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setCanClimb(true);
        if (this.isSpectator()) {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_SILENT, true);
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, false);
        }

        log.info(this.getServer().getLanguage().translateString("nukkit.player.logIn",
                TextFormat.AQUA + this.username + TextFormat.WHITE,
                this.getAddress(),
                String.valueOf(this.getPort()),
                String.valueOf(this.id),
                this.level.getName(),
                String.valueOf(NukkitMath.round(this.x, 4)),
                String.valueOf(NukkitMath.round(this.y, 4)),
                String.valueOf(NukkitMath.round(this.z, 4))));

        if (this.isOp()) {
            this.setRemoveFormat(false);
        }

        this.server.addOnlinePlayer(this);
        this.server.onPlayerCompleteLoginSequence(this);
    }

    public void handleDataPacket(DataPacket packet) {
        if (!connected) {
            return;
        }

        try (Timing timing = Timings.getReceiveDataPacketTiming(packet)) {
            DataPacketReceiveEvent ev = new DataPacketReceiveEvent(this, packet);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                timing.stopTiming();
                return;
            }

            if (packet.pid() == ProtocolInfo.BATCH_PACKET) {
                timing.stopTiming();
                this.server.getNetwork().processBatch((BatchPacket) packet, this);
                return;
            }

            packetswitch:
            switch (packet.pid()) {
                case ProtocolInfo.LOGIN_PACKET:
                    if (this.loggedIn) {
                        break;
                    }

                    LoginPacket loginPacket = (LoginPacket) packet;

                    String message;
                    if (loginPacket.getProtocol() < ProtocolInfo.CURRENT_PROTOCOL) {
                        if (loginPacket.getProtocol() < ProtocolInfo.CURRENT_PROTOCOL) {
                            message = "disconnectionScreen.outdatedClient";

                            this.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_CLIENT);
                        } else {
                            message = "disconnectionScreen.outdatedServer";

                            this.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_SERVER);
                        }
                        this.close("", message, false);
                        break;
                    }

                    this.protocol = loginPacket.protocol;
                    this.username = TextFormat.clean(loginPacket.username);
                    this.displayName = this.username;
                    this.iusername = this.username.toLowerCase();
                    this.setDataProperty(new StringEntityData(DATA_NAMETAG, this.username), false);

                    this.loginChainData = ClientChainData.read(loginPacket);

                    if (this.server.getOnlinePlayers().size() >= this.server.getMaxPlayers() && this.kick(PlayerKickEvent.Reason.SERVER_FULL, "disconnectionScreen.serverFull", false)) {
                        break;
                    }

                    this.randomClientId = loginPacket.clientId;

                    this.uuid = loginPacket.clientUUID;
                    this.rawUUID = Binary.writeUUID(this.uuid);

                    boolean valid = true;
                    int len = loginPacket.username.length();
                    if (len > 16 || len < 3) {
                        valid = false;
                    }

                    for (int i = 0; i < len && valid; i++) {
                        char c = loginPacket.username.charAt(i);
                        if ((c >= 'a' && c <= 'z') ||
                                (c >= 'A' && c <= 'Z') ||
                                (c >= '0' && c <= '9') ||
                                c == '_' || c == ' '
                                ) {
                            continue;
                        }

                        valid = false;
                        break;
                    }

                    if (!valid || Objects.equals(this.iusername, "rcon") || Objects.equals(this.iusername, "console")) {
                        this.close("", "disconnectionScreen.invalidName");

                        break;
                    }

                    if (!loginPacket.skin.isValid()) {
                        this.close("", "disconnectionScreen.invalidSkin");
                        break;
                    } else {
                        this.setSkin(loginPacket.getSkinLegacy());
                    }

                    PlayerPreLoginEvent playerPreLoginEvent;
                    this.server.getPluginManager().callEvent(playerPreLoginEvent = new PlayerPreLoginEvent(this, "Plugin reason"));
                    if (playerPreLoginEvent.isCancelled()) {
                        this.close("", playerPreLoginEvent.getKickMessage());

                        break;
                    }

                    Player playerInstance = this;
                    this.preLoginEventTask = new AsyncTask<PlayerAsyncPreLoginEvent>() {
                        @Override
                        public void onRun() {
                            PlayerAsyncPreLoginEvent e = new PlayerAsyncPreLoginEvent(playerInstance, username, uuid, getAddress(), getPort());
                            setResult(e);
                            server.getPluginManager().callEvent(e);
                        }

                        @Override
                        public void onCompletion(Server server) {
                            playerInstance.completePreLoginEventTask(getResult());
                        }
                    };

                    this.server.getScheduler().scheduleAsyncTask(this.preLoginEventTask);

                    this.processLogin();
                    break;
                case ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET:
                    ResourcePackClientResponsePacket responsePacket = (ResourcePackClientResponsePacket) packet;
                    switch (responsePacket.responseStatus) {
                        case ResourcePackClientResponsePacket.STATUS_REFUSED:
                            this.close("", "disconnectionScreen.noReason");
                            break;
                        case ResourcePackClientResponsePacket.STATUS_SEND_PACKS:
                            for (String id : responsePacket.packIds) {
                                ResourcePack resourcePack = this.resourcePacks.getOrDefault(id, this.behaviourPacks.get(id));
                                if (resourcePack == null) {
                                    this.close("", "disconnectionScreen.resourcePack");
                                    break;
                                }

                                ResourcePackDataInfoPacket dataInfoPacket = new ResourcePackDataInfoPacket();
                                dataInfoPacket.packId = resourcePack.getPackId();
                                dataInfoPacket.maxChunkSize = RESOURCE_PACK_CHUNK_SIZE;
                                dataInfoPacket.chunkCount = Mth.ceil(resourcePack.getPackSize() / (float) RESOURCE_PACK_CHUNK_SIZE);
                                dataInfoPacket.compressedPackSize = resourcePack.getPackSize();
                                dataInfoPacket.sha256 = resourcePack.getSha256();
                                if (resourcePack.getPackType().equals("resources")) {
                                    dataInfoPacket.type = ResourcePackDataInfoPacket.TYPE_RESOURCE;
                                }
                                else if (resourcePack.getPackType().equals("data")) {
                                    dataInfoPacket.type = ResourcePackDataInfoPacket.TYPE_BEHAVIOR;
                                }
                                this.dataPacket(dataInfoPacket);
                            }
                            break;
                        case ResourcePackClientResponsePacket.STATUS_HAVE_ALL_PACKS:
                            ResourcePackStackPacket stackPacket = new ResourcePackStackPacket();
                            stackPacket.mustAccept = this.forceResources;
                            stackPacket.resourcePackStack = this.resourcePacks.values().toArray(new ResourcePack[0]);
                            stackPacket.behaviourPackStack = this.behaviourPacks.values().toArray(new ResourcePack[0]);
                            this.dataPacket(stackPacket);
                            break;
                        case ResourcePackClientResponsePacket.STATUS_COMPLETED:
                            if (this.preLoginEventTask.isFinished()) {
                                this.completeLoginSequence();
                            } else {
                                this.shouldLogin = true;
                            }
                            break;
                    }
                    break;
                case ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET:
                    ResourcePackChunkRequestPacket requestPacket = (ResourcePackChunkRequestPacket) packet;
                    ResourcePack resourcePack = this.resourcePacks.getOrDefault(requestPacket.packId, this.behaviourPacks.get(requestPacket.packId));
                    if (resourcePack == null) {
                        this.close("", "disconnectionScreen.resourcePack");
                        break;
                    }

                    ResourcePackChunkDataPacket dataPacket = new ResourcePackChunkDataPacket();
                    dataPacket.packId = resourcePack.getPackId();
                    dataPacket.chunkIndex = requestPacket.chunkIndex;
                    dataPacket.data = resourcePack.getPackChunk(RESOURCE_PACK_CHUNK_SIZE * requestPacket.chunkIndex, RESOURCE_PACK_CHUNK_SIZE);
                    dataPacket.progress = (long) RESOURCE_PACK_CHUNK_SIZE * requestPacket.chunkIndex;
                    this.dataPacket(dataPacket);
                    break;
                case ProtocolInfo.PLAYER_INPUT_PACKET:
                    if (!this.isAlive() || !this.spawned) {
                        break;
                    }
                    PlayerInputPacket ipk = (PlayerInputPacket) packet;
                    if (riding instanceof EntityMinecartAbstract) {
                        ((EntityMinecartEmpty) riding).setCurrentSpeed(ipk.motionY);
                    } else if (riding instanceof EntityRideable) {
                        ((EntityRideable) riding).onPlayerInput(this, ipk.motionX, ipk.motionY);
                    }
                    break;
                case ProtocolInfo.MOVE_PLAYER_PACKET:
                    if (this.teleportPosition != null) {
                        break;
                    }

                    MovePlayerPacket movePlayerPacket = (MovePlayerPacket) packet;
                    Vector3 newPos = new Vector3(movePlayerPacket.x, movePlayerPacket.y - this.getEyeHeight(), movePlayerPacket.z);

                    if (newPos.distanceSquared(this) == 0 && movePlayerPacket.yaw % 360 == this.yaw && movePlayerPacket.pitch % 360 == this.pitch) {
                        break;
                    }

                    boolean revert = false;
                    if (!this.isAlive() || !this.spawned) {
                        revert = true;
                        this.forceMovement = new Vector3(this.x, this.y, this.z);
                    }

                    if (riding != null) forceMovement = null;

                    if (this.forceMovement != null && (newPos.distanceSquared(this.forceMovement) > 0.1 || revert)) {
                        this.sendPosition(this.forceMovement, this.yaw, this.pitch, MovePlayerPacket.MODE_TELEPORT);
                    } else {
                        movePlayerPacket.yaw %= 360;
                        movePlayerPacket.pitch %= 360;

                        if (movePlayerPacket.yaw < 0) {
                            movePlayerPacket.yaw += 360;
                        }

                        this.setRotation(movePlayerPacket.yaw, movePlayerPacket.pitch);
                        this.newPosition = newPos;
                        this.forceMovement = null;
                    }

                    if (riding != null) {
                        if (riding instanceof EntityRideable && !(riding instanceof EntityBoat)) {
                            Vector3f offset = riding.getMountedOffset(this);
                            ((EntityRideable) riding).onPlayerRiding(this.temporalVector.setComponents(movePlayerPacket.x - offset.x, movePlayerPacket.y - offset.y, movePlayerPacket.z - offset.z), (movePlayerPacket.headYaw + 90) % 360, 0);
                        }
                    }

                    break;
                case ProtocolInfo.MOVE_ENTITY_ABSOLUTE_PACKET:
                    MoveEntityPacket moveEntityPacket = (MoveEntityPacket) packet;
                    if (this.riding == null || this.riding.getId() != moveEntityPacket.eid || !this.riding.isControlling(this)) {
                        break;
                    }
                    if (this.riding instanceof EntityBoat) {
                        if (this.temporalVector.setComponents(moveEntityPacket.x, moveEntityPacket.y, moveEntityPacket.z).distanceSquared(this.riding) < 1000) {
                            ((EntityBoat) this.riding).onInput(moveEntityPacket.x, moveEntityPacket.y, moveEntityPacket.z, moveEntityPacket.yaw);
                        }
                    }
                    break;
                case ProtocolInfo.ADVENTURE_SETTINGS_PACKET:
                    //TODO: player abilities, check for other changes
                    AdventureSettingsPacket adventureSettingsPacket = (AdventureSettingsPacket) packet;
                    if (adventureSettingsPacket.entityUniqueId != this.getLocalEntityId()) {
                        break;
                    }
                    if (adventureSettingsPacket.getFlag(AdventureSettingsPacket.FLYING) && !server.getAllowFlight() && !this.getAdventureSettings().get(Type.ALLOW_FLIGHT)) {
                        this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server");
                        break;
                    }
                    PlayerToggleFlightEvent playerToggleFlightEvent = new PlayerToggleFlightEvent(this, adventureSettingsPacket.getFlag(AdventureSettingsPacket.FLYING));
                    if (this.isSpectator()) {
                        playerToggleFlightEvent.setCancelled();
                    }
                    this.server.getPluginManager().callEvent(playerToggleFlightEvent);
                    if (playerToggleFlightEvent.isCancelled()) {
                        this.sendAbilities(this, this.getAdventureSettings());
                    } else {
                        this.getAdventureSettings().set(Type.FLYING, playerToggleFlightEvent.isFlying());
                    }
                    break;
                case ProtocolInfo.MOB_EQUIPMENT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    MobEquipmentPacket mobEquipmentPacket = (MobEquipmentPacket) packet;

                    Inventory inv = this.getWindowById(mobEquipmentPacket.windowId);

                    if (inv == null) {
                        this.server.getLogger().debug(this.getName() + " has no open container with window ID " + mobEquipmentPacket.windowId);
                        return;
                    }

                    Item item = inv.getItem(mobEquipmentPacket.hotbarSlot);

                    if (!item.equals(mobEquipmentPacket.item, !(item instanceof ItemDurable))) {
                        log.debug(this.getName() + " tried to equip " + mobEquipmentPacket.item + " but have " + item + " in target slot");
                        inv.sendContents(this);
                        return;
                    }

                    if (inv instanceof PlayerInventory) {
                        ((PlayerInventory) inv).equipItem(mobEquipmentPacket.hotbarSlot);
                    }

                    this.setDataFlag(Player.DATA_FLAGS, Player.DATA_FLAG_ACTION, false);

                    break;
                case ProtocolInfo.PLAYER_ACTION_PACKET:
                    PlayerActionPacket playerActionPacket = (PlayerActionPacket) packet;
                    if (!this.spawned || (!this.isAlive() && playerActionPacket.action != PlayerActionPacket.ACTION_RESPAWN /*&& playerActionPacket.action != PlayerActionPacket.ACTION_DIMENSION_CHANGE_REQUEST*/)) {
                        break;
                    }

                    playerActionPacket.entityId = this.id;
                    Vector3 pos = new Vector3(playerActionPacket.x, playerActionPacket.y, playerActionPacket.z);

                    switch (playerActionPacket.action) {
                        case PlayerActionPacket.ACTION_START_BREAK:
                            if (!this.spawned || !this.isAlive() || this.isSpectator() || this.lastBreak != Long.MAX_VALUE || pos.distanceSquared(this) > 100) {
                                break;
                            }
                            Block target = this.level.getBlock(pos, false);
                            BlockFace face = BlockFace.fromIndex(playerActionPacket.data);
                            PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(this, this.inventory.getItemInHand(), target, face, target.getId() == BlockID.AIR ? Action.LEFT_CLICK_AIR : Action.LEFT_CLICK_BLOCK);
                            this.getServer().getPluginManager().callEvent(playerInteractEvent);
                            if (playerInteractEvent.isCancelled()) {
                                this.inventory.sendHeldItem(this);
                                break;
                            }
                            if (target.getId() == Block.NOTEBLOCK) {
                                ((BlockNoteblock) target).emitSound();
                                break;
                            }
                            Block block = target.getSide(face);
                            if (block.isFire()) {
                                this.level.setBlock(block, Block.get(BlockID.AIR), true);
                                this.level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_EXTINGUISH_FIRE);
                                break;
                            }
                            if (!this.isCreative()) {
                                //improved this to take stuff like swimming, ladders, enchanted tools into account, fix wrong tool break time calculations for bad tools (pmmp/PocketMine-MP#211)
                                //Done by lmlstarqaq
                                int breakTime = Mth.ceil(target.getBreakTime(this.inventory.getItemInHand(), this) * 20);
                                if (breakTime > 0) {
                                    LevelEventPacket pk = new LevelEventPacket();
                                    pk.evid = LevelEventPacket.EVENT_BLOCK_START_BREAK;
                                    pk.x = (float) pos.x;
                                    pk.y = (float) pos.y;
                                    pk.z = (float) pos.z;
                                    pk.data = 65535 / breakTime;
                                    this.getLevel().addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
                                }
                            }

                            this.breakingBlock = target;
                            this.lastBreak = System.currentTimeMillis();
                            break;
                        case PlayerActionPacket.ACTION_ABORT_BREAK:
                            this.lastBreak = Long.MAX_VALUE;
                            this.breakingBlock = null;
                        case PlayerActionPacket.ACTION_STOP_BREAK:
                            if (pos.distanceSquared(this) < 100) { // same as with ACTION_START_BREAK
                                LevelEventPacket pk = new LevelEventPacket();
                                pk.evid = LevelEventPacket.EVENT_BLOCK_STOP_BREAK;
                                pk.x = (float) pos.x;
                                pk.y = (float) pos.y;
                                pk.z = (float) pos.z;
                                pk.data = 0;
                                this.getLevel().addChunkPacket(pos.getChunkX(), pos.getChunkZ(), pk);
                            }
                            this.breakingBlock = null;
                            break;
                        case PlayerActionPacket.ACTION_GET_UPDATED_BLOCK:
                            break;
                        case PlayerActionPacket.ACTION_DROP_ITEM:
                            break;
                        case PlayerActionPacket.ACTION_STOP_SLEEPING:
                            this.stopSleep();
                            break;
                        case PlayerActionPacket.ACTION_RESPAWN:
                            if (!this.spawned || this.isAlive() || !this.isOnline()) {
                                break;
                            }

                            if (this.server.isHardcore()) {
                                this.setBanned(true);
                                break;
                            }

                            this.craftingType = CRAFTING_SMALL;
                            this.resetCraftingGridType();

                            PlayerRespawnEvent playerRespawnEvent = new PlayerRespawnEvent(this, this.getSpawn());
                            this.server.getPluginManager().callEvent(playerRespawnEvent);

                            Position respawnPos = playerRespawnEvent.getRespawnPosition();

                            this.teleport(respawnPos, null);

                            RespawnPacket respawnPacket = new RespawnPacket();
                            respawnPacket.x = (float) respawnPos.x;
                            respawnPacket.y = (float) respawnPos.y;
                            respawnPacket.z = (float) respawnPos.z;
                            this.dataPacket(respawnPacket);

                            this.setSprinting(false);
                            this.setSneaking(false);

                            this.extinguish();
                            this.setDataProperty(new ShortEntityData(Player.DATA_AIR, 400), false);
                            this.deadTicks = 0;
                            this.noDamageTicks = 60;

                            this.removeAllEffects();
                            this.setHealth(this.getMaxHealth());
                            this.getFoodData().setLevel(20, 20);

                            this.sendData(this);

                            this.setMovementSpeed(DEFAULT_SPEED);

                            this.getAdventureSettings().update();
                            this.inventory.sendContents(this);
                            this.inventory.sendArmorContents(this);

                            this.spawnToAll();
                            this.scheduleUpdate();
                            break;
                        case PlayerActionPacket.ACTION_JUMP:
                            break packetswitch;
                        case PlayerActionPacket.ACTION_START_SPRINT:
                            PlayerToggleSprintEvent playerToggleSprintEvent = new PlayerToggleSprintEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleSprintEvent);
                            if (playerToggleSprintEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSprinting(true);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_STOP_SPRINT:
                            playerToggleSprintEvent = new PlayerToggleSprintEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleSprintEvent);
                            if (playerToggleSprintEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSprinting(false);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_START_SNEAK:
                            PlayerToggleSneakEvent playerToggleSneakEvent = new PlayerToggleSneakEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleSneakEvent);
                            if (playerToggleSneakEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSneaking(true);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_STOP_SNEAK:
                            playerToggleSneakEvent = new PlayerToggleSneakEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleSneakEvent);
                            if (playerToggleSneakEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSneaking(false);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_DIMENSION_CHANGE_ACK:
                            this.onDimensionChangeSuccess();
                            break;
                        case PlayerActionPacket.ACTION_START_GLIDE:
                            PlayerToggleGlideEvent playerToggleGlideEvent = new PlayerToggleGlideEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleGlideEvent);
                            if (playerToggleGlideEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setGliding(true);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_STOP_GLIDE:
                            playerToggleGlideEvent = new PlayerToggleGlideEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleGlideEvent);
                            if (playerToggleGlideEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setGliding(false);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_CONTINUE_BREAK:
                            if (this.isBreakingBlock()) {
                                block = this.level.getBlock(pos, false);
                                face = BlockFace.fromIndex(playerActionPacket.data);
                                this.level.addParticle(new PunchBlockParticle(pos, block, face));
                            }
                            break;
                    }

                    this.startAction = -1;
                    this.startActionTimestamp = -1;
                    this.setDataFlag(Player.DATA_FLAGS, Player.DATA_FLAG_ACTION, false);
                    break;
                case ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET:
                    break;

                case ProtocolInfo.MODAL_FORM_RESPONSE_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    ModalFormResponsePacket modalFormPacket = (ModalFormResponsePacket) packet;

                    if (formWindows.containsKey(modalFormPacket.formId)) {
                        FormWindow window = formWindows.get(modalFormPacket.formId);
                        window.setResponse(modalFormPacket.data.trim());

                        PlayerFormRespondedEvent event = new PlayerFormRespondedEvent(this, modalFormPacket.formId, window);
                        getServer().getPluginManager().callEvent(event);

                        formWindows.remove(modalFormPacket.formId);
                    } else if (serverSettings.containsKey(modalFormPacket.formId)) {
                        FormWindow window = serverSettings.get(modalFormPacket.formId);
                        window.setResponse(modalFormPacket.data.trim());

                        PlayerSettingsRespondedEvent event = new PlayerSettingsRespondedEvent(this, modalFormPacket.formId, window);
                        getServer().getPluginManager().callEvent(event);

                        //Set back new settings if not been cancelled
                        if (!event.isCancelled() && window instanceof FormWindowCustom)
                            ((FormWindowCustom) window).setElementsFromResponse();
                    }

                    break;

                case ProtocolInfo.INTERACT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

//                    this.craftingType = CRAFTING_SMALL;
                    //this.resetCraftingGridType();

                    InteractPacket interactPacket = (InteractPacket) packet;

                    if (interactPacket.target == 0 && interactPacket.action == InteractPacket.ACTION_MOUSEOVER) {
                        this.setButtonText("");
                        break;
                    }

                    Entity targetEntity = interactPacket.target == this.getId() ? this : this.level.getEntity(interactPacket.target);

                    if (targetEntity == null || !this.isAlive() || !targetEntity.isAlive()) {
                        break;
                    }

                    if (targetEntity instanceof EntityItem || targetEntity instanceof EntityArrow || targetEntity instanceof EntityXPOrb) {
                        this.kick(PlayerKickEvent.Reason.INVALID_PVE, "Attempting to interact with an invalid entity");
                        log.warn(this.getServer().getLanguage().translateString("nukkit.player.invalidEntity", this.getName()));
                        break;
                    }

                    //item = this.inventory.getItemInHand();

                    switch (interactPacket.action) {
                        case InteractPacket.ACTION_MOUSEOVER:
                            if (targetEntity instanceof EntityInteractable) {
                                this.setButtonText(((EntityInteractable) targetEntity).getInteractButtonText());
                            }

                            this.getServer().getPluginManager().callEvent(new PlayerMouseOverEntityEvent(this, targetEntity));
                            break;
                        case InteractPacket.ACTION_VEHICLE_EXIT:
                            if (!(targetEntity instanceof EntityRideable) || this.riding != targetEntity) {
                                break;
                            }

                            ((EntityRideable) riding).dismountEntity(this);
                            break;
                        case InteractPacket.ACTION_OPEN_INVENTORY:
                            if (!(targetEntity instanceof InventoryHolder)) {
                                break;
                            }

                            ((InventoryHolder) targetEntity).openInventory(this);
                            break;
                    }
                    break;
                case ProtocolInfo.BLOCK_PICK_REQUEST_PACKET:
                    BlockPickRequestPacket pickRequestPacket = (BlockPickRequestPacket) packet;
                    Block block = this.level.getBlock(pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z, false);
                    if (block.distanceSquared(this) > 1000) {
                        this.getServer().getLogger().debug(this.getName() + ": Block pick request for a block too far away");
                        return;
                    }
                    item = block.toItem(true);

                    if (pickRequestPacket.addUserData) {
                        BlockEntity blockEntity = this.getLevel().getBlockEntity(new Vector3(pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z));
                        if (blockEntity != null) {
                            CompoundTag nbt = blockEntity.getCleanedNBT();
                            if (nbt != null) {
                                Item item1 = this.getInventory().getItemInHand();
                                item1.setCustomBlockData(nbt);
                                item1.setLore("+(DATA)");
                                this.getInventory().setItemInHand(item1);
                            }
                        }
                    }

                    PlayerBlockPickEvent pickEvent = new PlayerBlockPickEvent(this, block, item);
                    if (!this.isCreative()) {
                        log.debug("Got block-pick request from " + this.getName() + " when not in creative mode (gamemode " + this.getGamemode() + ")");
                        pickEvent.setCancelled();
                    }

                    this.server.getPluginManager().callEvent(pickEvent);

                    if (!pickEvent.isCancelled()) {
                        boolean itemExists = false;
                        for (int slot = 0; slot != this.inventory.getHotbarSize(); slot++) {
                            if (this.inventory.getItem(slot).equals(pickEvent.getItem())) {
                                this.inventory.setHeldItemSlot(slot);
                                itemExists = true;
                                break;
                            }
                        }
                        if (!itemExists) {
                            this.inventory.setItemInHand(pickEvent.getItem());
                        }
                    }
                    break;
                case ProtocolInfo.ANIMATE_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    PlayerAnimationEvent animationEvent = new PlayerAnimationEvent(this, ((AnimatePacket) packet).action);
                    this.server.getPluginManager().callEvent(animationEvent);
                    if (animationEvent.isCancelled()) {
                        break;
                    }

                    AnimatePacket.Action animation = animationEvent.getAnimationType();

                    switch (animation) {
                        case ROW_RIGHT:
                        case ROW_LEFT:
                            if (this.riding instanceof EntityBoat) {
                                ((EntityBoat) this.riding).onPaddle(animation, ((AnimatePacket) packet).rowingTime);
                            }
                            break;
                    }

                    AnimatePacket animatePacket = new AnimatePacket();
                    animatePacket.eid = this.getId();
                    animatePacket.action = animationEvent.getAnimationType();
                    Server.broadcastPacket(this.getViewers().values(), animatePacket);
                    break;
                case ProtocolInfo.SET_HEALTH_PACKET:
                    //use UpdateAttributePacket instead
                    break;

                case ProtocolInfo.ENTITY_EVENT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    EntityEventPacket entityEventPacket = (EntityEventPacket) packet;
                    if (entityEventPacket.event != EntityEventPacket.ENCHANT)
                        this.craftingType = CRAFTING_SMALL;
                        //this.resetCraftingGridType();

                    switch (entityEventPacket.event) {
                        case EntityEventPacket.EATING_ITEM:
                            if (entityEventPacket.data == 0 || entityEventPacket.eid != this.id) {
                                break;
                            }
                            entityEventPacket.isEncoded = false;

                            this.dataPacket(entityEventPacket);
                            Server.broadcastPacket(this.getViewers().values(), entityEventPacket);

                            break;
                        case EntityEventPacket.ENCHANT:
                            if (entityEventPacket.eid != this.id) {
                                break;
                            }

                            Inventory inventory = this.getWindowById(ANVIL_WINDOW_ID);
                            if (inventory instanceof AnvilInventory) {
                                ((AnvilInventory) inventory).setCost(-entityEventPacket.data);
                            } else if (this.getWindowById(ENCHANT_WINDOW_ID) != null) {
                                int levels = entityEventPacket.data; // Sent as negative number of levels lost
                                if (levels < 0) this.setExperience(this.exp, this.expLevel + levels);
                                break;
                            }
                            break;
                    }
                    break;
                case ProtocolInfo.COMMAND_REQUEST_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    this.craftingType = CRAFTING_SMALL;
                    CommandRequestPacket commandRequestPacket = (CommandRequestPacket) packet;
                    PlayerCommandPreprocessEvent playerCommandPreprocessEvent = new PlayerCommandPreprocessEvent(this, commandRequestPacket.command);
                    this.server.getPluginManager().callEvent(playerCommandPreprocessEvent);
                    if (playerCommandPreprocessEvent.isCancelled()) {
                        break;
                    }

                    Timings.playerCommandTimer.startTiming();
                    this.server.dispatchCommand(playerCommandPreprocessEvent.getPlayer(), playerCommandPreprocessEvent.getMessage().substring(1));
                    Timings.playerCommandTimer.stopTiming();
                    break;
                case ProtocolInfo.TEXT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    TextPacket textPacket = (TextPacket) packet;

                    if (textPacket.type == TextPacket.TYPE_CHAT) {
                        this.chat(textPacket.message);
                    }
                    break;
                case ProtocolInfo.CONTAINER_CLOSE_PACKET:
                    ContainerClosePacket containerClosePacket = (ContainerClosePacket) packet;
                    if (!this.spawned || containerClosePacket.windowId == 0) {
                        break;
                    }
                    this.craftingType = CRAFTING_SMALL;
                    this.resetCraftingGridType();

                    if (this.windowIndex.containsKey(containerClosePacket.windowId)) {
                        this.server.getPluginManager().callEvent(new InventoryCloseEvent(this.windowIndex.get(containerClosePacket.windowId), this));
                        this.removeWindow(this.windowIndex.get(containerClosePacket.windowId));
                    } else {
                        this.windowIndex.remove(containerClosePacket.windowId);
                    }
                    break;
                case ProtocolInfo.CRAFTING_EVENT_PACKET:
                    break;
                case ProtocolInfo.BLOCK_ENTITY_DATA_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    BlockEntityDataPacket blockEntityDataPacket = (BlockEntityDataPacket) packet;
                    this.craftingType = CRAFTING_SMALL;
                    this.resetCraftingGridType();

                    pos = new Vector3(blockEntityDataPacket.x, blockEntityDataPacket.y, blockEntityDataPacket.z);
                    if (pos.distanceSquared(this) > 10000) {
                        break;
                    }

                    BlockEntity t = this.level.getBlockEntity(pos);
                    if (t instanceof BlockEntitySpawnable) {
                        CompoundTag nbt;
                        try {
                            nbt = NBTIO.read(blockEntityDataPacket.namedTag, ByteOrder.LITTLE_ENDIAN, true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        if (!((BlockEntitySpawnable) t).updateCompoundTag(nbt, this)) {
                            ((BlockEntitySpawnable) t).spawnTo(this);
                        }
                    }
                    break;
                case ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET:
                    RequestChunkRadiusPacket requestChunkRadiusPacket = (RequestChunkRadiusPacket) packet;
                    ChunkRadiusUpdatedPacket chunkRadiusUpdatePacket = new ChunkRadiusUpdatedPacket();
                    this.chunkRadius = Math.max(3, Math.min(requestChunkRadiusPacket.radius, this.viewDistance));
                    chunkRadiusUpdatePacket.radius = this.chunkRadius;
                    this.dataPacket(chunkRadiusUpdatePacket);
                    break;
                case ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET:
                    SetPlayerGameTypePacket setPlayerGameTypePacket = (SetPlayerGameTypePacket) packet;
                    int gamemode = vanillaGamemodeToNukkitGamemode(setPlayerGameTypePacket.gamemode);
                    if (gamemode == this.gamemode) {
                        break;
                    }

                    if (!this.hasPermission("nukkit.command.gamemode") || gamemode == -1) {
                        SetPlayerGameTypePacket setPlayerGameTypePacket1 = new SetPlayerGameTypePacket();
                        setPlayerGameTypePacket1.gamemode = getClientFriendlyGamemode(this.gamemode);
                        this.dataPacket(setPlayerGameTypePacket1);

                        this.sendAbilities(this, this.getAdventureSettings());
                        break;
                    }

                    this.setGamemode(gamemode, true);
                    Command.broadcastCommandMessage(this, new TranslationContainer("commands.gamemode.success.self", Server.getGamemodeString(this.gamemode)));
                    break;
                case ProtocolInfo.SET_DEFAULT_GAME_TYPE_PACKET:
                    SetDefaultGameTypePacket setDefaultGameTypePacket = (SetDefaultGameTypePacket) packet;
                    gamemode = vanillaGamemodeToNukkitGamemode(setDefaultGameTypePacket.gamemode);

                    int defaultGamemode = this.server.getDefaultGamemode();
                    if (gamemode == defaultGamemode) {
                        break;
                    }

                    if (!this.hasPermission("nukkit.command.defaultgamemode") || gamemode == -1) {
                        SetDefaultGameTypePacket setDefaultGameTypePacket1 = new SetDefaultGameTypePacket();
                        setDefaultGameTypePacket1.gamemode = getClientFriendlyGamemode(defaultGamemode);
                        this.dataPacket(setDefaultGameTypePacket1);
                        break;
                    }

                    this.server.setPropertyInt("gamemode", gamemode);
                    Command.broadcastCommandMessage(this, new TranslationContainer("commands.defaultgamemode.success", Server.getGamemodeString(gamemode)));
                    break;
                case ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET:
                    ItemFrameDropItemPacket itemFrameDropItemPacket = (ItemFrameDropItemPacket) packet;
                    Vector3 vector3 = this.temporalVector.setComponents(itemFrameDropItemPacket.x, itemFrameDropItemPacket.y, itemFrameDropItemPacket.z);
                    if (vector3.distanceSquared(this) < 1000) {
                        BlockEntity itemFrame = this.level.getBlockEntity(vector3);
                        if (itemFrame instanceof BlockEntityItemFrame) {
                            ((BlockEntityItemFrame) itemFrame).dropItem(this);
                        }
                    }
                    break;
                case ProtocolInfo.MAP_INFO_REQUEST_PACKET:
                    MapInfoRequestPacket pk = (MapInfoRequestPacket) packet;
                    Item mapItem = null;

                    for (Item item1 : this.offhandInventory.getContents().values()) {
                        if (item1 instanceof ItemMap && ((ItemMap) item1).getMapId() == pk.mapId) {
                            mapItem = item1;
                        }
                    }

                    if (mapItem == null) {
                        for (Item item1 : this.inventory.getContents().values()) {
                            if (item1 instanceof ItemMap && ((ItemMap) item1).getMapId() == pk.mapId) {
                                mapItem = item1;
                            }
                        }
                    }

                    if (mapItem == null) {
                        for (BlockEntity be : this.level.getBlockEntities().values()) {
                            if (be instanceof BlockEntityItemFrame) {
                                BlockEntityItemFrame itemFrame1 = (BlockEntityItemFrame) be;

                                if (itemFrame1.getItem() instanceof ItemMap && ((ItemMap) itemFrame1.getItem()).getMapId() == pk.mapId) {
                                    ((ItemMap) itemFrame1.getItem()).sendImage(this);
                                    break;
                                }
                            }
                        }
                    }

                    if (mapItem != null) {
                        PlayerMapInfoRequestEvent event;
                        getServer().getPluginManager().callEvent(event = new PlayerMapInfoRequestEvent(this, mapItem));

                        if (!event.isCancelled()) {
                            ((ItemMap) mapItem).sendImage(this);
                        }
                    }

                    break;
                case ProtocolInfo.LEVEL_SOUND_EVENT_PACKET:
                    if (this.isSpectator()) {
                        break;
                    }
                    //LevelSoundEventPacket levelSoundEventPacket = (LevelSoundEventPacket) packet;
                    //We just need to broadcast this packet to all viewers.
                    Server.broadcastPacket(this.getViewers().values(), packet);
                    this.dataPacket(packet);
                    break;
                case ProtocolInfo.INVENTORY_TRANSACTION_PACKET:
                    InventoryTransactionPacket transactionPacket = (InventoryTransactionPacket) packet;

                    List<InventoryAction> actions = new ObjectArrayList<>();
                    for (NetworkInventoryAction networkInventoryAction : transactionPacket.actions) {
                        InventoryAction a = networkInventoryAction.createInventoryActionLegacy(this);

                        if (a == null) {
                            log.debug("Unmatched inventory action from " + this.getName() + ": " + networkInventoryAction);
                            this.sendAllInventories();
                            break packetswitch;
                        }

                        actions.add(a);
                    }

                    if (!this.isSpectator() && transactionPacket.isCraftingPart) {
                        if (this.craftingTransaction == null) {
                            this.craftingTransaction = new CraftingTransaction(this, actions);
                        } else {
                            for (InventoryAction action : actions) {
                                this.craftingTransaction.addAction(action);
                            }
                        }

                        if (this.craftingTransaction.getPrimaryOutput() != null) {
                            //we get the actions for this in several packets, so we can't execute it until we get the result

                            this.craftingTransaction.execute();
                            this.craftingTransaction = null;
                        }

                        return;
                    } else if (this.craftingTransaction != null) {
                        log.debug("Got unexpected normal inventory action with incomplete crafting transaction from " + this.getName() + ", refusing to execute crafting");
                        this.craftingTransaction = null;
                    }

                    switch (transactionPacket.transactionType) {
                        case InventoryTransactionPacket.TYPE_NORMAL:
                            InventoryTransaction transaction = new InventoryTransaction(this, actions);

                            if (!transaction.execute()) {
                                log.debug("Failed to execute inventory transaction from " + this.getName() + " with actions: " + Arrays.toString(transactionPacket.actions));
                                break packetswitch; //oops!
                            }

                            break packetswitch;
                        case InventoryTransactionPacket.TYPE_MISMATCH:
                            if (transactionPacket.actions.length > 0) {
                                log.debug("Expected 0 actions for mismatch, got " + transactionPacket.actions.length + ", " + Arrays.toString(transactionPacket.actions));
                            }
                            this.sendAllInventories();

                            break packetswitch;
                        case InventoryTransactionPacket.TYPE_USE_ITEM:
                            UseItemData useItemData = (UseItemData) transactionPacket.transactionData;

                            BlockVector3 blockVector = useItemData.blockPos;
                            BlockFace face = useItemData.face;
                            int type = useItemData.actionType;

                            if (face == null && type != InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_AIR) {
                                break packetswitch;
                            }

                            switch (type) {
                                case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_BLOCK:
                                    this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false);

                                    if (this.canInteract(blockVector.add(0.5, 0.5, 0.5), this.isCreative() ? 16 : 10)) {
                                        if (this.isCreative()) {
                                            Item i = inventory.getItemInHand();
                                            if (this.level.useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, this) != null) {
                                                break packetswitch;
                                            }
                                        } else if (inventory.getItemInHand().equals(useItemData.itemInHand)) {
                                            Item i = inventory.getItemInHand();
                                            Item oldItem = i.clone();
                                            //TODO: Implement adventure mode checks
                                            if ((i = this.level.useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, this)) != null) {
                                                if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                                    inventory.setItemInHand(i);
                                                    inventory.sendHeldItem(this.getViewers().values());
                                                }
                                                break packetswitch;
                                            }
                                        }
                                    }

                                    inventory.sendHeldItem(this);

                                    if (blockVector.distanceSquared(this) > 10000) {
                                        break packetswitch;
                                    }

                                    Block target = this.level.getBlock(blockVector.asVector3());
                                    block = target.getSide(face);

                                    this.level.sendBlocks(new Player[]{this}, new Block[]{target, block}, UpdateBlockPacket.FLAG_ALL_PRIORITY);

                                    if (target instanceof BlockDoor) {
                                        BlockDoor door = (BlockDoor) target;

                                        Block part;

                                        if ((door.getDamage() & 0x08) > 0) { //up
                                            part = target.down();

                                            if (part.getId() == target.getId()) {
                                                target = part;

                                                this.level.sendBlocks(new Player[]{this}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY);
                                            }
                                        }
                                    }
                                    break packetswitch;
                                case InventoryTransactionPacket.USE_ITEM_ACTION_BREAK_BLOCK:
                                    if (!this.spawned || !this.isAlive()) {
                                        break packetswitch;
                                    }

                                    this.resetCraftingGridType();

                                    Item i = this.getInventory().getItemInHand();

                                    Item oldItem = i.clone();

                                    if (this.canInteract(blockVector.add(0.5, 0.5, 0.5), this.isCreative() ? 16 : 8) && (i = this.level.useBreakOn(blockVector.asVector3(), i, this, true)) != null) {
                                        if (this.isSurvival()) {
                                            this.getFoodData().updateFoodExpLevel(0.025);
                                            if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                                inventory.setItemInHand(i);
                                                inventory.sendHeldItem(this.getViewers().values());
                                            }
                                        }
                                        break packetswitch;
                                    }

                                    inventory.sendContents(this);
                                    inventory.sendHeldItem(this);

                                    if (blockVector.distanceSquared(this) < 10000) {
                                        target = this.level.getBlock(blockVector.asVector3());
                                        this.level.sendBlocks(new Player[]{this}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY);

                                        BlockEntity blockEntity = this.level.getBlockEntity(blockVector.asVector3());
                                        if (blockEntity instanceof BlockEntitySpawnable) {
                                            ((BlockEntitySpawnable) blockEntity).spawnTo(this);
                                        }
                                    }

                                    break packetswitch;
                                case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_AIR:
                                    Vector3 directionVector = this.getDirectionVector();

                                    if (this.isCreative()) {
                                        item = this.inventory.getItemInHand();
                                    } else if (!this.inventory.getItemInHand().equals(useItemData.itemInHand)) {
                                        this.inventory.sendHeldItem(this);
                                        break packetswitch;
                                    } else {
                                        item = this.inventory.getItemInHand();
                                    }

                                    PlayerInteractEvent interactEvent = new PlayerInteractEvent(this, item, directionVector, face, Action.RIGHT_CLICK_AIR);

                                    this.server.getPluginManager().callEvent(interactEvent);

                                    if (interactEvent.isCancelled()) {
                                        this.inventory.sendHeldItem(this);
                                        break packetswitch;
                                    }

                                    if (item.onClickAir(this, directionVector) && this.isSurvival()) {
                                        this.inventory.setItemInHand(item);
                                    }

                                    this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, true);
                                    this.startAction = this.server.getTick();
                                    this.startActionTimestamp = System.currentTimeMillis();

                                    break packetswitch;
                                default:
                                    //unknown
                                    break;
                            }
                            break;
                        case InventoryTransactionPacket.TYPE_USE_ITEM_ON_ENTITY:
                            UseItemOnEntityData useItemOnEntityData = (UseItemOnEntityData) transactionPacket.transactionData;

                            Entity target = this.level.getEntity(useItemOnEntityData.entityRuntimeId);
                            if (target == null) {
                                item = this.inventory.getItemInHand();
                                PlayerInteractEvent interactEvent = new PlayerInteractEvent(this, item, this.getDirectionVector(), BlockFace.UP, Action.CLICK_UNKNOWN_ENTITY).setUnkownEntityId(useItemOnEntityData.entityRuntimeId);
                                this.server.getPluginManager().callEvent(interactEvent);
                                return;
                            }

                            type = useItemOnEntityData.actionType;

                            if (!useItemOnEntityData.itemInHand.equalsExact(this.inventory.getItemInHand())) {
                                this.inventory.sendHeldItem(this);
                            }

                            item = this.inventory.getItemInHand();

                            switch (type) {
                                case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_INTERACT:
                                    PlayerInteractEntityEvent playerInteractEntityEvent = new PlayerInteractEntityEvent(this, target, item, useItemOnEntityData.clickPos);
                                    if (this.isSpectator()) playerInteractEntityEvent.setCancelled();
                                    getServer().getPluginManager().callEvent(playerInteractEntityEvent);

                                    if (playerInteractEntityEvent.isCancelled()) {
                                        break;
                                    }

                                    if (target.onInteract(this, item) && this.isSurvival()) {
                                        if (item.isTool()) {
                                            if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                                                item = new ItemBlock(Block.get(BlockID.AIR));
                                            }
                                        } else {
                                            if (item.count > 1) {
                                                item.count--;
                                            } else {
                                                item = new ItemBlock(Block.get(BlockID.AIR));
                                            }
                                        }

                                        this.inventory.setItemInHand(item);
                                    }
                                    break;
                                case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_ATTACK:
                                    if (!this.canInteract(target, isCreative() ? 8 : 5)) {
                                        break;
                                    } else if (target instanceof Player) {
                                        if ((((Player) target).getGamemode() & 0x01) > 0) {
                                            break;
                                        } else if (!this.server.getPropertyBoolean("pvp")) {
                                            break;
                                        }
                                    }

                                    Enchantment[] enchantments = item.getEnchantments();

                                    float itemDamage = item.getAttackDamage();
                                    for (Enchantment enchantment : enchantments) {
                                        itemDamage += enchantment.getDamageBonus(target);
                                    }

                                    Map<DamageModifier, Float> damage = new EnumMap<>(DamageModifier.class);
                                    damage.put(DamageModifier.BASE, itemDamage);

                                    float knockBack = 0.29f;
                                    Enchantment knockBackEnchantment = item.getEnchantment(Enchantment.ID_KNOCKBACK);
                                    if (knockBackEnchantment != null) {
                                        knockBack += knockBackEnchantment.getLevel() * 0.1f;
                                    }

                                    EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(this, target, DamageCause.ENTITY_ATTACK, damage, knockBack, enchantments);
                                    if (this.isSpectator()) entityDamageByEntityEvent.setCancelled();
                                    if ((target instanceof Player) && !this.level.getGameRules().getBoolean(GameRule.PVP)) {
                                        entityDamageByEntityEvent.setCancelled();
                                    }

                                    if (!target.attack(entityDamageByEntityEvent)) {
                                        if (item.isTool() && this.isSurvival()) {
                                            this.inventory.sendContents(this);
                                        }
                                        break;
                                    }

                                    for (Enchantment enchantment : item.getEnchantments()) {
                                        enchantment.doPostAttack(this, target);
                                    }

                                    if (item.isTool() && (this.isSurvival() || this.isAdventure())) {
                                        if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                                            this.inventory.setItemInHand(Item.get(0));
                                        } else {
                                            if (item.getId() == 0 || this.inventory.getItemInHand().getId() == item.getId()) {
                                                this.inventory.setItemInHand(item);
                                            } else {
                                                server.getLogger().debug("Tried to set item " + item.getId() + " but " + this.username + " had item " + this.inventory.getItemInHand().getId() + " in their hand slot");
                                            }
                                        }
                                    }
                                    return;
                                default:
                                    break; //unknown
                            }

                            break;
                        case InventoryTransactionPacket.TYPE_RELEASE_ITEM:
                            if (this.isSpectator()) {
                                this.sendAllInventories();
                                break packetswitch;
                            }
                            ReleaseItemData releaseItemData = (ReleaseItemData) transactionPacket.transactionData;

                            try {
                                type = releaseItemData.actionType;
                                switch (type) {
                                    case InventoryTransactionPacket.RELEASE_ITEM_ACTION_RELEASE:
                                        if (this.isUsingItem()) {
                                            item = this.inventory.getItemInHand();
                                            // Used item
                                            //int ticksUsed = this.server.getTick() - this.startAction;
                                            int ticksUsed = (int) (System.currentTimeMillis() - this.startActionTimestamp) / 50;

                                            if (item.onRelease(this, ticksUsed)) {
                                                this.inventory.setItemInHand(item);
                                            }
                                        } else {
                                            this.inventory.sendContents(this);
                                        }
                                        return;
                                    case InventoryTransactionPacket.RELEASE_ITEM_ACTION_CONSUME:
                                        Item itemInHand = this.inventory.getItemInHand();
                                        PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(this, itemInHand);

                                        if (itemInHand.getId() == Item.POTION) {
                                            this.server.getPluginManager().callEvent(consumeEvent);
                                            if (consumeEvent.isCancelled()) {
                                                this.inventory.sendContents(this);
                                                break;
                                            }
                                            Potion potion = Potion.getPotion(itemInHand.getDamage()).setSplash(false);

                                            if (this.getGamemode() == SURVIVAL) {
                                                --itemInHand.count;
                                                this.inventory.setItemInHand(itemInHand);
                                                this.inventory.addItem(new ItemGlassBottle());
                                            }

                                            if (potion != null) {
                                                potion.applyPotion(this);
                                            }

                                        } else if (itemInHand.getId() == Item.BUCKET && itemInHand.getDamage() == 1) { //milk
                                            this.server.getPluginManager().callEvent(consumeEvent);
                                            if (consumeEvent.isCancelled()) {
                                                this.inventory.sendContents(this);
                                                break;
                                            }

                                            EntityEventPacket eventPacket = new EntityEventPacket();
                                            eventPacket.eid = this.getId();
                                            eventPacket.event = EntityEventPacket.USE_ITEM;
                                            this.dataPacket(eventPacket);
                                            Server.broadcastPacket(this.getViewers().values(), eventPacket);

                                            if (this.isSurvival()) {
                                                itemInHand.count--;
                                                this.inventory.setItemInHand(itemInHand);
                                                this.inventory.addItem(new ItemBucket());
                                            }

                                            this.removeAllEffects();
                                        } else {
                                            this.server.getPluginManager().callEvent(consumeEvent);
                                            if (consumeEvent.isCancelled()) {
                                                this.inventory.sendContents(this);
                                                break;
                                            }

                                            Food food = Food.getByRelative(itemInHand);
                                            if (food != null && food.eatenBy(this)) --itemInHand.count;
                                            this.inventory.setItemInHand(itemInHand);
                                        }
                                        return;
                                    default:
                                        break;
                                }
                            } finally {
                                this.setUsingItem(false);
                            }
                            break;
                        default:
                            this.inventory.sendContents(this);
                            break;
                    }
                    break;
                case ProtocolInfo.PLAYER_HOTBAR_PACKET:
                    PlayerHotbarPacket hotbarPacket = (PlayerHotbarPacket) packet;

                    if (hotbarPacket.windowId != ContainerIds.INVENTORY) {
                        return; //In PE this should never happen
                    }

                    this.inventory.equipItem(hotbarPacket.selectedHotbarSlot);
                    break;
                case ProtocolInfo.SERVER_SETTINGS_REQUEST_PACKET:
                    PlayerServerSettingsRequestEvent settingsRequestEvent = new PlayerServerSettingsRequestEvent(this, new Int2ObjectOpenHashMap<>(this.serverSettings));
                    this.getServer().getPluginManager().callEvent(settingsRequestEvent);

                    if (!settingsRequestEvent.isCancelled()) {
                        settingsRequestEvent.getSettings().forEach((id, window) -> {
                            ServerSettingsResponsePacket re = new ServerSettingsResponsePacket();
                            re.formId = id;
                            re.data = window.getJSONData();
                            this.dataPacket(re);
                        });
                    }
                    break;
                case ProtocolInfo.BOOK_EDIT_PACKET:
                    BookEditPacket bookEditPacket = (BookEditPacket) packet;
                    Item oldBook = this.inventory.getItem(bookEditPacket.inventorySlot);
                    if (oldBook.getId() != Item.WRITABLE_BOOK) {
                        return;
                    }

                    if (bookEditPacket.text != null && bookEditPacket.text.length() > 256) {
                        this.getServer().getLogger().debug(this.getName() + ": BookEditPacket with too long text");
                        return;
                    }

                    Item newBook = oldBook.clone();
                    boolean success;
                    switch (bookEditPacket.action) {
                        case REPLACE_PAGE:
                            success = ((ItemBookAndQuill) newBook).setPageText(bookEditPacket.pageNumber, bookEditPacket.text);
                            break;
                        case ADD_PAGE:
                            success = ((ItemBookAndQuill) newBook).insertPage(bookEditPacket.pageNumber, bookEditPacket.text);
                            break;
                        case DELETE_PAGE:
                            success = ((ItemBookAndQuill) newBook).deletePage(bookEditPacket.pageNumber);
                            break;
                        case SWAP_PAGES:
                            success = ((ItemBookAndQuill) newBook).swapPages(bookEditPacket.pageNumber, bookEditPacket.secondaryPageNumber);
                            break;
                        case SIGN_BOOK:
                            if (bookEditPacket.title == null || bookEditPacket.author == null || bookEditPacket.xuid == null || bookEditPacket.title.length() > 64 || bookEditPacket.author.length() > 64 || bookEditPacket.xuid.length() > 64) {
                                this.getServer().getLogger().debug(this.getName() + ": Invalid BookEditPacket action SIGN_BOOK: title/author/xuid is too long");
                                return;
                            }
                            newBook = Item.get(Item.WRITTEN_BOOK, 0, 1, oldBook.getCompoundTag());
                            success = ((ItemBookWritten) newBook).signBook(bookEditPacket.title, bookEditPacket.author, bookEditPacket.xuid, ItemBookWritten.GENERATION_ORIGINAL);
                            break;
                        default:
                            return;
                    }

                    if (success) {
                        PlayerEditBookEvent editBookEvent = new PlayerEditBookEvent(this, oldBook, newBook, bookEditPacket.action);
                        this.server.getPluginManager().callEvent(editBookEvent);
                        if (!editBookEvent.isCancelled()) {
                            this.inventory.setItem(bookEditPacket.inventorySlot, editBookEvent.getNewBook());
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Sends a chat message as this player. If the message begins with a / (forward-slash) it will be treated
     * as a command.
     * @param message message to send
     * @return successful
     */
    public boolean chat(String message) {
        if (!this.spawned || !this.isAlive()) {
            return false;
        }

        this.resetCraftingGridType();
        this.craftingType = CRAFTING_SMALL;

        if (this.removeFormat) {
            message = TextFormat.clean(message, true);
        }

        for (String msg : message.split("\n")) {
            if (!msg.trim().isEmpty() && msg.length() <= 512 && this.messageCounter-- > 0) {
                PlayerChatEvent chatEvent = new PlayerChatEvent(this, msg);
                this.server.getPluginManager().callEvent(chatEvent);
                if (!chatEvent.isCancelled()) {
                    this.server.broadcastMessage(this.getServer().getLanguage().translateString(chatEvent.getFormat(), new String[]{chatEvent.getPlayer().getDisplayName(), chatEvent.getMessage()}), chatEvent.getRecipients());
                }
            }
        }

        return true;
    }

    public boolean kick() {
        return this.kick("");
    }

    public boolean kick(String reason, boolean isAdmin) {
        return this.kick(PlayerKickEvent.Reason.UNKNOWN, reason, isAdmin);
    }

    public boolean kick(String reason) {
        return kick(PlayerKickEvent.Reason.UNKNOWN, reason);
    }

    public boolean kick(PlayerKickEvent.Reason reason) {
        return this.kick(reason, true);
    }

    public boolean kick(PlayerKickEvent.Reason reason, String reasonString) {
        return this.kick(reason, reasonString, true);
    }

    public boolean kick(PlayerKickEvent.Reason reason, boolean isAdmin) {
        return this.kick(reason, reason.toString(), isAdmin);
    }

    public boolean kick(PlayerKickEvent.Reason reason, String reasonString, boolean isAdmin) {
        PlayerKickEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerKickEvent(this, reason, this.getLeaveMessage()));
        if (!ev.isCancelled()) {
            String message;
            if (isAdmin) {
                if (!this.isBanned()) {
                    message = "Kicked by admin." + (!reasonString.isEmpty() ? " Reason: " + reasonString : "");
                } else {
                    message = reasonString;
                }
            } else {
                if (reasonString.isEmpty()) {
                    message = "disconnectionScreen.noReason";
                } else {
                    message = reasonString;
                }
            }

            this.close(ev.getQuitMessage(), message);

            return true;
        }

        return false;
    }

    public void setViewDistance(int distance) {
        this.chunkRadius = distance;

        ChunkRadiusUpdatedPacket pk = new ChunkRadiusUpdatedPacket();
        pk.radius = distance;

        this.dataPacket(pk);
    }

    public int getViewDistance() {
        return this.chunkRadius;
    }

    @Override
    public void sendMessage(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_RAW;
        pk.message = this.server.getLanguage().translateString(message);
        pk.setChannel(DataPacket.CHANNEL_TEXT);
        this.dataPacket(pk);
    }

    @Override
    public void sendMessage(TextContainer message) {
        if (message instanceof TranslationContainer) {
            this.sendTranslation(message.getText(), ((TranslationContainer) message).getParameters());
            return;
        }
        this.sendMessage(message.getText());
    }

    public void sendTranslation(String message) {
        this.sendTranslation(message, new String[0]);
    }

    public void sendTranslation(String message, String[] parameters) {
        TextPacket pk = new TextPacket();
        if (!this.server.isLanguageForced()) {
            pk.type = TextPacket.TYPE_TRANSLATION;
            pk.message = this.server.getLanguage().translateString(message, parameters, "nukkit.");
            pk.isLocalized = true;
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = this.server.getLanguage().translateString(parameters[i], parameters, "nukkit.");
            }
            pk.parameters = parameters;
        } else {
            pk.type = TextPacket.TYPE_RAW;
            pk.message = this.server.getLanguage().translateString(message, parameters);
        }
        pk.setChannel(DataPacket.CHANNEL_TEXT);
        this.dataPacket(pk);
    }

    public void sendChat(String message) {
        this.sendChat("", message);
    }

    public void sendChat(String source, String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_CHAT;
        pk.primaryName = source;
        pk.message = this.server.getLanguage().translateString(message);
        pk.setChannel(DataPacket.CHANNEL_TEXT);
        this.dataPacket(pk);
    }

    public void sendPopup(String message) {
        this.sendPopup(message, "");
    }

    public void sendPopup(String message, String subtitle) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_POPUP;
        pk.primaryName = subtitle;
        pk.message = message;
        pk.setChannel(DataPacket.CHANNEL_TEXT);
        this.dataPacket(pk);
    }

    public void sendTip(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_TIP;
        pk.message = message;
        pk.setChannel(DataPacket.CHANNEL_TEXT);
        this.dataPacket(pk);
    }

    public void clearTitle() {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_CLEAR;
        pk.setChannel(DataPacket.CHANNEL_TEXT);
        this.dataPacket(pk);
    }

    /**
     * Resets both title animation times and subtitle for the next shown title
     */
    public void resetTitleSettings() {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_RESET;
        pk.setChannel(DataPacket.CHANNEL_TEXT);
        this.dataPacket(pk);
    }

    public void setSubtitle(String subtitle) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_SUBTITLE;
        pk.text = subtitle;
        pk.setChannel(DataPacket.CHANNEL_TEXT);
        this.dataPacket(pk);
    }

    public void setTitleAnimationTimes(int fadein, int duration, int fadeout) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_ANIMATION_TIMES;
        pk.fadeInTime = fadein;
        pk.stayTime = duration;
        pk.fadeOutTime = fadeout;
        pk.setChannel(DataPacket.CHANNEL_TEXT);
        this.dataPacket(pk);
    }

    public void sendTitle(String title) {
        this.sendTitle(title, "", 20, 20, 5);
    }

    public void sendTitle(String title, String subtitle) {
        this.sendTitle(title, subtitle, 20, 20, 5);
    }

    public void sendTitle(String title, String subtitle, int fadein, int duration, int fadeout) {
        if (!subtitle.equals("")) {
            SetTitlePacket pk = new SetTitlePacket();
            pk.type = SetTitlePacket.TYPE_SUBTITLE;
            pk.text = subtitle;
            pk.fadeInTime = fadein;
            pk.stayTime = duration;
            pk.fadeOutTime = fadeout;
            pk.setChannel(DataPacket.CHANNEL_TEXT);
            this.dataPacket(pk);
        }
        SetTitlePacket pk2 = new SetTitlePacket();
        pk2.type = SetTitlePacket.TYPE_TITLE;
        pk2.text = title;
        pk2.fadeInTime = fadein;
        pk2.stayTime = duration;
        pk2.fadeOutTime = fadeout;
        pk2.setChannel(DataPacket.CHANNEL_TEXT);
        this.dataPacket(pk2);
    }

    public void sendActionBar(String title) {
        this.sendActionBar(title, 1, 0, 1);
    }

    public void sendActionBar(String title, int fadein, int duration, int fadeout) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_ACTION_BAR;
        pk.text = title;
        pk.fadeInTime = fadein;
        pk.stayTime = duration;
        pk.fadeOutTime = fadeout;
        pk.setChannel(DataPacket.CHANNEL_TEXT);
        this.dataPacket(pk);
    }

    @Override
    public void close() {
        this.close("");
    }

    public void close(String message) {
        this.close(message, "generic");
    }

    public void close(String message, String reason) {
        this.close(message, reason, true);
    }

    public void close(String message, String reason, boolean notify) {
        this.close(new TextContainer(message), reason, notify);
    }

    public void close(TextContainer message) {
        this.close(message, "generic");
    }

    public void close(TextContainer message, String reason) {
        this.close(message, reason, true);
    }

    public void close(TextContainer message, String reason, boolean notify) {
        if (this.connected && !this.closed) {
            if (notify && reason.length() > 0) {
                DisconnectPacket pk = new DisconnectPacket();
                pk.message = reason;
                this.dataPacket(pk);
            }

            this.connected = false;
            PlayerQuitEvent ev = null;
            if (this.getName() != null && this.getName().length() > 0) {
                this.server.getPluginManager().callEvent(ev = new PlayerQuitEvent(this, message, true, reason));
                if (this.loggedIn && ev.getAutoSave()) {
                    this.save();
                }
                if (this.fishing != null) {
                    this.stopFishing(false);
                }
            }

            for (Player player : new ObjectArrayList<>(this.server.getOnlinePlayers().values())) {
                if (!player.canSee(this)) {
                    player.showPlayer(this);
                }
            }

            this.hiddenPlayers = new Object2ObjectOpenHashMap<>();

            this.removeAllWindows(true);

            List<Player> needRemovePlayerListFrom = this.getServer().getOnlinePlayers().values().stream().filter(p -> p.sentSkins.contains(this.getUniqueId())).collect(Collectors.toList());
            needRemovePlayerListFrom.forEach(p -> p.sentSkins.remove(this.getUniqueId()));
            this.getServer().removePlayerListData(this.getUniqueId(), needRemovePlayerListFrom);

            for (long index : new LongArrayList(this.usedChunks.keySet())) {
                int chunkX = Level.getHashX(index);
                int chunkZ = Level.getHashZ(index);
                this.level.unregisterChunkLoader(this, chunkX, chunkZ);
                this.usedChunks.remove(index);

                for (Entity entity : level.getChunkEntities(chunkX, chunkZ).values()) {
                    if (entity != this) {
                        entity.getViewers().remove(getLoaderId());
                    }
                }
            }

            super.close();

            this.interfaz.close(this, notify ? reason : "");

            if (this.loggedIn) {
                this.server.removeOnlinePlayer(this);
            }

            this.loggedIn = false;

            if (ev != null && !Objects.equals(this.username, "") && this.spawned && !Objects.equals(ev.getQuitMessage().toString(), "")) {
                this.server.broadcastMessage(ev.getQuitMessage());
            }

            this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_USERS, this);
            this.spawned = false;
            log.info(this.getServer().getLanguage().translateString("nukkit.player.logOut",
                    TextFormat.AQUA + (this.getName() == null ? "" : this.getName()) + TextFormat.WHITE,
                    this.getAddress(),
                    String.valueOf(this.getPort()),
                    this.getServer().getLanguage().translateString(reason)));
            this.windows.clear();
            this.windowIndex.clear();
            this.usedChunks = new Long2BooleanOpenHashMap();
            this.loadQueue = new Long2IntOpenHashMap();
            this.hasSpawned = new Int2ObjectOpenHashMap<>();
            this.spawnPosition = null;

            if (this.riding instanceof EntityRideable) {
                this.riding.passengers.remove(this);
            }

            this.riding = null;
        }

        if (this.perm != null) {
            this.perm.clearPermissions();
            this.perm = null;
        }

        if (this.inventory != null) {
            //总是发生this.inventory == null的现象
            //this.inventory = null;
        }

        this.chunk = null;

        this.server.removePlayer(this);
    }

    public void save() {
        this.save(false);
    }

    public void save(boolean async) {
        if (this.closed) {
            throw new IllegalStateException("Tried to save closed player");
        }

        super.saveNBT();

        if (this.level != null) {
            this.namedTag.putString("Level", this.level.getFolderName());
            if (this.spawnPosition != null && this.spawnPosition.getLevel() != null) {
                this.namedTag.putString("SpawnLevel", this.spawnPosition.getLevel().getFolderName());
                this.namedTag.putInt("SpawnX", (int) this.spawnPosition.x);
                this.namedTag.putInt("SpawnY", (int) this.spawnPosition.y);
                this.namedTag.putInt("SpawnZ", (int) this.spawnPosition.z);
            }
            if (this.spawnBlockPosition != null && this.spawnBlockPosition.level != null) {
                this.namedTag.putString("SpawnBlockPositionLevel", this.spawnBlockPosition.level.getFolderName());
                this.namedTag.putInt("SpawnBlockPositionX", this.spawnBlockPosition.getFloorX());
                this.namedTag.putInt("SpawnBlockPositionY", this.spawnBlockPosition.getFloorY());
                this.namedTag.putInt("SpawnBlockPositionZ", this.spawnBlockPosition.getFloorZ());
            }

            /*CompoundTag achievements = new CompoundTag();
            for (String achievement : this.achievements) {
                achievements.putByte(achievement, 1);
            }

            this.namedTag.putCompound("Achievements", achievements);*/

            this.namedTag.putInt("playerGameType", this.gamemode);
            this.namedTag.putLong("lastPlayed", System.currentTimeMillis() / 1000);

            this.namedTag.putString("lastIP", this.getAddress());

            this.namedTag.putInt("EXP", this.getExperience());
            this.namedTag.putInt("expLevel", this.getExperienceLevel());

            this.namedTag.putInt("foodLevel", this.getFoodData().getLevel());
            this.namedTag.putFloat("foodSaturationLevel", this.getFoodData().getFoodSaturationLevel());

            if (!this.username.isEmpty() && this.namedTag != null) {
                this.server.saveOfflinePlayerData(this.username, this.namedTag, async);
            }
        }
    }

    public String getName() {
        return this.username;
    }

    public int getProtocol() {
        return this.protocol;
    }

    @Override
    public void kill() {
        if (!this.spawned) {
            return;
        }

        boolean showMessages = this.level.getGameRules().getBoolean(GameRule.SHOW_DEATH_MESSAGES);
        String message = "death.attack.generic";

        List<String> params = new ObjectArrayList<>();
        params.add(this.getDisplayName());
        if (showMessages) {

            EntityDamageEvent cause = this.getLastDamageCause();

            switch (cause == null ? DamageCause.CUSTOM : cause.getCause()) {
                case ENTITY_ATTACK:
                    if (cause instanceof EntityDamageByEntityEvent) {
                        Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                        killer = e;
                        if (e instanceof Player) {
                            message = "death.attack.player";
                            params.add(((Player) e).getDisplayName());
                            break;
                        } else if (e instanceof EntityLiving) {
                            message = "death.attack.mob";
                            params.add(!Objects.equals(e.getNameTag(), "") ? e.getNameTag() : e.getName());
                            break;
                        } else {
                            params.add("Unknown");
                        }
                    }
                    break;
                case PROJECTILE:
                    if (cause instanceof EntityDamageByEntityEvent) {
                        Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                        killer = e;
                        if (e instanceof Player) {
                            message = "death.attack.arrow";
                            params.add(((Player) e).getDisplayName());
                        } else if (e instanceof EntityLiving) {
                            message = "death.attack.arrow";
                            params.add(!Objects.equals(e.getNameTag(), "") ? e.getNameTag() : e.getName());
                            break;
                        } else {
                            params.add("Unknown");
                        }
                    }
                    break;
                case SUICIDE:
                    message = "death.attack.generic";
                    break;
                case VOID:
                    message = "death.attack.outOfWorld";
                    break;
                case FALL:
                    if (cause != null) {
                        if (cause.getFinalDamage() > 2) {
                            message = "death.fell.accident.generic";
                            break;
                        }
                    }
                    message = "death.attack.fall";
                    break;

                case SUFFOCATION:
                    message = "death.attack.inWall";
                    break;

                case LAVA:
                    message = "death.attack.lava";
                    break;

                case FIRE:
                    message = "death.attack.onFire";
                    break;

                case FIRE_TICK:
                    message = "death.attack.inFire";
                    break;

                case DROWNING:
                    message = "death.attack.drown";
                    break;

                case CONTACT:
                    if (cause instanceof EntityDamageByBlockEvent) {
                        int id = ((EntityDamageByBlockEvent) cause).getDamager().getId();
                        if (id == Block.CACTUS) {
                            message = "death.attack.cactus";
                        } else if (id == Block.ANVIL) {
                            message = "death.attack.anvil";
                        }
                    }
                    break;

                case BLOCK_EXPLOSION:
                case ENTITY_EXPLOSION:
                    if (cause instanceof EntityDamageByEntityEvent) {
                        Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                        killer = e;
                        if (e instanceof Player) {
                            message = "death.attack.explosion.player";
                            params.add(((Player) e).getDisplayName());
                        } else if (e instanceof EntityLiving) {
                            message = "death.attack.explosion.player";
                            params.add(!Objects.equals(e.getNameTag(), "") ? e.getNameTag() : e.getName());
                            break;
                        }
                    } else {
                        message = "death.attack.explosion";
                    }
                    break;

                case MAGIC:
                    message = "death.attack.magic";
                    break;

                case CUSTOM:
                    break;

                default:
                    break;

            }
        } else {
            message = "";
            params.clear();
        }

        this.health = 0;
        this.scheduleUpdate();

        PlayerDeathEvent ev = new PlayerDeathEvent(this, this.getDrops(), new TranslationContainer(message, params.toArray(new String[0])), this.getExperienceLevel());

        ev.setKeepExperience(this.level.gameRules.getBoolean(GameRule.KEEP_INVENTORY));
        ev.setKeepInventory(ev.getKeepExperience());
        this.server.getPluginManager().callEvent(ev);

        if (!ev.getKeepInventory() && this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            for (Item item : ev.getDrops()) {
                if (!item.hasEnchantment(Enchantment.ID_VANISHING)) {
                    this.level.dropItem(this, item, null, true, 40);
                }
            }

            if (this.inventory != null) {
                for (int slot = 0; slot < inventory.getSize(); slot++) {
                    Item item = inventory.getItem(slot);
                    if (!item.isKeepOnDeath()) {
                        inventory.setItem(slot, Item.get(Item.AIR));
                    }
                }
            }
        }

        if (!ev.getKeepExperience() && this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            if (this.isSurvival() || this.isAdventure()) {
                int exp = ev.getExperience() * 7;
                if (exp > 100) exp = 100;
                this.getLevel().dropExpOrb(this, exp);
            }
            this.setExperience(0, 0);
        }

        if (showMessages && !ev.getDeathMessage().toString().isEmpty()) {
            this.server.broadcast(ev.getDeathMessage(), Server.BROADCAST_CHANNEL_USERS);
        }


        RespawnPacket pk = new RespawnPacket();
        Position pos = this.getSpawn();
        pk.x = (float) pos.x;
        pk.y = (float) pos.y;
        pk.z = (float) pos.z;
        this.dataPacket(pk);
    }

    @Override
    public void setHealth(float health) {
        if (health < 1) {
            health = 0;
        }

        super.setHealth(health);
        //TODO: Remove it in future! This a hack to solve the client-side absorption bug! WFT Mojang (Half a yellow heart cannot be shown, we can test it in local gaming)
        Attribute attr = Attribute.getAttribute(Attribute.HEALTH).setMaxValue(this.getAbsorption() % 2 != 0 ? this.getMaxHealth() + 1 : this.getMaxHealth()).setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);
        if (this.spawned) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            pk.entries = new Attribute[]{attr};
            pk.entityId = this.id;
            this.dataPacket(pk);
        }
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);

        Attribute attr = Attribute.getAttribute(Attribute.HEALTH).setMaxValue(this.getAbsorption() % 2 != 0 ? this.getMaxHealth() + 1 : this.getMaxHealth()).setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);
        if (this.spawned) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            pk.entries = new Attribute[]{attr};
            pk.entityId = this.id;
            this.dataPacket(pk);
        }
    }

    public int getExperience() {
        return this.exp;
    }

    public int getExperienceLevel() {
        return this.expLevel;
    }

    public void addExperience(int add) {
        if (add == 0) return;
        int now = this.getExperience();
        int added = now + add;
        int level = this.getExperienceLevel();
        int most = calculateRequireExperience(level);
        while (added >= most) {  //Level Up!
            added = added - most;
            level++;
            most = calculateRequireExperience(level);
        }
        this.setExperience(added, level);
    }

    public static int calculateRequireExperience(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else if (level >= 15) {
            return 37 + (level - 15) * 5;
        } else {
            return 7 + level * 2;
        }
    }

    public void setExperience(int exp) {
        setExperience(exp, this.getExperienceLevel());
    }

    public void setExperience(int exp, int level) {
        this.exp = exp;
        this.expLevel = level;

        this.sendExperienceLevel(level);
        this.sendExperience(exp);
    }

    public void sendExperience() {
        sendExperience(this.getExperience());
    }

    public void sendExperience(int exp) {
        if (this.spawned) {
            float percent = ((float) exp) / calculateRequireExperience(this.getExperienceLevel());
            percent = Math.max(0f, Math.min(1f, percent));
            this.setAttribute(Attribute.getAttribute(Attribute.PLAYER_EXPERIENCE).setValue(percent));
        }
    }

    public void sendExperienceLevel() {
        sendExperienceLevel(this.getExperienceLevel());
    }

    public void sendExperienceLevel(int level) {
        if (this.spawned) {
            this.setAttribute(Attribute.getAttribute(Attribute.PLAYER_LEVEL).setValue(level));
        }
    }

    public void setAttribute(Attribute attribute) {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entries = new Attribute[]{attribute};
        pk.entityId = this.id;
        this.dataPacket(pk);
    }

    @Override
    public void setMovementSpeed(float speed) {
        setMovementSpeed(speed, true);
    }

    public void setMovementSpeed(float speed, boolean send) {
        super.setMovementSpeed(speed);
        if (this.spawned && send) {
            Attribute attribute = Attribute.getAttribute(Attribute.MOVEMENT).setValue(speed);
            this.setAttribute(attribute);
        }
    }

    public Entity getKiller() {
        return killer;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (!this.isAlive()) {
            return false;
        }

        if (this.isCreative()
                && source.getCause() != DamageCause.MAGIC
                && source.getCause() != DamageCause.SUICIDE
                && source.getCause() != DamageCause.VOID
                ) {
            //source.setCancelled();
            return false;
        } else if (this.getAdventureSettings().get(Type.ALLOW_FLIGHT) && source.getCause() == DamageCause.FALL) {
            //source.setCancelled();
            return false;
        } else if (source.getCause() == DamageCause.FALL) {
            if (this.getLevel().getBlock(this.getPosition().floor().add(0.5, -1, 0.5)).getId() == Block.SLIME) {
                if (!this.isSneaking()) {
                    //source.setCancelled();
                    this.resetFallDistance();
                    return false;
                }
            }
        }
        boolean add = false;
        boolean doubleCritical = false;
        if (source instanceof EntityDamageByEntityEvent && source.getCause() == DamageCause.ENTITY_ATTACK) {
            Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
            if (damager instanceof Player) {
                ((Player) damager).getFoodData().updateFoodExpLevel(0.3);
            }
            //Critical hit

            if (!damager.onGround && damager instanceof Player) {
                if (((Player) damager).speed != null && ((Player) damager).speed.y > 0) {
                    //((Player) damager).sendMessage("speed = " + ((Player) damager).speed.y);
                    if (((Player) damager).attackCriticalThisJump <= 0) add = true;
                    else doubleCritical = true;
                }
            }
            if (add) {
                source.setDamage((float) (source.getDamage() * 1.3));
                AnimatePacket animate = new AnimatePacket();
                animate.action = AnimatePacket.Action.CRITICAL_HIT;
                animate.eid = getId();
                this.getLevel().addChunkPacket(damager.getChunkX(), damager.getChunkZ(), animate);
                this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ATTACK_STRONG);
            }
            if (doubleCritical) {
                //正在叠刀
                Player damagerPlayer = (Player)((EntityDamageByEntityEvent) source).getDamager();
                if (damagerPlayer.attackCriticalThisJump < 2) {
                    this.nextAllowAttack = 0;
                    // this.attackTime = 0;
                    source.setDamage((float) (source.getDamage() * 0.2));
                }
                damagerPlayer.sendPopup("叠刀 × " + damagerPlayer.attackCriticalThisJump);
                AnimatePacket animate = new AnimatePacket();
                animate.action = AnimatePacket.Action.CRITICAL_HIT;
                animate.eid = getId();
                this.getLevel().addChunkPacket(damager.getChunkX(), damager.getChunkZ(), animate);
                this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ATTACK_STRONG);
            }
        }

        if (super.attack(source)) { //!source.isCancelled()
            this.stopSleep();

            if (this.getLastDamageCause() == source && this.spawned) {
                this.getFoodData().updateFoodExpLevel(0.3);
                EntityEventPacket pk = new EntityEventPacket();
                pk.eid = this.id;
                pk.event = EntityEventPacket.HURT_ANIMATION;
                Server.broadcastPacket(this.hasSpawned.values(), pk);
                this.dataPacket(pk);
                if (add) {
                    if (((EntityDamageByEntityEvent) source).getDamager() instanceof Player) {
                        if (((Player) ((EntityDamageByEntityEvent) source).getDamager()).getLoginChainData().getDeviceOS() == 7)  //win10
                            (((EntityDamageByEntityEvent) source).getDamager()).addEffect(Effect.getEffect(Effect.SLOWNESS).setDuration(10).setAmplifier(1).setVisible(false));
                        ((Player)(((EntityDamageByEntityEvent) source).getDamager())).attackCriticalThisJump++;
                    }
                    /*
                    Random random = ThreadLocalRandom.current();
                    for (int i = 0; i < 10; i++) {
                        CriticalParticle par = new CriticalParticle(new Vector3(this.x + random.nextDouble() * 2 - 1, this.y + random.nextDouble() * 2, this.z + random.nextDouble() * 2 - 1));
                        this.getLevel().addParticle(par);
                    }*/
                }
                if (doubleCritical) {
                    if (((EntityDamageByEntityEvent) source).getDamager() instanceof Player) {
                        Player damagerPlayer = (Player)((EntityDamageByEntityEvent) source).getDamager();
                        damagerPlayer.attackCriticalThisJump++;
                    }
                }

            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Drops an item on the ground in front of the player. Returns if the item drop was successful.
     *
     * @return bool if the item was dropped or if the item was null
     */
    public boolean dropItem(Item item) {
        if (!this.spawned || !this.isAlive()) {
            return false;
        }

        if (item.isNull()) {
            log.debug(this.getName() + " attempted to drop a null item (" + item + ")");
            return true;
        }

        Vector3 motion = this.getDirectionVector().multiply(0.4);

        this.level.dropItem(this.add(0, 1.3, 0), item, motion, 40);

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false);
        return true;
    }

    public void sendPosition(Vector3 pos) {
        this.sendPosition(pos, this.yaw);
    }

    public void sendPosition(Vector3 pos, double yaw) {
        this.sendPosition(pos, yaw, this.pitch);
    }

    public void sendPosition(Vector3 pos, double yaw, double pitch) {
        this.sendPosition(pos, yaw, pitch, MovePlayerPacket.MODE_NORMAL);
    }

    public void sendPosition(Vector3 pos, double yaw, double pitch, int mode) {
        this.sendPosition(pos, yaw, pitch, mode, null);
    }

    public void sendPosition(Vector3 pos, double yaw, double pitch, int mode, Player[] targets) {
        MovePlayerPacket pk = new MovePlayerPacket();
        pk.eid = this.getId();
        pk.x = (float) pos.x;
        pk.y = (float) (pos.y + this.getEyeHeight());
        pk.z = (float) pos.z;
        pk.headYaw = (float) yaw;
        pk.pitch = (float) pitch;
        pk.yaw = (float) yaw;
        pk.mode = mode;
        if (this.riding != null) {
            pk.ridingEid = this.riding.getId();
        }
        pk.setChannel(DataPacket.CHANNEL_PLAYER_MOVING);

        if (targets != null) {
            Server.broadcastPacket(targets, pk);
        } else {
            pk.eid = this.id;
            this.dataPacket(pk);
        }
    }

    @Override
    protected void checkChunks() {
        if (this.chunk == null || (this.chunk.getX() != ((int) this.x >> 4) || this.chunk.getZ() != ((int) this.z >> 4))) {
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }
            this.chunk = this.level.getChunk((int) this.x >> 4, (int) this.z >> 4, true);

            if (!this.justCreated) {
                Int2ObjectMap<Player> newChunk = this.level.getChunkPlayers((int) this.x >> 4, (int) this.z >> 4);
                newChunk.remove(this.getLoaderId());

                //List<Player> reload = new ObjectArrayList<>();
                for (Player player : new ObjectArrayList<>(this.hasSpawned.values())) {
                    if (!newChunk.containsKey(player.getLoaderId())) {
                        this.despawnFrom(player);
                    } else {
                        newChunk.remove(player.getLoaderId());
                        //reload.add(player);
                    }
                }

                for (Player player : newChunk.values()) {
                    this.spawnTo(player);
                }
            }

            if (this.chunk == null) {
                return;
            }

            this.chunk.addEntity(this);
        }
    }

    protected boolean checkTeleportPosition() {
        if (this.teleportPosition != null) {
            int chunkX = (int) this.teleportPosition.x >> 4;
            int chunkZ = (int) this.teleportPosition.z >> 4;

            long centerChunkIndex = Level.chunkHash(chunkX, chunkZ);
            if (!this.usedChunks.containsKey(centerChunkIndex) || !this.usedChunks.get(centerChunkIndex)) {
                if (this.teleportChunkLoaded) {
                    this.lastImmobile = this.isImmobile();
                }

                this.teleportChunkIndex = centerChunkIndex;
                this.teleportChunkLoaded = false;
            }

            for (int X = -1; X <= 1; ++X) {
                for (int Z = -1; Z <= 1; ++Z) {
                    long index = Level.chunkHash(chunkX + X, chunkZ + Z);
                    if (!this.usedChunks.containsKey(index) || !this.usedChunks.get(index)) {
                        return false;
                    }
                }
            }

            this.spawnToAll();
            this.forceMovement = this.teleportPosition;
            this.teleportPosition = null;
            return true;
        }

        return false;
    }

    protected void sendPlayStatus(int status) {
        sendPlayStatus(status, false);
    }

    protected void sendPlayStatus(int status, boolean immediate) {
        PlayStatusPacket pk = new PlayStatusPacket();
        pk.status = status;

        this.dataPacket(pk);
    }

    @Override
    public boolean teleport(Location location, TeleportCause cause) {
        if (!this.isOnline()) {
            return false;
        }

        Location from = this.getLocation();
        Location to = location;

        if (cause != null) {
            PlayerTeleportEvent event = new PlayerTeleportEvent(this, from, to, cause);
            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
            to = event.getTo();
            if (from.getLevel().getId() != to.getLevel().getId()) { //Different level, update compass position
                SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
                pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
                Position spawn = to.getLevel().getSpawnLocation();
                pk.x = spawn.getFloorX();
                pk.y = spawn.getFloorY();
                pk.z = spawn.getFloorZ();
                dataPacket(pk);
            }
        }

        //TODO Remove it! A hack to solve the client-side teleporting bug! (inside into the block)
        if (super.teleport(to.getY() == to.getFloorY() ? to.add(0, 0.00001, 0) : to, null)) { // null to prevent fire of duplicate EntityTeleportEvent
            this.removeAllWindows();

            this.teleportPosition = new Vector3(this.x, this.y, this.z);
            this.forceMovement = this.teleportPosition;
            this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_TELEPORT);

            this.checkTeleportPosition();

            this.resetFallDistance();
            this.nextChunkOrderRun = 0;
            this.newPosition = null;

            //DummyBossBar
            this.getDummyBossBars().values().forEach(DummyBossBar::reshow);
            //Weather
            this.getLevel().sendWeather(this);
            //Update time
            this.getLevel().sendTime(this);
            return true;
        }

        return false;
    }

    protected void forceSendEmptyChunks() {
        this.forceSendEmptyChunks(this.chunkRadius);
    }

    protected void forceSendEmptyChunks(int chunkRadius) {
        int chunkPositionX = this.getFloorX() >> 4;
        int chunkPositionZ = this.getFloorZ() >> 4;
        List<FullChunkDataPacket> pkList = new ObjectArrayList<>();
        for (int x = -chunkRadius; x < chunkRadius; x++) {
            for (int z = -chunkRadius; z < chunkRadius; z++) {
                FullChunkDataPacket chunk = new FullChunkDataPacket();
                chunk.chunkX = chunkPositionX + x;
                chunk.chunkZ = chunkPositionZ + z;
                chunk.data = new byte[0];
                pkList.add(chunk);
            }
        }
        //Server.getInstance().batchPackets(new Player[]{this}, pkList.stream().toArray(DataPacket[]::new), true);
        pkList.forEach(this::dataPacket);
    }

    public void teleportImmediate(Location location) {
        this.teleportImmediate(location, TeleportCause.PLUGIN);
    }

    public void teleportImmediate(Location location, TeleportCause cause) {
        Location from = this.getLocation();
        if (super.teleport(location, cause)) {

            for (Inventory window : new ObjectArrayList<>(this.windowIndex.values())) {
                if (window == this.inventory) {
                    continue;
                }
                this.removeWindow(window);
            }

            if (from.getLevel().getId() != location.getLevel().getId()) { //Different level, update compass position
                SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
                pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
                Position spawn = location.getLevel().getSpawnLocation();
                pk.x = spawn.getFloorX();
                pk.y = spawn.getFloorY();
                pk.z = spawn.getFloorZ();
                dataPacket(pk);
            }

            this.forceMovement = new Vector3(this.x, this.y, this.z);
            this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);

            this.resetFallDistance();
            this.orderChunks();
            this.nextChunkOrderRun = 0;
            this.newPosition = null;

            //Weather
            this.getLevel().sendWeather(this);
            //Update time
            this.getLevel().sendTime(this);
        }
    }

    /**
     * Shows a new FormWindow to the player
     * You can find out FormWindow result by listening to PlayerFormRespondedEvent
     *
     * @param window to show
     * @return form id to use in {@link PlayerFormRespondedEvent}
     */
    public int showFormWindow(FormWindow window) {
        int id = this.formWindowCount++;

        ModalFormRequestPacket packet = new ModalFormRequestPacket();
        packet.formId = id;
        packet.data = window.getJSONData();
        this.formWindows.put(packet.formId, window);

        this.dataPacket(packet);
        return id;
    }

    /**
     * Shows a new setting page in game settings
     * You can find out settings result by listening to PlayerFormRespondedEvent
     *
     * @param window to show on settings page
     * @return form id to use in {@link PlayerFormRespondedEvent}
     */
    public int addServerSettings(FormWindow window) {
        int id = this.formWindowCount++;

        this.serverSettings.put(id, window);
        return id;
    }

    /**
     * Creates and sends a BossBar to the player
     *
     * @param text   The BossBar message
     * @param length The BossBar percentage
     * @return bossBarId  The BossBar ID, you should store it if you want to remove or update the BossBar later
     */
    @Deprecated
    public long createBossBar(String text, int length) {
        DummyBossBar bossBar = new DummyBossBar.Builder(this).text(text).length(length).build();
        return this.createBossBar(bossBar);
    }

    /**
     * Creates and sends a BossBar to the player
     *
     * @param dummyBossBar DummyBossBar Object (Instantiate it by the Class Builder)
     * @return bossBarId  The BossBar ID, you should store it if you want to remove or update the BossBar later
     * @see DummyBossBar.Builder
     */
    public long createBossBar(DummyBossBar dummyBossBar) {
        this.dummyBossBars.put(dummyBossBar.getBossBarId(), dummyBossBar);
        dummyBossBar.create();
        return dummyBossBar.getBossBarId();
    }

    /**
     * Get a DummyBossBar object
     *
     * @param bossBarId The BossBar ID
     * @return DummyBossBar object
     * @see DummyBossBar#setText(String) Set BossBar text
     * @see DummyBossBar#setLength(float) Set BossBar length
     * @see DummyBossBar#setColor(BossEventPacket.BossBarColor) Set BossBar color
     */
    public DummyBossBar getDummyBossBar(long bossBarId) {
        return this.dummyBossBars.getOrDefault(bossBarId, null);
    }

    /**
     * Get all DummyBossBar objects
     *
     * @return DummyBossBars Map
     */
    public Long2ObjectMap<DummyBossBar> getDummyBossBars() {
        return dummyBossBars;
    }

    /**
     * Updates a BossBar
     *
     * @param text      The new BossBar message
     * @param length    The new BossBar length
     * @param bossBarId The BossBar ID
     */
    @Deprecated
    public void updateBossBar(String text, int length, long bossBarId) {
        if (this.dummyBossBars.containsKey(bossBarId)) {
            DummyBossBar bossBar = this.dummyBossBars.get(bossBarId);
            bossBar.setText(text);
            bossBar.setLength(length);
        }
    }

    /**
     * Removes a BossBar
     *
     * @param bossBarId The BossBar ID
     */
    public void removeBossBar(long bossBarId) {
        if (this.dummyBossBars.containsKey(bossBarId)) {
            this.dummyBossBars.get(bossBarId).destroy();
            this.dummyBossBars.remove(bossBarId);
        }
    }

    public int getWindowId(Inventory inventory) {
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }

        return -1;
    }

    public Inventory getWindowById(int id) {
        return this.windowIndex.get(id);
    }

    public int addWindow(Inventory inventory) {
        return this.addWindow(inventory, null);
    }

    public int addWindow(Inventory inventory, Integer forceId) {
        return addWindow(inventory, forceId, false);
    }

    public int addWindow(Inventory inventory, Integer forceId, boolean isPermanent) {
        return addWindow(inventory, forceId, isPermanent, false);
    }

    public int addWindow(Inventory inventory, Integer forceId, boolean isPermanent, boolean alwaysOpen) {
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }

        int cnt;
        if (forceId == null) {
            this.windowCnt = cnt = Math.max(FIRST_AVAILABLE_WINDOW_ID, ++this.windowCnt % 99);
        } else {
            cnt = forceId;
        }
        this.windowIndex.put(cnt, inventory);
        this.windows.put(inventory, cnt);

        if (isPermanent) {
            this.permanentWindows.add(cnt);
        }

        if (inventory.open(this)) {
            return cnt;
        } else {
            this.removeWindow(inventory);

            return -1;
        }
    }

    public Optional<Inventory> getTopWindow() {
        return Optional.ofNullable(this.windowIndex.get(windowCnt));
    }

    public void removeWindow(Inventory inventory) {
        inventory.close(this);
        if (this.windows.containsKey(inventory)) {
            int id = this.windows.get(inventory);
            this.windows.remove(this.windowIndex.get(id));
            this.windowIndex.remove(id);
        }
    }

    public void sendAllInventories() {
        for (Inventory inv : this.windowIndex.values()) {
            inv.sendContents(this);

            if (inv instanceof PlayerInventory) {
                ((PlayerInventory) inv).sendArmorContents(this);
            }
        }
    }

    protected void addDefaultWindows() {
        this.addWindow(this.getInventory(), ContainerIds.INVENTORY, true, true);

        this.playerUIInventory = new PlayerUIInventory(this);
        this.addWindow(this.playerUIInventory, ContainerIds.UI, true);
        this.addWindow(this.offhandInventory, ContainerIds.OFFHAND, true, true);

        this.craftingGrid = this.playerUIInventory.getCraftingGrid();
        this.addWindow(this.craftingGrid, ContainerIds.NONE);
    }

    public PlayerUIInventory getUIInventory() {
        return playerUIInventory;
    }

    public PlayerCursorInventory getCursorInventory() {
        return this.playerUIInventory.getCursorInventory();
    }

    public CraftingGrid getCraftingGrid() {
        return this.craftingGrid;
    }

    public void setCraftingGrid(CraftingGrid grid) {
        this.craftingGrid = grid;
//        if (grid instanceof BigCraftingGrid) {
//            this.addWindow(grid, WORKBENCH_WINDOW_ID);
//        }
        this.addWindow(grid, ContainerIds.NONE);
    }

    public void resetCraftingGridType() {
        if (this.craftingGrid != null) {
            Item[] drops = this.inventory.addItem(this.craftingGrid.getContents().values().toArray(new Item[0]));

            if (drops.length > 0) {
                for (Item drop : drops) {
                    this.dropItem(drop);
                }
            }

            drops = this.inventory.addItem(this.getCursorInventory().getItem(0));
            if (drops.length > 0) {
                for (Item drop : drops) {
                    this.dropItem(drop);
                }
            }

            this.playerUIInventory.clearAll();

            if (this.craftingGrid instanceof BigCraftingGrid) {
                this.craftingGrid = this.playerUIInventory.getCraftingGrid();
                this.addWindow(this.craftingGrid, ContainerIds.NONE);
//
//                ContainerClosePacket pk = new ContainerClosePacket(); //be sure, big crafting is really closed
//                pk.windowId = ContainerIds.NONE;
//                this.dataPacket(pk);
            }

            this.craftingType = CRAFTING_SMALL;
        }
    }

    public PlayerOffhandInventory getOffhandInventory() {
        return offhandInventory;
    }

    public void removeAllWindows() {
        removeAllWindows(false);
    }

    public void removeAllWindows(boolean permanent) {
        for (Entry<Integer, Inventory> entry : new ObjectArrayList<>(this.windowIndex.entrySet())) {
            if (!permanent && this.permanentWindows.contains(entry.getKey())) {
                continue;
            }
            this.removeWindow(entry.getValue());
        }
    }

    public int getClosingWindowId() {
        return this.closingWindowId;
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        this.server.getPlayerMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return this.server.getPlayerMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return this.server.getPlayerMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        this.server.getPlayerMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    @Override
    public void onChunkChanged(FullChunk chunk) {
        this.usedChunks.remove(Level.chunkHash(chunk.getX(), chunk.getZ()));
    }

    @Override
    public void onChunkLoaded(FullChunk chunk) {

    }

    @Override
    public void onChunkPopulated(FullChunk chunk) {

    }

    @Override
    public void onChunkUnloaded(FullChunk chunk) {

    }

    @Override
    public void onBlockChanged(Vector3 block) {

    }

    @Override
    public Integer getLoaderId() {
        return this.loaderId;
    }

    @Override
    public boolean isLoaderActive() {
        return this.isConnected();
    }

    /**
     * @deprecated Use {@link Level#getChunkCacheFromData}.
     */
    @Deprecated
    public static BatchPacket getChunkCacheFromData(int chunkX, int chunkZ, byte[] payload) {
        return getChunkCacheFromData(chunkX, chunkZ, payload, false);
    }

    /**
     * @deprecated Use {@link Level#getChunkCacheFromData}.
     */
    @Deprecated
    public static BatchPacket getChunkCacheFromData(int chunkX, int chunkZ, byte[] payload, boolean zlibRaw) {
        FullChunkDataPacket pk = new FullChunkDataPacket();
        pk.chunkX = chunkX;
        pk.chunkZ = chunkZ;
        pk.data = payload;
        pk.tryEncode();

        BatchPacket batch = new BatchPacket();
        byte[][] batchPayload = new byte[2][];
        byte[] buf = pk.getBuffer();
        batchPayload[0] = Binary.writeUnsignedVarInt(buf.length);
        batchPayload[1] = buf;
        byte[] data = Binary.appendBytes(batchPayload);
        try {
            batch.payload = zlibRaw ? Network.deflateRaw(data, Server.getInstance().networkCompressionLevel) : Zlib.deflate(data, Server.getInstance().networkCompressionLevel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return batch;
    }

    private boolean foodEnabled = true;

    public boolean isFoodEnabled() {
        return !(this.isCreative() || this.isSpectator()) && this.foodEnabled;
    }

    public void setFoodEnabled(boolean foodEnabled) {
        this.foodEnabled = foodEnabled;
    }

    public PlayerFood getFoodData() {
        return this.foodData;
    }

    public void setDimension(int dimension) {
        ChangeDimensionPacket pk = new ChangeDimensionPacket();
        pk.dimension = getLevel().getDimension().ordinal();
        this.dataPacket(pk);
    }

    public void setCheckMovement(boolean checkMovement) {
        this.checkMovement = checkMovement;
    }

    public synchronized void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    public synchronized Locale getLocale() {
        return this.locale.get();
    }

    @Override
    public void setSprinting(boolean value) {
        if (isSprinting() != value) {
            super.setSprinting(value);
            this.setMovementSpeed(value ? getMovementSpeed() * 1.3f : getMovementSpeed() / 1.3f);
        }
    }

    public void transfer(InetSocketAddress address) {
        String hostName = address.getAddress().getHostAddress();
        int port = address.getPort();
        TransferPacket pk = new TransferPacket();
        pk.address = hostName;
        pk.port = port;
        this.dataPacket(pk);
    }

    public LoginChainData getLoginChainData() {
        return this.loginChainData;
    }

    public boolean pickupEntity(Entity entity, boolean near) {
        if (!this.spawned || !this.isAlive() || !this.isOnline() || this.isSpectator() || entity.isClosed()) {
            return false;
        }

        if (near) {
            if (entity instanceof EntityArrow && ((EntityArrow) entity).hadCollision && entity.ticksLived > 5) {
                ItemArrow item = new ItemArrow();
                if (!this.isCreative() && !this.inventory.canAddItem(item)) {
                    return false;
                }

                InventoryPickupArrowEvent ev = new InventoryPickupArrowEvent(this.inventory, (EntityArrow) entity);

                int pickupMode = ((EntityArrow) entity).getPickupMode();
                if (pickupMode == EntityArrow.PICKUP_NONE || pickupMode == EntityArrow.PICKUP_CREATIVE && !this.isCreative()) {
                    ev.setCancelled();
                }

                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return false;
                }

                TakeItemEntityPacket pk = new TakeItemEntityPacket();
                pk.entityId = this.getId();
                pk.target = entity.getId();
                Server.broadcastPacket(entity.getViewers().values(), pk);
                this.dataPacket(pk);

                if (!this.isCreative()) {
                    this.inventory.addItem(item.clone());
                }
                entity.close();
                return true;
            } else if (entity instanceof EntityThrownTrident && ((EntityThrownTrident) entity).hadCollision) {
                Item item = ((EntityThrownTrident) entity).getItem();
                if (!this.isCreative() && !this.inventory.canAddItem(item)) {
                    return false;
                }

                InventoryPickupTridentEvent ev = new InventoryPickupTridentEvent(this.inventory, (EntityThrownTrident) entity);

                int pickupMode = ((EntityThrownTrident) entity).getPickupMode();
                if (pickupMode == EntityThrownTrident.PICKUP_NONE || (pickupMode == EntityThrownTrident.PICKUP_CREATIVE && !this.isCreative())) {
                    ev.setCancelled();
                }

                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return false;
                }

                TakeItemEntityPacket pk = new TakeItemEntityPacket();
                pk.entityId = this.getId();
                pk.target = entity.getId();
                Server.broadcastPacket(entity.getViewers().values(), pk);
                this.dataPacket(pk);

                if (!this.isCreative()) {
                    this.inventory.addItem(item.clone());
                }
                entity.close();
                return true;
            } else if (entity instanceof EntityItem) {
                if (((EntityItem) entity).getPickupDelay() <= 0) {
                    Item item = ((EntityItem) entity).getItem();

                    if (item != null) {
                        if (!this.isCreative() && !this.inventory.canAddItem(item)) {
                            return false;
                        }

                        InventoryPickupItemEvent ev;
                        this.server.getPluginManager().callEvent(ev = new InventoryPickupItemEvent(this.inventory, (EntityItem) entity));
                        if (ev.isCancelled()) {
                            return false;
                        }

                        /*switch (item.getId()) {
                            case Item.LOG:
                            case Item.LOG2:
                                this.awardAchievement("mineWood");
                                break;
                            case Item.DIAMOND:
                                this.awardAchievement("diamond");
                                break;
                        }*/

                        TakeItemEntityPacket pk = new TakeItemEntityPacket();
                        pk.entityId = this.getId();
                        pk.target = entity.getId();
                        Server.broadcastPacket(entity.getViewers().values(), pk);
                        this.dataPacket(pk);

                        this.inventory.addItem(item.clone());
                        entity.close();
                        return true;
                    }
                }
            }
        }

        int tick = this.getServer().getTick();
        if (pickedXPOrb < tick && entity instanceof EntityXPOrb && this.boundingBox.isVectorInside(entity)) {
            EntityXPOrb xpOrb = (EntityXPOrb) entity;
            if (xpOrb.getPickupDelay() <= 0) {
                int exp = xpOrb.getExp();
                entity.kill();
                this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_EXPERIENCE_ORB);
                pickedXPOrb = tick;
                this.addExperience(exp);
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        if ((this.hash == 0) || (this.hash == 485)) {
            this.hash = (485 + (getUniqueId() != null ? getUniqueId().hashCode() : 0));
        }

        return this.hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player)) {
            return false;
        }
        Player other = (Player) obj;
        return Objects.equals(this.getUniqueId(), other.getUniqueId()) && this.getId() == other.getId();
    }

    public boolean isBreakingBlock() {
        return this.breakingBlock != null;
    }

    /**
     * Show a window of a XBOX account's profile
     * @param xuid XUID
     */
    public void showXboxProfile(String xuid) {
        ShowProfilePacket pk = new ShowProfilePacket();
        pk.xuid = xuid;
        this.dataPacket(pk);
    }

    public Map<String, ResourcePack> getResourcePacks() {
        return this.resourcePacks;
    }

    public Map<String, ResourcePack> getBehaviourPacks() {
        return this.behaviourPacks;
    }

    public boolean isForceResources() {
        return this.forceResources;
    }

    /**
     * Start fishing
     * @param fishingRod fishing rod item
     */
    public void startFishing(Item fishingRod) {
        Vector3 motion = this.getDirectionVector().multiply(1.2);
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", x))
                        .add(new DoubleTag("", y + this.getEyeHeight()))
                        .add(new DoubleTag("", z)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", motion.x))
                        .add(new DoubleTag("", motion.y))
                        .add(new DoubleTag("", motion.z)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) yaw))
                        .add(new FloatTag("", (float) pitch)));
        EntityFishingHook fishingHook = new EntityFishingHook(chunk, nbt, this);
        fishingHook.setMotion(motion);
        ProjectileLaunchEvent ev = new ProjectileLaunchEvent(fishingHook);
        this.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            fishingHook.close();
        } else {
            this.fishing = fishingHook;
            fishingHook.rod = fishingRod;
            fishingHook.checkLure();
            fishingHook.spawnToAll();
            this.level.addSound(new LaunchSound(this));
        }
    }

    /**
     * Stop fishing
     * @param click clicked or forced
     */
    public void stopFishing(boolean click) {
        if (this.fishing != null && click) {
            fishing.reelLine();
        } else if (this.fishing != null) {
            this.fishing.close();
        }

        this.fishing = null;
    }

    @Override
    public boolean doesTriggerPressurePlate() {
        return this.gamemode != SPECTATOR;
    }

    @Override
    public String toString() {
        return "Player(name='" + getName() +
                "', location=" + super.toString() +
                ')';
    }

    @Override
    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        this.sendPosition(new Vector3(x, y, z), yaw, pitch, MovePlayerPacket.MODE_NORMAL, this.getViewers().values().toArray(new Player[0]));
    }

    public List<Item> getCreativeItems() {
        return Item.getCreativeItems();
    }

    public int getCreativeItemIndex(Item item) {
        List<Item> creative = this.getCreativeItems();
        for (int i = 0; i < creative.size(); i++) {
            if (item.equals(creative.get(i), !item.isTool())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void setImmobile(boolean value) {
        super.setImmobile(value);
        if (this.teleportChunkLoaded)
            this.lastImmobile = value;
    }

    /**
     * {@link PlayerInventory} 被打开时调用.
     * @since 1.16.0
     */
    public void onInventoryOpen() {
    }

    public void sendCreativeContents() {
        this.inventory.sendCreativeContents();
    }

    /**
     * 由于万恶的Hardcode，中国版玩家标识移至Player类.
     */
    public boolean isNetEaseClient() {
        return isNetEaseClient;
    }

    public void setIsNetEaseClient(boolean netEaseClient) {
        isNetEaseClient = netEaseClient;
    }

    /**
     * 使用 AnimateEntityPacket 播放动画.
     * @since 1.16.100
     */
    public void playAnimation(String animation, long entityRuntimeId) {
    }

    /**
     * 1.18及以上版本支持子区块请求.
     */
    public boolean isSubChunkRequestAvailable() {
        return false;
    }

    /**
     * 发送未缓存在内存的子区块.
     * @since 1.18.0
     */
    public void sendSubChunks(int dimension, int x, int z, int subChunkCount, ChunkBlobCache blobCache, Map<StaticVersion, byte[][]> payload, byte[] heightMapType, byte[][] heightMap) {
    }

    /**
     * 发送缓存在内存的子区块.
     * @since 1.18.0
     */
    public void sendSubChunks(int dimension, int x, int z, int subChunkCount, ChunkBlobCache blobCache, ChunkPacketCache packetCache, byte[] heightMapType, byte[][] heightMap) {
    }

    protected boolean hasSubChunkRequest() {
        return false;
    }

    protected void processSubChunkRequest() {
    }

    protected boolean onLevelSwitch() {
        return false;
    }

    protected void onDimensionChangeSuccess() {
    }

    /**
     * 使用 SpawnParticleEffectPacket 生成粒子效果.
     * @since 1.8.0
     */
    public void spawnParticleEffect(Vector3f position, String identifier) {
    }

    /**
     * 使用 SpawnParticleEffectPacket 生成粒子效果.
     * @since 1.8.0
     */
    public void spawnParticleEffect(Vector3f position, String identifier, long entityUniqueId) {
    }

    /**
     * 使用 SpawnParticleEffectPacket 生成粒子效果.
     * @since 1.8.0
     */
    public void spawnParticleEffect(Vector3f position, String identifier, long entityUniqueId, int dimension) {
    }

    /**
     * 使用 SpawnParticleEffectPacket 生成粒子效果.
     * @since 1.8.0
     */
    public void spawnParticleEffect(Vector3f position, String identifier, long entityUniqueId, int dimension, @Nullable String molangVariables) {
    }

    /**
     * @since 1.16.100
     */
    public void addCameraShake(float intensity, float duration, int type) {
    }

    /**
     * @since 1.16.100
     */
    public void stopCameraShake() {
    }

    /**
     * 仅提供客户端物品冷却的视觉效果, 没有实质上的功能限制.
     * @param duration ticks
     * @since 1.18.10
     */
    public void startItemCooldown(String itemCategory, int duration) {
    }

    /**
     * @since 1.19.0
     */
    public void sendToast(String title) {
        this.sendToast(title, "");
    }

    /**
     * @since 1.19.0
     */
    public void sendToast(String title, String content) {
    }

    /**
     * @since 1.4.0
     */
    public void sendJukeboxPopup(TranslationContainer message) {
    }

    @Override
    public boolean canBreathe() {
        if (isCreative() || isSpectator()) {
            return true;
        }
        return super.canBreathe();
    }

    /**
     * 1.18.0 后使用 UpdateSubChunkBlocksPacket 代替 UpdateBlockPacket 和 UpdateBlockSyncedPacket.
     * WIP
     */
    public void updateBlock(Block entry) {
        //TODO: queue
    }

    public void sendAdventureSettingsAndAbilities(Player player, AdventureSettings settings) {
        sendAdventureSettings(settings);
        sendAbilities(player, settings);
    }

    /**
     * 1.19.10 后使用 UpdateAbilitiesPacket 代替 AdventureSettingsPacket.
     */
    public void sendAbilities(Player player, AdventureSettings settings) {
        AdventureSettingsPacket packet = new AdventureSettingsPacket();
        for (Type type : Type.values()) {
            int id = type.getId();
            if (id == -1) {
                continue;
            }
            packet.setFlag(id, settings.get(type));
        }
        packet.commandPermission = player.isOp() ? AdventureSettingsPacket.PERMISSION_OPERATOR : AdventureSettingsPacket.PERMISSION_NORMAL;
        packet.playerPermission = player.isOp() ? Player.PERMISSION_OPERATOR : Player.PERMISSION_MEMBER;
        packet.entityUniqueId = player.getId();
        dataPacket(packet);
    }

    /**
     * 1.19.10 后发送 UpdateAdventureSettingsPacket.
     */
    public void sendAdventureSettings(AdventureSettings settings) {
    }

    public long getLocalEntityId() {
        return getId();
    }
}
