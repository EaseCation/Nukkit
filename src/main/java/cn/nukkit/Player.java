package cn.nukkit;

import cn.nukkit.AdventureSettings.Type;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDoor;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockNoteblock;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityEnderChest;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.entity.*;
import cn.nukkit.entity.attribute.Attribute;
import cn.nukkit.entity.attribute.AttributeModifiers;
import cn.nukkit.entity.data.*;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.entity.property.*;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDamageEvent.DamageModifier;
import cn.nukkit.event.inventory.*;
import cn.nukkit.event.level.LevelCorruptEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerKickEvent.Reason;
import cn.nukkit.event.player.PlayerSpawnChangeEvent.Cause;
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
import cn.nukkit.item.enchantment.EnchantmentOption;
import cn.nukkit.item.food.Food;
import cn.nukkit.lang.LiteralContainer;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.*;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.GlobalBlockPaletteInterface.StaticVersion;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.ChunkCachedData;
import cn.nukkit.level.particle.PunchBlockParticle;
import cn.nukkit.level.util.AroundChunkComparator;
import cn.nukkit.level.util.AroundPlayerChunkComparator;
import cn.nukkit.math.*;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.PacketViolationReason;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.AnimatePacket.SwingSource;
import cn.nukkit.network.protocol.types.*;
import cn.nukkit.network.protocol.types.CommandOriginData.Origin;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.EffectID;
import cn.nukkit.potion.Potion;
import cn.nukkit.resourcepacks.ResourcePack;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.*;
import com.google.common.annotations.Beta;
import com.google.common.collect.BiMap;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static cn.nukkit.SharedConstants.*;

/**
 * author: MagicDroidX &amp; Box
 * Nukkit Project
 */
@Log4j2
public class Player extends EntityHuman implements CommandSender, InventoryHolder, ChunkLoader, IPlayer {

    public static final int SURVIVAL = 0;
    public static final int CREATIVE = 1;
    public static final int ADVENTURE = 2;
    public static final int SPECTATOR = 3;
    public static final int VIEWER = SPECTATOR;

    public static final int MAX_REACH_DISTANCE_CREATIVE = 13 + 1;
    public static final int MAX_REACH_DISTANCE_SURVIVAL = 7 + 1;
    public static final int MAX_REACH_DISTANCE_ENTITY_INTERACTION = 8;

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

    public static final int MAX_VIEW_DISTANCE = 96;

    /**
     * Prevent the player inside into unloaded chunks.
     */
    public static final int TELEPORT_TEMP_Y = Short.MAX_VALUE;

    public static final int VIOLATION_THRESHOLD = 150;
    public static final int VIOLATION_KICK_THRESHOLD = 100;

    private static PlayerViolationListener VIOLATION_LISTENER = PlayerViolationListener.NOPE;

    protected final SourceInterface interfaz;

    public boolean playedBefore;
    public boolean spawned = false;
    public boolean loggedIn = false;
    public boolean locallyInitialized = false;
    public int gamemode;
    public long lastBreak;
    protected BlockVector3 lastBreakPosition = new BlockVector3();

    protected int windowCnt = FIRST_AVAILABLE_WINDOW_ID;

    protected int closingWindowId = Integer.MIN_VALUE;

    protected final BiMap<Inventory, Integer> windows = HashBiMap.create();
    protected final BiMap<Integer, Inventory> windowIndex = windows.inverse();
    protected IntSet permanentWindows = new IntOpenHashSet();
    protected int messageCounter = 2;

    public Vector3 speed = null;
    public int attackCriticalThisJump = 0;

    public int craftingType = CRAFTING_SMALL;
    public RecipeTag recipeTag = RecipeTag.CRAFTING_TABLE;

    protected PlayerUIInventory playerUIInventory;
    protected CraftingGrid craftingGrid;
    protected CraftingTransaction craftingTransaction;
    protected EnchantTransaction enchantTransaction;
    protected RepairItemTransaction repairItemTransaction;

    protected int enchantmentSeed;

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

    private final int loaderId;

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

    protected float walkSpeed = 0.1f;
    protected float flySpeed = 0.05f;
    protected float verticalFlySpeed = 1;

    protected boolean checkMovement = true;

    private PermissibleBase perm;

    private int exp = 0;
    private int expLevel = 0;

    protected PlayerFood foodData = null;

    private Entity killer = null;

    private Locale locale;

    private String buttonText = "";

    protected boolean enableClientCommand = true;

    private Vector3 viewingEnderChest = null;

    //TODO: use itemCooldownMap
    protected int lastEnderPearl = 20;
    protected int lastChorusFruitTeleport = 20;
    protected int lastIceBomb = 20;
    protected int lastGoatHornPlay = 140;

    private LoginChainData loginChainData;

    public Block breakingBlock = null;

    public int pickedXPOrb = 0;

    protected int protocol;
    @Nullable
    protected StaticVersion blockVersion;

    protected int formWindowCount = 0;
    protected Int2ObjectMap<FormWindow> formWindows = new Int2ObjectOpenHashMap<>();
    protected Int2ObjectMap<FormWindow> serverSettings = new Int2ObjectOpenHashMap<>();

    protected Long2ObjectMap<DummyBossBar> dummyBossBars = new Long2ObjectOpenHashMap<>();

    protected Map<String, ResourcePack> resourcePacks = new Object2ObjectLinkedOpenHashMap<>();
    protected Map<String, ResourcePack> behaviourPacks = new Object2ObjectLinkedOpenHashMap<>();
    protected boolean forceResources = false;

    private final Queue<ObjectIntPair<ResourcePack>> resourcePackChunkRequests = new ArrayDeque<>();

    @Nullable
    protected CompoundTag offlinePlayerData;
    protected AsyncTask preLoginEventTask = null;
    protected boolean shouldLogin = false;

    public EntityFishingHook fishing = null;

    public long lastSkinChange;

    protected long lastRightClickTime;
    @Nullable
    protected UseItemData lastRightClickData;

    protected boolean isNetEaseClient = false;

    // EC：确保在一个服务器中，每个玩家的皮肤只发一遍
    public final List<UUID> sentSkins = new ObjectArrayList<>();

    protected Entity lookAtEntity;

    private int violation;
    private volatile boolean violated;
    private final Queue<PlayerViolationRecord> violationRecords = EvictingQueue.create(32);
    private volatile PlayerViolationRecord asyncViolationRecord;

    protected boolean inWater = false;
    protected boolean underwater;

    protected boolean swinging;
    protected int swingTime;

    public int damageNearbyMobsTick;

    protected int shieldBlockingTick;
    protected int prevShieldBlockingTick;
    protected int shieldCooldown;

    public int brushCooldown;

    @Beta //TODO: AttributeMap
    protected Attribute movementSpeedAttribute = Attribute.getAttribute(Attribute.MOVEMENT);

    protected boolean hiddenLocator;
    protected final Vector3f locatorPos = new Vector3f(Float.NaN, Float.NaN, Float.NaN);

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

    public int getLastIceBombThrowingTick() {
        return lastIceBomb;
    }

    public void onThrowIceBomb() {
        this.lastIceBomb = this.server.getTick();
    }

    public int getLastGoatHornPlay() {
        return lastGoatHornPlay;
    }

    public void onGoatHornPlay() {
        this.lastGoatHornPlay = this.server.getTick();
    }

    public Vector3 getViewingEnderChest() {
        return viewingEnderChest;
    }

    public void setViewingEnderChest(Vector3 chest) {
        if (chest == null && this.viewingEnderChest != null) {
            if (viewingEnderChest instanceof BlockEntityEnderChest viewing) {
                viewing.getViewers().remove(this);
            }
        } else if (chest instanceof BlockEntityEnderChest enderChest) {
            enderChest.getViewers().add(this);
        }
        this.viewingEnderChest = chest;
    }

    public TranslationContainer getLeaveMessage() {
        return new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.left", this.getDisplayName());
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

    public UUID getSessionId() {
        return getUniqueId();
    }

    @Override
    public boolean isBanned() {
        return this.server.getNameBans().isBanned(this.getName());
    }

    @Override
    public void setBanned(boolean value) {
        if (value) {
            this.server.getNameBans().addBan(this.getName(), null, null, null);
            this.kick(PlayerKickEvent.Reason.NAME_BANNED, "Banned by admin", false);
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
        if (!value) {
            this.getAdventureSettings().set(Type.FLYING, false);
        }
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

    public float getWalkSpeed() {
        return walkSpeed;
    }

    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    public float getFlySpeed() {
        return flySpeed;
    }

    public void setFlySpeed(float flySpeed) {
        this.flySpeed = flySpeed;
    }

    public float getVerticalFlySpeed() {
        return verticalFlySpeed;
    }

    public void setVerticalFlySpeed(float verticalFlySpeed) {
        this.verticalFlySpeed = verticalFlySpeed;
    }

    @Override
    public void spawnTo(Player player) {
        if (this.spawned && player.spawned && this.isAlive() && player.isAlive() && player.getLevel() == this.level && player.canSee(this) && !this.isSpectator()) {
            super.spawnTo(player);
        }
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
        if (this.isSurvivalLike() && entity instanceof EntityFishingHook) return true;
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
        if (this.perm == null) {
            return false;
        }
        return this.perm.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        if (this.perm == null) {
            return false;
        }
        return this.perm.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String name) {
        return this.perm != null && this.perm.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        if (this.perm == null) {
            return false;
        }
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
        if (this.perm == null) {
            return null;
        }
        return this.perm.addAttachment(plugin, name, value);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        if (this.perm == null) {
            return;
        }
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
        if (this.perm == null) {
            return Collections.emptyMap();
        }
        return this.perm.getEffectivePermissions();
    }

    public Player(SourceInterface interfaz, Long clientID, InetSocketAddress socketAddress) {
        super(null, new CompoundTag());
        this.interfaz = interfaz;
        this.perm = new PermissibleBase(this);
        this.server = Server.getInstance();
        this.lastBreak = -1;
        this.socketAddress = socketAddress;
        this.clientID = clientID;
        this.loaderId = Level.generateChunkLoaderId();
        this.chunksPerTick = this.server.getConfiguration().getChunkSendingPerTick();
        this.spawnThreshold = this.server.getConfiguration().getChunkSpawnThreshold();
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

    @Override
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
        return this.getDataFlag(DATA_FLAG_ACTION) && this.startAction > -1;
    }

    public void setUsingItem(boolean value) {
        this.startAction = value ? this.server.getTick() : -1;
        this.startActionTimestamp = value ? System.currentTimeMillis() : -1;
        this.setDataFlag(DATA_FLAG_ACTION, value);
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

            targetLevel.onPlayerAdd(this);

            this.usedChunks = new Long2BooleanOpenHashMap();

            if (this.onLevelSwitch(oldLevel, targetLevel)) {
                return true;
            }

            SetSpawnPositionPacket spawnPosition = new SetSpawnPositionPacket();
            spawnPosition.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
            Position spawn = level.getSpawnLocation(Position::new);
            spawnPosition.x = spawn.getFloorX();
            spawnPosition.y = spawn.getFloorY();
            spawnPosition.z = spawn.getFloorZ();
            this.dataPacket(spawnPosition);

            //forceSendEmptyChunks();

            level.sendTime(this);

            level.sendDifficulty(this);

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
        dummyBossBars.values().forEach(DummyBossBar::destroy);

        for (long index : new LongArrayList(this.usedChunks.keySet())) {
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
                this.setSpawnBlockPosition(null, Cause.RESET);
            }
        }

        if (this.spawnPosition != null && this.spawnPosition.getLevel() != null) {
            return this.spawnPosition.floor().add(0.5, 0.1, 0.5);
        } else {
            return this.server.getDefaultLevel().getSafeSpawn().floor().add(0.5, 0.1, 0.5);
        }
    }

    public void sendChunk(int dimension, int x, int z, int subChunkCount, ChunkCachedData cachedData, DataPacket packet) {
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

    public void sendChunk(int dimension, int x, int z, int subChunkCount, ChunkCachedData cachedData, byte[] payload, byte[] subModePayload) {
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

        this.sendQueuedChunk();

        if ((this.canDoFirstSpawn() || System.currentTimeMillis() - this.creationTime > 15000) && !this.spawned && this.teleportPosition == null) {
            this.doFirstSpawn();
        }
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
                this.sendMessage(TextFormat.RED + "Chunk " + chunkX + "," + chunkZ + " load&send failed: " + e.getMessage());
                if (e.getCause() != null) {
                    this.sendMessage(Utils.getExceptionMessage(e.getCause()));
                }
                log.warn("Chunk " + chunkX + "," + chunkZ + " load&send failed!", e);
                new LevelCorruptEvent(level, new ChunkPosition(chunkX, chunkZ)).call();
            }

            this.loadQueue.remove(index);

            PlayerChunkRequestEvent ev = new PlayerChunkRequestEvent(this, chunkX, chunkZ);
            this.server.getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                this.level.requestChunk(chunkX, chunkZ, this, getDummyDimension());
                success = true;
            }
        }

        return success;
    }

    protected void sendRecipeList() {
/*
        this.dataPacket(CraftingManager.packet);
*/
    }

    protected void firstRespawn(Position pos) {
        RespawnPacket respawnPacket = new RespawnPacket();
        respawnPacket.x = (float) pos.x;
        respawnPacket.y = (float) pos.y + this.getBaseOffset();
        respawnPacket.z = (float) pos.z;
        this.dataPacket(respawnPacket);
    }

    protected void doFirstSpawn() {
        this.locallyInitialized = true;  // 在1.6及以上版本，doFirstSpawn 后，收到客户端的 SetLocalPositionAndLookPacket 后，会设置locallyInitialized为true。
        this.sendPotionEffects(this);
        this.sendData(this);

        this.level.sendTime(this);

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
                new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.joined", this.getDisplayName())
        );

        this.server.getPluginManager().callEvent(playerJoinEvent);

        this.spawned = true;

        if (!playerJoinEvent.getJoinMessage().toString().trim().isEmpty()) {
            this.server.broadcastMessage(playerJoinEvent.getJoinMessage());
        }

        this.inventory.sendContents(this);
        this.armorInventory.sendContents(this);
        this.offhandInventory.sendContents(this);

        this.noDamageTicks = 60;

        this.sendCreativeContents();
        this.sendRecipeList();

        if (this.getHealth() < 1) {
            this.respawn();
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

        DataPacketSendEvent ev = new DataPacketSendEvent(this, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }

        this.interfaz.putPacket(this, packet, false, true);
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
                if (((Player) p).sleeping != null && pos.distanceSquared(((Player) p).sleeping) <= 0.1 * 0.1) {
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

        this.setDataProperty(new Vector3fEntityData(DATA_ENTER_BED_POSITION, asVector3f()));
        this.recalculateBoundingBox();
        this.setDataProperty(new IntPositionEntityData(DATA_PLAYER_BED_POSITION, (int) pos.x, (int) pos.y, (int) pos.z));
        this.setPlayerFlag(DATA_PLAYER_FLAG_SLEEP, true);

        this.setSpawn(pos);

        this.level.sleepTicks = 100;

        return true;
    }

    public void setSpawn(@Nullable Vector3 pos) {
        if (pos == null) {
            this.spawnPosition = null;
            return;
        }

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

    public void setSpawnBlockPosition(@Nullable Position pos) {
        setSpawnBlockPosition(pos, Cause.PLUGIN);
    }

    public void setSpawnBlockPosition(@Nullable Position pos, Cause cause) {
        PlayerSpawnChangeEvent event = new PlayerSpawnChangeEvent(this, pos, cause);
        event.call();
        if (event.isCancelled()) {
            return;
        }
        pos = event.getNewSpawn();

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
            this.setPlayerFlag(DATA_PLAYER_FLAG_SLEEP, false);
            this.recalculateBoundingBox();

            this.level.sleepTicks = 0;

            AnimatePacket pk = new AnimatePacket();
            pk.eid = this.id;
            pk.action = AnimatePacket.Action.WAKE_UP;
            this.dataPacket(pk);
        }
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
            newSettings.set(Type.INSTABUILD, gamemode == CREATIVE || gamemode == SPECTATOR);
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
            if (fishing != null) {
                stopFishing(false);
            }

            this.teleport(this, null);

            this.setDataFlag(DATA_FLAG_SILENT, true);
            this.setDataFlag(DATA_FLAG_HAS_COLLISION, false);
            this.setDataFlag(DATA_FLAG_PUSH_TOWARDS_CLOSEST_SPACE, false);
        } else {
            this.setDataFlag(DATA_FLAG_SILENT, false);
            this.setDataFlag(DATA_FLAG_HAS_COLLISION, true);
            this.setDataFlag(DATA_FLAG_PUSH_TOWARDS_CLOSEST_SPACE, true);
        }

        this.resetFallDistance();

        if (this.inventory != null) {
            this.inventory.sendContents(this);
            this.inventory.sendHeldItem(this.hasSpawned.values());

//            this.sendCreativeContents();
        }

        return true;
    }

    public boolean isSurvival() {
        return (this.gamemode & 0x01) == 0;
    }

    /**
     * @return survival or adventure
     */
    public boolean isSurvivalLike() {
        return (this.gamemode & 0x01) == 0;
    }

    public boolean isCreative() {
        return (this.gamemode & 0x01) > 0;
    }

    /**
     * @return creative or spectator
     */
    public boolean isCreativeLike() {
        return (this.gamemode & 0x01) == 1;
    }

    public boolean isSpectator() {
        return this.gamemode == 3;
    }

    public boolean isAdventure() {
        return (this.gamemode & 0x02) > 0;
    }

    public boolean canDestroy(Block block, Item item) {
        AdventureSettings settings;
        return switch (getGamemode()) {
            case Player.SURVIVAL -> (settings = getAdventureSettings()).get(Type.MINE)
                    && (!settings.get(Type.WORLD_IMMUTABLE) || settings.get(Type.PRIVILEGED_BUILDER));
            case Player.ADVENTURE -> getAdventureSettings().get(Type.MINE) && item.canDestroy(block);
            case Player.SPECTATOR -> false;
            default -> true;
        };
    }

    public boolean canPlaceOn(Block block, Item item) {
        AdventureSettings settings;
        return switch (getGamemode()) {
            case Player.SURVIVAL -> (settings = getAdventureSettings()).get(Type.BUILD)
                    && (!settings.get(Type.WORLD_IMMUTABLE) || settings.get(Type.PRIVILEGED_BUILDER));
            case Player.ADVENTURE -> getAdventureSettings().get(Type.BUILD) && item.canPlaceOn(block);
            case Player.SPECTATOR -> false;
            default -> true;
        };
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

        AxisAlignedBB newBB = this.boundingBox.getOffsetBoundingBox(dx, dy, dz);

        if (this.isSpectator() || server.getAllowFlight() || !this.level.hasCollision(this, newBB, false)) {
            this.boundingBox = newBB;
        }

        this.x = (this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2;
        this.y = this.boundingBox.getMinY() - this.ySize;
        this.z = (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2;

        this.checkChunks();

        if (!this.isSpectator()) {
            boolean inWaterPrev = this.inWater;
            boolean wasUnderwater = this.underwater;
            this.inWater = isInsideOfWater(0.7f);
            this.underwater = isInsideOfWater(true);
            boolean swimming = isSwimming();

            if (!this.onGround || dy != 0) {
                AxisAlignedBB bb = this.boundingBox.clone();
                bb.setMinY(bb.getMinY() - 0.75);

                boolean onGroundPrev = this.onGround;
                Block[] blocks = this.level.getCollisionBlocks(bb, true);
                this.onGround = blocks.length > 0;

                if (!this.isRiding() && !swimming && !onGroundPrev && onGround && !isInsideOfWater(false)) {
                    level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_LAND, blocks[0].getFullId(), EntityFullNames.PLAYER, getId());
                }
            }
            this.isCollided = this.onGround;
            this.updateFallState(this.onGround);

            if (!swimming && !inWaterPrev && inWater) {
                level.addLevelSoundEvent(add(0, 1.1f, 0), LevelSoundEventPacket.SOUND_SPLASH, ThreadLocalRandom.current().nextInt(700000, 1100000)); //TODO: check data
            }
            if (!wasUnderwater && underwater) {
                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_AMBIENT_UNDERWATER_ENTER, EntityFullNames.PLAYER);
            } else if (wasUnderwater && !underwater) {
                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_AMBIENT_UNDERWATER_EXIT, EntityFullNames.PLAYER);
            }

            /*this.lastMotionX = this.motionX;
            this.lastMotionY = this.motionY;
            this.lastMotionZ = this.motionZ;

            this.motionX = dx;
            this.motionY = dy;
            this.motionZ = dz;*/
        }

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
                        Block block = this.level.getBlock(x, y, z);

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
        boolean endPortal = false;
        Vector3 endGateway = null;
//        boolean water = false;

        for (Block block : this.getCollisionBlocks()) {
            int id = block.getId();
            if (id == Block.PORTAL) {
                portal = true;
            } else if (id == Block.END_PORTAL) {
                endPortal = true;
            } else if (id == Block.END_GATEWAY) {
                endGateway = block;
//            } else if (block.isWater()) {
//                water = true;
            }

            block.onEntityCollide(this);
        }

        if (portal) {
            inPortalTicks++;
        } else {
            this.inPortalTicks = 0;
        }

        if (endPortal) {
            inEndPortalTicks++;
        } else {
            inEndPortalTicks = 0;
        }

        endGatewayPos = endGateway;
        if (endGateway != null) {
            inEndGatewayTicks++;
        } else {
            inEndGatewayTicks = 0;
        }

//        if (!water && isSwimming()) {
//            setSwimming(false);
//        }
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
        if (newPos.checkIncorrectIntegerRange()) {
            this.close("", "Invalid position from " + this.getLocation() + " moving to " + newPos);
            return;
        }
        double distanceSquared = newPos.distanceSquared(this);
        boolean revert = false;
        if ((distanceSquared / ((double) (tickDiff * tickDiff))) > 225) {
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
        float distance = (float) Math.sqrt(tdx * tdx + tdz * tdz);

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
                                    log.warn(this.getServer().getLanguage().translate("nukkit.player.invalidMove", this.getName()));
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

        double deltaX = this.lastX - to.x;
        double deltaY = this.lastY - to.y;
        double deltaZ = this.lastZ - to.z;
        double deltaXZ = deltaX * deltaX + deltaZ * deltaZ;
        double delta = deltaXZ + deltaY * deltaY;
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

        if (!revert && (this.isFoodEnabled() || this.level.getDifficulty() == 0)) {
            if (this.isSurvivalLike() && this.riding == null) {
                //UpdateFoodExpLevel
                if (distance >= 0.05f) {
                    float jump = 0;
                    float swimming = this.isInsideOfWater() ? 0.015f * distance : 0;
                    if (swimming != 0) distance = 0;
                    if (this.isSprinting()) {  //Running
                        if (this.inAirTicks == 3 && swimming == 0) {
                            jump = 0.2f;
                        }
                        this.getFoodData().updateFoodExpLevel(0.1f * distance + jump + swimming);
                    } else {
                        if (this.inAirTicks == 3 && swimming == 0) {
                            jump = 0.05f;
                        }
                        this.getFoodData().updateFoodExpLevel(jump + swimming);
                    }
                }
            }
        }

        HeightRange heightRange = level.getHeightRange();
        if (!revert && !isSpectator() && this.y >= heightRange.getMinY() + 1 && this.y < heightRange.getMaxY() && deltaXZ > Mth.EPSILON) {
            int frostWalker = armorInventory.getBoots().getValidEnchantmentLevel(Enchantment.FROST_WALKER);
            if (frostWalker > 0) {
                int playerX = getFloorX();
                int playerZ = getFloorZ();
                int radius = 2 + frostWalker;
                int y = getFloorY() - 1;
                for (int x = playerX - radius; x <= playerX + radius; x++) {
                    POS:
                    for (int z = playerZ - radius; z <= playerZ + radius; z++) {
                        Block block = level.getBlock(x, y, z);
                        if (!block.isWaterSource() || !block.up().isAir()) {
                            continue;
                        }

                        Entity[] entities = level.getCollidingEntities(new SimpleAxisAlignedBB(x, y, z, x + 1, y + 1, z + 1));
                        for (Entity entity : entities) {
                            if (entity instanceof Player && ((Player) entity).isSpectator()
                                    || entity instanceof EntityProjectile
                                    || entity instanceof EntityItem
                                    || entity instanceof EntityXPOrb
                                    || entity instanceof EntityPainting
                                    || entity instanceof EntityFirework
                            ) {
                                continue;
                            }
                            continue POS;
                        }

                        Block frostedIce = Block.get(Block.FROSTED_ICE);
                        level.setBlock(block, frostedIce, true);
                        level.scheduleUpdate(frostedIce, ThreadLocalRandom.current().nextInt(20, 40));

                        if (x == playerX && z == playerZ) {
                            resetFallDistance(); //TODO
                        }
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
            this.sendPosition(from.add(0, 0.00001, 0), from.yaw, from.pitch, MovePlayerPacket.MODE_NORMAL);
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
                this.getLevel().addEntityMotion(this.getChunkX(), this.getChunkZ(), this.getId(), this.motionX, this.motionY, this.motionZ);  //Send to others
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
        this.setAttribute(
                Attribute.getAttribute(Attribute.HEALTH).setMaxValue(this.getMaxHealth()).setValue(health >= 1 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0),
                Attribute.getAttribute(Attribute.ABSORPTION).setValue(absorption),
                Attribute.getAttribute(Attribute.PLAYER_HUNGER).setValue(this.getFoodData().getLevel()),
                Attribute.getAttribute(Attribute.PLAYER_SATURATION).setValue(this.getFoodData().getFoodSaturationLevel()),
                Attribute.getAttribute(Attribute.PLAYER_EXHAUSTION).setValue(this.getFoodData().getExhaustionLevel()),
                movementSpeedAttribute.copy().setValue(movementSpeedAttribute.getModifiedValue()),
                Attribute.getAttribute(Attribute.UNDERWATER_MOVEMENT),
                Attribute.getAttribute(Attribute.LAVA_MOVEMENT),
                Attribute.getAttribute(Attribute.PLAYER_LEVEL).setValue(this.getExperienceLevel()),
                Attribute.getAttribute(Attribute.PLAYER_EXPERIENCE).setValue(Mth.clamp(((float) this.getExperience()) / calculateRequireExperience(this.getExperienceLevel()), 0, 1))
        );
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

        if (this.fishing != null) {
            if (this.fishing.isClosed() || this.server.getTick() % 20 == 0 && this.distanceSquared(fishing) > 32 * 32) {
                this.stopFishing(false);
            }
        }

        updateLocatorBar();

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

            syncAttributeMap();

            if (this.level.getDifficulty() == 0 && this.level.getGameRules().getBoolean(GameRule.NATURAL_REGENERATION)) {
                if (this.getHealth() < this.getMaxHealth() && this.ticksLived % 20 == 0) {
                    this.heal(new EntityRegainHealthEvent(this, 1, EntityRegainHealthEvent.CAUSE_REGEN));
                }

                PlayerFood foodData = this.getFoodData();
                if (foodData.getLevel() < 20 && this.ticksLived % 10 == 0) {
                    foodData.addFoodLevel(1, 0);
                }
            }

            if (this.isOnFire() && this.age % 10 == 0) {
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

                    if (EXPERIMENTAL_COMBAT_KNOCKBACK_TEST) {
                        // 地面阻力
                        this.motionX *= 0.3;
                        this.motionY = 0;
                        this.motionZ *= 0.3;
                    }
                } else {
                    if (this.checkMovement && !this.isGliding() && !server.getAllowFlight() && !this.getAdventureSettings().get(Type.ALLOW_FLIGHT) && this.inAirTicks > 20 && !this.isSleeping() && !this.isImmobile() && !this.isSwimming() && this.riding == null && !this.hasEffect(Effect.LEVITATION) && !this.hasEffect(Effect.SLOW_FALLING)) {
                        double expectedVelocity = (-this.getGravity()) / ((double) this.getDrag()) - ((-this.getGravity()) / ((double) this.getDrag())) * Math.exp(-((double) this.getDrag()) * ((double) (this.inAirTicks - this.startAirTicks)));
                        double diff = (this.speed.y - expectedVelocity) * (this.speed.y - expectedVelocity);

                        int block = level.getBlock(this).getId();
                        boolean ignore = block == Block.LADDER || block == Block.VINE || block == Block.WEB || block == BlockID.BUBBLE_COLUMN || block == BlockID.SCAFFOLDING || block == BlockID.SWEET_BERRY_BUSH || block == BlockID.WEEPING_VINES || block == BlockID.TWISTING_VINES || block == BlockID.CAVE_VINES || block == BlockID.CAVE_VINES_BODY_WITH_BERRIES || block == BlockID.CAVE_VINES_HEAD_WITH_BERRIES || block == BlockID.POWDER_SNOW || block == BlockID.BIG_DRIPLEAF
                                || block == BlockID.HONEY_BLOCK; //TODO: slide down

                        if (!this.hasEffect(Effect.JUMP_BOOST) && diff > 0.6 && expectedVelocity < this.speed.y && !ignore) {
                            if (this.inAirTicks < 150) {
                                this.setMotion(new Vector3(0, expectedVelocity, 0));
                            } else if (this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server", false)) {
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

                    if (EXPERIMENTAL_COMBAT_KNOCKBACK_TEST) {
                        // 空气阻力
                        this.motionX *= 0.9900000095367432;
                        this.motionY *= 0.9800000190734863;
                        this.motionZ *= 0.9900000095367432;
                    }

                    ++this.inAirTicks;
                }

                if (EXPERIMENTAL_COMBAT_KNOCKBACK_TEST) {
                    if (Math.abs(this.motionX) < 0.0001) this.motionX = 0;
                    if (Math.abs(this.motionY) < 0.0001) this.motionY = 0;
                    if (Math.abs(this.motionZ) < 0.0001) this.motionZ = 0;
                }

                if (this.isSurvivalLike()) {
                    if (this.getFoodData() != null) this.getFoodData().update(tickDiff);
                }
            }

            if (this.isSleeping() && spawnBlockPosition != null) {
                Block block = this.spawnBlockPosition.level.getBlock(this.spawnBlockPosition);
                if (block.getId() != Block.BLOCK_BED || (block.getDamage() & 0x8) != 0x8) {
                    this.stopSleep();
                }
            }

            if (isSurvivalLike() && age % 20 == 0 && isGliding()) {
                Item chestplate = armorInventory.getChestplate();
                if (chestplate.getId() == Item.ELYTRA) {
                    int damage = chestplate.getDamage();
                    if (damage < chestplate.getMaxDurability() - 1) {
                        if (chestplate.hurtAndBreak(1) != 0) {
                            armorInventory.setChestplate(chestplate, true);
                        }
                    } else {
                        setGliding(false);
                    }
                }
            }

            if (inventory.getItemInHand() instanceof ItemChemicalTickable tickable && tickable.tick(this)) {
                inventory.setItemInHand(tickable);
            }
            if (offhandInventory.getItem() instanceof ItemChemicalTickable tickable && tickable.tick(this)) {
                offhandInventory.setItem(tickable);
            }

            if (isBreakingBlock() && age % 5 == 0) {
                level.addLevelSoundEvent(breakingBlock.blockCenter(), LevelSoundEventPacket.SOUND_HIT, breakingBlock.getFullId());
            }

            boolean isSwinging;
            if (isUsingItem()) {
                isSwinging = true;

                Item item = inventory.getItemInHand();
                if (item.canRelease()) {
                    int ticksUsed = this.server.getTick() - this.startAction;
                    int timeUsedTicks = (int) (System.currentTimeMillis() - this.startActionTimestamp) / 50;
                    if (!isSpectator()) {
                        item.onUsing(this, timeUsedTicks);
                    }

                    int useDuration = item.getUseDuration();
                    if (useDuration > 0 && ticksUsed > useDuration) {
                        // client bug fixes: left click when using an item
                        Vector3 directionVector = this.getDirectionVector();
                        PlayerInteractEvent event = new PlayerInteractEvent(this, item, directionVector, null, PlayerInteractEvent.Action.RIGHT_CLICK_AIR);
                        if (isSpectator()) {
                            event.setCancelled();
                        }
                        event.call();
                        if (event.isCancelled()) {
                            this.inventory.sendHeldItem(this);
                        } else if (item.onClickAir(this, directionVector)) {
                            if (this.isSurvivalLike()) {
                                this.inventory.setItemInHand(item);
                            }

                            if (!this.isUsingItem()) {
                                this.setUsingItem(item.canRelease());
                            } else {
                                this.setUsingItem(false);

                                if (!item.onUse(this, ticksUsed)) {
                                    this.inventory.sendContents(this);
                                }

                                if (item.canRelease() && !item.isNull()) {
                                    this.setUsingItem(true);
                                }
                            }
                        }
                    }
                }
            } else {
                isSwinging = this.swinging;
            }

            boolean blocking = !isSwinging && shieldCooldown < server.getTick() && (isRiding() || isSneaking() && !getDataFlag(DATA_FLAG_IN_SCAFFOLDING) && !getDataFlag(DATA_FLAG_OVER_SCAFFOLDING)) && !isSwimming();
            if (getDataFlag(DATA_FLAG_BLOCKED_USING_SHIELD)) {
                setDataFlag(DATA_FLAG_BLOCKED_USING_SHIELD, false);
            }
            if (getDataFlag(DATA_FLAG_BLOCKED_USING_DAMAGED_SHIELD)) {
                setDataFlag(DATA_FLAG_BLOCKED_USING_DAMAGED_SHIELD, false);
            }

            Pair<Inventory, Item> shield = getCurrentActiveShield();
            if (shield != null) {
                if (shieldBlockingTick != 0) {
                    if (!blocking) {
                        shieldBlockingTick = 0;
                    }
                } else if (blocking) {
                    shieldBlockingTick = server.getTick();
                }

                boolean transitionBlocking = shieldBlockingTick != prevShieldBlockingTick;
                setDataFlag(DATA_FLAG_TRANSITION_BLOCKING, transitionBlocking);
                if (transitionBlocking) {
                    prevShieldBlockingTick = shieldBlockingTick;
                }
            } else {
                shieldBlockingTick = 0;
            }

            if (!getDataFlag(DATA_FLAG_BLOCKING) && blocking) {
                setDataFlag(DATA_FLAG_TRANSITION_BLOCKING, true);
            }
            setDataFlag(DATA_FLAG_BLOCKING, blocking);

            if (this.swinging) {
                if (++this.swingTime >= getCurrentSwingDuration()) {
                    this.swingTime = 0;
                    this.swinging = false;
                }
            } else {
                this.swingTime = 0;
            }

            if (damageNearbyMobsTick > 0 && !isSpectator()) {
                damageNearbyMobsTick--;

                for (Entity entity : level.getNearbyEntities(boundingBox, this)) {
                    if (!(entity instanceof EntityLiving)) {
                        continue;
                    }

                    if (entity instanceof Player player && (player.isCreativeLike() || level.getDifficulty() == 0 || !server.getConfiguration().isPvp() || !level.getGameRules().getBoolean(GameRule.PVP))) {
                        continue;
                    }

                    Item item = inventory.getItemInHand();
                    Enchantment[] enchantments = item.getId() != Item.ENCHANTED_BOOK ? item.getEnchantments() : Enchantment.EMPTY;

                    ItemAttackDamageEvent event = new ItemAttackDamageEvent(item);
                    event.call();
                    float damage = event.getAttackDamage();

                    float damageBonus = 0;
                    for (Enchantment enchantment : enchantments) {
                        damageBonus += enchantment.getDamageBonus(this, entity);
                    }
                    damage += Mth.floor(damageBonus);

                    Map<DamageModifier, Float> modifiers = new EnumMap<>(DamageModifier.class);
                    modifiers.put(DamageModifier.BASE, damage);

                    float knockbackH = EntityDamageByEntityEvent.GLOBAL_KNOCKBACK_H;
                    float knockbackV = EntityDamageByEntityEvent.GLOBAL_KNOCKBACK_V;
                    int knockbackEnchant = !item.is(Item.ENCHANTED_BOOK) ? item.getEnchantmentLevel(Enchantment.KNOCKBACK) : 0;
                    if (knockbackEnchant > 0) {
                        knockbackH += knockbackEnchant * 0.1f;
                        knockbackV += knockbackEnchant * 0.1f;
                    }

                    if (!entity.attack(new EntityDamageByEntityEvent(this, entity, DamageCause.ENTITY_ATTACK, modifiers, knockbackH, knockbackV, enchantments))) {
                        continue;
                    }

                    for (Enchantment enchantment : enchantments) {
                        enchantment.doPostAttack(item, this, entity, null);
                    }

                    if (isSurvivalLike() && item.isTool() && item.useOn(entity)) {
                        if (item.getDamage() > item.getMaxDurability()) {
                            inventory.setItemInHand(Items.air());
                            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BREAK);
                        } else {
                            inventory.setItemInHand(item);
                        }
                    }

                    setMotion(getMotion().multiply(-0.2));

                    damageNearbyMobsTick = 0;
                    setDataFlag(DATA_FLAG_SPIN_ATTACK, false);
                    break;
                }
            } else {
                damageNearbyMobsTick = 0;
                setDataFlag(DATA_FLAG_SPIN_ATTACK, false);
            }
        }

        this.checkTeleportPosition();
        /*if (currentTick % 10 == 0) {
            this.checkInteractNearby(); // 在客户端计算, 结果通过 action 为 mouseover 的 InteractPacket 发给服务端 (runtimeEntityId 为 0 表示移开)
        }*/

        if (this.spawned && !this.dummyBossBars.isEmpty() && currentTick % 100 == 0) {
            this.dummyBossBars.values().forEach(DummyBossBar::updateBossEntityPosition);
        }

        return true;
    }

    public boolean checkInteractNearby = true;

    public void checkInteractNearby() {
        if (!checkInteractNearby) return;
        int interactDistance = isCreative() ? 5 : 3;
        EntityInteractable onInteract = getEntityPlayerLookingAt(interactDistance);
        if (onInteract != null) {
            String text = onInteract.getInteractButtonText(this);
            setButtonText(text);
            setDataProperty(new ByteEntityData(DATA_CAN_RIDE_TARGET, onInteract != riding && onInteract instanceof EntityRideable && ("action.interact.mount".equals(text) || text.startsWith("action.interact.ride."))));
        } else {
            setButtonText("");
            setDataProperty(new ByteEntityData(DATA_CAN_RIDE_TARGET, false));
        }
    }

    /**
     * Returns the Entity the player is looking at currently
     *
     * @param maxDistance the maximum distance to check for entities
     * @return Entity|null    either NULL if no entity is found or an instance of the entity
     */
    public EntityInteractable getEntityPlayerLookingAt(int maxDistance) {
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


        return entity;
    }

    private EntityInteractable getEntityAtPosition(Entity[] nearbyEntities, int x, int y, int z) {
        for (Entity nearestEntity : nearbyEntities) {
            if (nearestEntity.getFloorX() == x && nearestEntity.getFloorY() == y && nearestEntity.getFloorZ() == z
                    && nearestEntity instanceof EntityInteractable
                    && ((EntityInteractable) nearestEntity).canDoInteraction(this)) {
                return (EntityInteractable) nearestEntity;
            }
        }
        return null;
    }

    private boolean processResourcePackChunkRequest() {
        ObjectIntPair<ResourcePack> request = resourcePackChunkRequests.poll();
        if (request == null) {
            return false;
        }

        ResourcePack pack = request.left();
        int index = request.rightInt();

        ResourcePackChunkDataPacket packet = new ResourcePackChunkDataPacket();
        packet.packId = pack.getPackId();
        packet.chunkIndex = index;
        packet.progress = (long) RESOURCE_PACK_CHUNK_SIZE * index;
        packet.data = pack.getPackChunk(index);
        dataPacket(packet);
        return true;
    }

    public void checkNetwork() {
        if (processResourcePackChunkRequest()) {
            return;
        }

        if (!this.isOnline()) {
            return;
        }

        checkViolation();

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
        return this.canInteract(pos, maxDistance, Mth.SQRT_OF_THREE / 2);
    }

    public boolean canInteract(Vector3 pos, double maxDistance, double maxDiff) {
        Vector3 eyePos = this.getEyePosition();
        if (eyePos.distanceSquared(pos) > maxDistance * maxDistance) {
            return false;
        }

        Vector3 dV = this.getDirectionVector();
        double eyeDot = dV.dot(eyePos);
        double targetDot = dV.dot(pos);
        return (targetDot - eyeDot) >= -maxDiff;
    }

    public void setOfflinePlayerData(CompoundTag nbt) {
        offlinePlayerData = nbt;
    }

    protected void processLogin() {
        if (!this.server.isWhitelisted((this.getName()).toLowerCase())) {
            this.kick(PlayerKickEvent.Reason.NOT_WHITELISTED, "Server is white-listed", false);

            return;
        } else if (this.isBanned()) {
            this.kick(PlayerKickEvent.Reason.NAME_BANNED, "You are banned", false);
            return;
        } else if (this.server.getIPBans().isBanned(this.getAddress())) {
            this.kick(PlayerKickEvent.Reason.IP_BANNED, "You are banned", false);
            return;
        }

        if (this.hasPermission(Server.BROADCAST_CHANNEL_USERS)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this);
        }
        if (this.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);
        }

        for (Player p : this.server.getOnlinePlayerList()) {
            if (p != this && (p.getName() != null && p.getName().equalsIgnoreCase(this.getName()) ||
                    this.getUniqueId().equals(p.getUniqueId()))) {
                if (!p.kick(PlayerKickEvent.Reason.NEW_CONNECTION, "disconnectionScreen.loggedinOtherLocation", false)) {
                    this.close("", "disconnectionScreen.serverIdConflict");
                    return;
                }
            }
        }

        CompoundTag nbt = offlinePlayerData != null ? offlinePlayerData : this.server.getOfflinePlayerData(this.username);
        if (nbt == null) {
            this.close("", "disconnectionScreen.worldCorruption");

            return;
        }

        this.playedBefore = (nbt.getLong("lastPlayed") - nbt.getLong("firstPlayed")) > 1;

        boolean alive = true;

        nbt.putString("NameTag", this.username);

        if (0 >= nbt.getFloat("Health")) {
            alive = false;
        }

        int exp = nbt.getInt("EXP");
        int expLevel = nbt.getInt("expLevel");
        this.setExperience(exp, expLevel);

        this.enchantmentSeed = nbt.getInt("EnchantmentSeed");
        if (enchantmentSeed == 0) {
            enchantmentSeed = ThreadLocalRandom.current().nextInt();
        }

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
                .set(Type.INSTABUILD, gamemode == CREATIVE)
                .set(Type.INVULNERABLE, isCreative() || isSpectator())
                .set(Type.OPERATOR, isOp())
                .set(Type.TELEPORT, hasPermission("nukkit.command.teleport"));

        Level level = this.server.getLevelByName(nbt.getString("Level"));
        if (level != null) {
            level = level.getDimension(Dimension.byIdOrDefault(nbt.getInt("DimensionId", DimensionID.OVERWORLD))) ;
        }
        if (level == null || !alive) {
            this.setLevel(this.server.getDefaultLevel());
            nbt.putString("Level", this.level.getName());
            nbt.putInt("DimensionId", this.level.getDimension().getId());
            nbt.getList("Pos", DoubleTag.class)
                    .add(new DoubleTag("", this.level.getSpawnLocation().x))
                    .add(new DoubleTag("", this.level.getSpawnLocation().y))
                    .add(new DoubleTag("", this.level.getSpawnLocation().z));
        } else {
            this.setLevel(level);
        }

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
        if (!this.namedTag.contains("foodSaturationLevel")) {
            this.namedTag.putFloat("foodSaturationLevel", 5);
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
            level = level.getDimension(Dimension.byIdOrDefault(this.namedTag.getInt("SpawnDimension", DimensionID.OVERWORLD))) ;
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
        startGamePacket.dimension = (byte) (spawnPosition.level.getDimension().getId() & 0xff);
        startGamePacket.worldGamemode = getClientFriendlyGamemode(this.gamemode);
        startGamePacket.difficulty = this.level.getDifficulty();
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
            this.setDataFlag(DATA_FLAG_SILENT, true);
            this.setDataFlag(DATA_FLAG_HAS_COLLISION, false);
        }

        log.info(this.getServer().getLanguage().translate("nukkit.player.logIn",
                TextFormat.AQUA + this.username + TextFormat.WHITE,
                this.getAddress(),
                this.getPort(),
                this.id,
                this.level.getName(),
                NukkitMath.round(this.x, 4),
                NukkitMath.round(this.y, 4),
                NukkitMath.round(this.z, 4)));

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

        {
            DataPacketReceiveEvent ev = new DataPacketReceiveEvent(this, packet);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return;
            }

            if (packet.pid() == ProtocolInfo.BATCH_PACKET) {
//                this.server.getNetwork().processBatch((BatchPacket) packet, this);
                // Batch packets are already decoded in Synapse
                onPacketViolation(PacketViolationReason.NESTED_BATCH);
                return;
            }

            packetswitch:
            switch (packet.pid()) {
                case ProtocolInfo.LOGIN_PACKET:
                    if (this.loggedIn) {
                        onPacketViolation(PacketViolationReason.ALREADY_LOGGED_IN);
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

                    if (this.server.getOnlinePlayerCount() >= this.server.getMaxPlayers() && this.kick(PlayerKickEvent.Reason.SERVER_FULL, "disconnectionScreen.serverFull", false)) {
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
                        this.setSkin(loginPacket.skin);
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

                    this.server.getScheduler().scheduleAsyncTask(null, this.preLoginEventTask);

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
                                dataInfoPacket.chunkCount = resourcePack.getChunkCount();
                                dataInfoPacket.compressedPackSize = resourcePack.getPackSize();
                                dataInfoPacket.sha256 = resourcePack.getSha256();
                                if (resourcePack.getPackType().equals("resources")) {
                                    dataInfoPacket.type = ResourcePackDataInfoPacket.TYPE_RESOURCE;
                                } else if (resourcePack.getPackType().equals("data")) {
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

                    ResourcePack resourcePack = this.resourcePacks.get(requestPacket.packId);
                    if (resourcePack == null) {
                        resourcePack = this.behaviourPacks.get(requestPacket.packId);
                        if (resourcePack == null) {
                            this.close("", "disconnectionScreen.resourcePack");
                            break;
                        }
                    }

                    resourcePackChunkRequests.offer(ObjectIntPair.of(resourcePack, requestPacket.chunkIndex));
                    break;
                case ProtocolInfo.PLAYER_INPUT_PACKET:
                    if (isServerAuthoritativeMovementEnabled()) {
                        onPacketViolation(PacketViolationReason.IMPOSSIBLE_BEHAVIOR, "input");
                        return;
                    }

                    if (!this.isAlive() || !this.spawned) {
                        break;
                    }

                    PlayerInputPacket ipk = (PlayerInputPacket) packet;
                    float moveVecX = ipk.motionX;
                    float moveVecY = ipk.motionY;
                    if (!validateVehicleInput(moveVecX) || !validateVehicleInput(moveVecY)) {
                        this.getServer().getLogger().warning("Invalid vehicle input received: " + this.getName());
                        this.close("", "Invalid vehicle input");
                        return;
                    }

                    if (riding != null && (moveVecX != 0 || moveVecY != 0) && riding.isControlling(this)) {
                        moveVecX = Mth.clamp(moveVecX, -1, 1);
                        moveVecY = Mth.clamp(moveVecY, -1, 1);

                        if (riding instanceof EntityRideable) {
                            ((EntityRideable) riding).onPlayerInput(this, moveVecX, moveVecY);
                        }

                        new PlayerVehicleInputEvent(this, moveVecX, moveVecY, ipk.jumping, ipk.sneaking).call();
                    }
                    break;
                case ProtocolInfo.MOVE_PLAYER_PACKET:
                    if (this.teleportPosition != null) {
                        break;
                    }

                    MovePlayerPacket movePlayerPacket = (MovePlayerPacket) packet;
                    if (!validateCoordinate(movePlayerPacket.x) || !validateCoordinate(movePlayerPacket.y) || !validateCoordinate(movePlayerPacket.z)
                            || !validateFloat(movePlayerPacket.pitch) || !validateFloat(movePlayerPacket.yaw) || !validateFloat(movePlayerPacket.headYaw)) {
                        this.getServer().getLogger().warning("Invalid movement received: " + this.getName());
                        this.close("", "Invalid movement");
                        return;
                    }

                    Vector3 newPos = new Vector3(movePlayerPacket.x, movePlayerPacket.y - this.getBaseOffset(), movePlayerPacket.z);

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
                    break;
                case ProtocolInfo.MOVE_ACTOR_ABSOLUTE_PACKET:
                    MoveEntityPacket moveEntityPacket = (MoveEntityPacket) packet;
                    if (!validateCoordinate(moveEntityPacket.x) || !validateCoordinate(moveEntityPacket.y) || !validateCoordinate(moveEntityPacket.z)
                            || !validateFloat(moveEntityPacket.pitch) || !validateFloat(moveEntityPacket.yaw) || !validateFloat(moveEntityPacket.headYaw)) {
                        this.getServer().getLogger().warning("Invalid vehicle movement received: " + this.getName());
                        this.close("", "Invalid vehicle movement");
                        return;
                    }

                    if (this.riding == null || this.riding.getId() != moveEntityPacket.eid || !this.riding.isControlling(this)) {
                        break;
                    }

                    if (this.riding instanceof EntityRideable rideable) {
                        if (this.temporalVector.setComponents(moveEntityPacket.x, moveEntityPacket.y, moveEntityPacket.z).distanceSquared(this.riding) < 1000) {
                            rideable.onPlayerInput(this, moveEntityPacket.x, moveEntityPacket.y, moveEntityPacket.z, moveEntityPacket.yaw % 360, 0);
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
                        this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server", false);
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

                    this.setDataFlag(DATA_FLAG_ACTION, false);

                    break;
                case ProtocolInfo.PLAYER_ACTION_PACKET:
                    PlayerActionPacket playerActionPacket = (PlayerActionPacket) packet;
                    if (!this.spawned || (!this.isAlive() && playerActionPacket.action != PlayerActionPacket.ACTION_RESPAWN /*&& playerActionPacket.action != PlayerActionPacket.ACTION_DIMENSION_CHANGE_REQUEST*/)) {
                        break;
                    }

                    playerActionPacket.entityId = this.id;
                    BlockVector3 pos = new BlockVector3(playerActionPacket.x, playerActionPacket.y, playerActionPacket.z);

                    switch (playerActionPacket.action) {
                        case PlayerActionPacket.ACTION_START_BREAK:
                            if (this.isSpectator()) {
                                break;
                            }
                            long currentBreak = System.currentTimeMillis();
                            BlockVector3 currentBreakPosition = new BlockVector3(playerActionPacket.x, playerActionPacket.y, playerActionPacket.z);
                            // HACK: Client spams multiple left clicks so we need to skip them.
                            if ((lastBreakPosition.equalsVec(currentBreakPosition) && (currentBreak - this.lastBreak) < 10) || !canInteract(pos.blockCenter(), isCreative() ? MAX_REACH_DISTANCE_CREATIVE : MAX_REACH_DISTANCE_SURVIVAL)) {
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
                            if (!isAdventure() && target.getId() == Block.NOTEBLOCK) {
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
                                    this.getLevel().addChunkPacket(pos.getChunkX(), pos.getChunkZ(), pk);
                                }
                            }

                            this.breakingBlock = target;
                            this.lastBreak = currentBreak;
                            this.lastBreakPosition = currentBreakPosition;
                            break;
                        case PlayerActionPacket.ACTION_ABORT_BREAK:
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
                        case PlayerActionPacket.ACTION_STOP_SLEEPING:
                            this.stopSleep();
                            break;
                        case PlayerActionPacket.ACTION_RESPAWN:
                            if (!this.spawned || this.isAlive() || !this.isOnline()) {
                                break;
                            }
                            this.respawn();
                            break;
                        case PlayerActionPacket.ACTION_JUMP:
                            PlayerJumpEvent playerJumpEvent = new PlayerJumpEvent(this);
                            this.server.getPluginManager().callEvent(playerJumpEvent);
                            break packetswitch;
                        case PlayerActionPacket.ACTION_START_SPRINT:
                            if (isSprinting()) {
                                break packetswitch;
                            }
                            PlayerToggleSprintEvent playerToggleSprintEvent = new PlayerToggleSprintEvent(this, true);
                            if (hasEffect(Effect.BLINDNESS)) {
                                playerToggleSprintEvent.setCancelled();
                            }
                            this.server.getPluginManager().callEvent(playerToggleSprintEvent);
                            if (playerToggleSprintEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSprinting(true);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_STOP_SPRINT:
                            if (!isSprinting()) {
                                break packetswitch;
                            }
                            playerToggleSprintEvent = new PlayerToggleSprintEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleSprintEvent);
                            if (playerToggleSprintEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSprinting(false);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_START_SNEAK:
                            if (isSneaking()) {
                                break packetswitch;
                            }
                            PlayerToggleSneakEvent playerToggleSneakEvent = new PlayerToggleSneakEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleSneakEvent);
                            if (playerToggleSneakEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSneaking(true);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_STOP_SNEAK:
                            if (!isSneaking()) {
                                break packetswitch;
                            }
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
                            if (isGliding()) {
                                break packetswitch;
                            }
                            PlayerToggleGlideEvent playerToggleGlideEvent = new PlayerToggleGlideEvent(this, true);
                            Item chestplate = getArmorInventory().getChestplate();
                            if (chestplate.getId() != Item.ELYTRA || chestplate.getDamage() >= chestplate.getMaxDurability() - 1) {
                                playerToggleGlideEvent.setCancelled();
                            }
                            this.server.getPluginManager().callEvent(playerToggleGlideEvent);
                            if (playerToggleGlideEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setGliding(true);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_STOP_GLIDE:
                            if (!isGliding()) {
                                break packetswitch;
                            }
                            playerToggleGlideEvent = new PlayerToggleGlideEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleGlideEvent);
                            if (playerToggleGlideEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setGliding(false);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_BUILD_DENIED:
//                            level.addLevelEvent(pos.blockCenter(), LevelEventPacket.EVENT_PARTICLE_BLOCK_FORCE_FIELD);
                            LevelEventPacket pk = new LevelEventPacket();
                            pk.evid = LevelEventPacket.EVENT_PARTICLE_BLOCK_FORCE_FIELD;
                            pk.x = pos.x + 0.5f;
                            pk.y = pos.y + 0.5f;
                            pk.z = pos.z + 0.5f;
                            dataPacket(pk);
                            break packetswitch;
                        case PlayerActionPacket.ACTION_CONTINUE_BREAK:
                            if (this.isBreakingBlock()) {
                                block = this.level.getBlock(pos, false);
                                face = BlockFace.fromIndex(playerActionPacket.data);
                                Vector3 blockCenter = pos.blockCenter();
                                this.level.addParticle(new PunchBlockParticle(blockCenter, block, face));
                                level.addLevelEvent(blockCenter, LevelEventPacket.EVENT_PARTICLE_PUNCH_BLOCK_DOWN + face.getIndex(), block.getFullId());

                                int breakTime = Mth.ceil(block.getBreakTime(inventory.getItemInHand(), this) * 20);
                                level.addLevelEvent(pos.asVector3(), LevelEventPacket.EVENT_BLOCK_UPDATE_BREAK, breakTime <= 0 ? 0 : 65535 / breakTime);
                            }
                            break;
                    }

                    this.setUsingItem(false);
                    break;
                case ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET:
                    break;
                case ProtocolInfo.MODAL_FORM_RESPONSE_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    ModalFormResponsePacket modalFormPacket = (ModalFormResponsePacket) packet;

                    FormWindow window = formWindows.get(modalFormPacket.formId);
                    if (window != null) {
                        window.setResponse(modalFormPacket.data.trim(), getProtocol());

                        PlayerFormRespondedEvent event = new PlayerFormRespondedEvent(this, modalFormPacket.formId, window);
                        getServer().getPluginManager().callEvent(event);

                        formWindows.remove(modalFormPacket.formId);
                        break;
                    }

                    window = serverSettings.get(modalFormPacket.formId);
                    if (window != null) {
                        window.setResponse(modalFormPacket.data.trim(), getProtocol());

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
                        this.lookAtEntity = null;
                        this.setButtonText("");
                        setDataProperty(new ByteEntityData(DATA_CAN_RIDE_TARGET, false));
                        break;
                    }

                    Entity targetEntity = interactPacket.target == this.getId() ? this : this.level.getEntity(interactPacket.target);

                    if (targetEntity == null || !this.isAlive() || !targetEntity.isAlive()) {
                        break;
                    }

                    if (targetEntity instanceof EntityItem || targetEntity instanceof EntityArrow || targetEntity instanceof EntityXPOrb) {
                        this.kick(PlayerKickEvent.Reason.INVALID_PVE, "Attempting to interact with an invalid entity", false);
                        log.warn(this.getServer().getLanguage().translate("nukkit.player.invalidEntity", this.getName()));
                        break;
                    }

                    switch (interactPacket.action) {
                        case InteractPacket.ACTION_MOUSEOVER:
                            this.lookAtEntity = targetEntity;

                            if (targetEntity instanceof EntityInteractable interactable) {
                                String text = interactable.getInteractButtonText(this);
                                this.setButtonText(text);
                                setDataProperty(new ByteEntityData(DATA_CAN_RIDE_TARGET, targetEntity != riding && targetEntity instanceof EntityRideable && ("action.interact.mount".equals(text) || text.startsWith("action.interact.ride."))));
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
                    if (this.distanceSquared(pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z) > 1000) {
                        this.getServer().getLogger().debug(this.getName() + ": Block pick request for a block too far away");
                        return;
                    }
                    Block block = this.level.getBlock(pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z, false);
                    if (block.isAir()) {
                        return;
                    }
                    item = block.pick(pickRequestPacket.addUserData);

                    if (pickRequestPacket.addUserData) {
                        BlockEntity blockEntity = this.getLevel().getBlockEntityIfLoaded(pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z);
                        if (blockEntity != null) {
                            CompoundTag nbt = blockEntity.getCleanedNBT();
                            if (nbt != null) {
                                item.setCustomBlockData(nbt);
                                item.setLore("+(DATA)");
                            }
                        }
                    }

                    PlayerBlockPickEvent pickEvent = new PlayerBlockPickEvent(this, block, item);
                    if (this.isSpectator()) {
                        pickEvent.setCancelled();
                    }

                    this.server.getPluginManager().callEvent(pickEvent);

                    if (!pickEvent.isCancelled()) {
                        item = pickEvent.getItem();

                        GUIDataPickItemPacket pk = new GUIDataPickItemPacket();
                        pk.itemName = item.getName();
                        pk.itemEffectName = "";

                        boolean itemExists = false;
                        int itemSlot = -1;
                        for (int slot = 0; slot < this.inventory.getHotbarSize(); slot++) {
                            if (this.inventory.getItem(slot).equals(item)) {
                                if (slot < this.inventory.getHotbarSize()) {
                                    pk.hotbarSlot = slot;
                                    dataPacket(pk);
                                    this.inventory.setHeldItemIndex(slot);
                                } else {
                                    itemSlot = slot;
                                }
                                itemExists = true;
                                break;
                            }
                        }

                        for (int slot = 0; slot < this.inventory.getHotbarSize(); slot++) {
                            if (this.inventory.getItem(slot).isNull()) {
                                if (!itemExists && this.isCreative()) {
                                    pk.hotbarSlot = slot;
                                    dataPacket(pk);
                                    this.inventory.setHeldItemIndex(slot);
                                    this.inventory.setItemInHand(item);
                                    break packetswitch;
                                } else if (itemSlot > -1) {
                                    pk.hotbarSlot = slot;
                                    dataPacket(pk);
                                    this.inventory.setHeldItemIndex(slot);
                                    this.inventory.setItemInHand(this.inventory.getItem(itemSlot));
                                    this.inventory.clear(itemSlot);
                                    break packetswitch;
                                }
                            }
                        }

                        if (!itemExists && this.isCreative()) {
                            Item itemInHand = this.inventory.getItemInHand();
                            pk.hotbarSlot = inventory.getHeldItemIndex();
                            dataPacket(pk);
                            this.inventory.setItemInHand(item);
                            if (!this.inventory.isFull()) {
                                for (int slot = 0; slot < this.inventory.getSize(); slot++) {
                                    if (this.inventory.getItem(slot).isNull()) {
                                        this.inventory.setItem(slot, itemInHand);
                                        break;
                                    }
                                }
                            }
                        } else if (itemSlot > -1) {
                            Item itemInHand = this.inventory.getItemInHand();
                            pk.hotbarSlot = inventory.getHeldItemIndex();
                            dataPacket(pk);
                            this.inventory.setItemInHand(this.inventory.getItem(itemSlot));
                            this.inventory.setItem(itemSlot, itemInHand);
                        }
                    }
                    break;
                case ProtocolInfo.ANIMATE_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    AnimatePacket animatePk = (AnimatePacket) packet;

                    PlayerAnimationEvent animationEvent = new PlayerAnimationEvent(this, animatePk.action);
                    this.server.getPluginManager().callEvent(animationEvent);
                    if (animationEvent.isCancelled()) {
                        break;
                    }

                    AnimatePacket.Action animation = animationEvent.getAnimationType();

                    switch (animation) {
                        case ROW_RIGHT:
                        case ROW_LEFT:
                            if (this.riding instanceof EntityBoat boat) {
                                float paddleTime = animatePk.rowingTime;
                                if (!validateFloat(paddleTime)) {
                                    onPacketViolation(PacketViolationReason.IMPOSSIBLE_BEHAVIOR, "anim_nan");
                                    return;
                                }

                                boat.onPaddle(animation, paddleTime);
                            }
                            return;
                        case SWING_ARM:
                            this.swingTime = -1;
                            this.swinging = true;

                            if (false && !isBreakingBlock()) {
                                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ATTACK_NODAMAGE, EntityFullNames.PLAYER, new Player[]{this});
                            }
                            break;
                        case WAKE_UP:
                            break;
                        default:
                            onPacketViolation(PacketViolationReason.IMPOSSIBLE_BEHAVIOR, "anim");
                            return;
                    }

                    AnimatePacket animatePacket = new AnimatePacket();
                    animatePacket.eid = this.getId();
                    animatePacket.action = animationEvent.getAnimationType();
                    animatePacket.rowingTime = animatePk.rowingTime;
                    Server.broadcastPacket(this.getViewers().values(), animatePacket);
                    break;
                case ProtocolInfo.SET_HEALTH_PACKET:
                    //use UpdateAttributePacket instead
                    break;

                case ProtocolInfo.ACTOR_EVENT_PACKET:
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
                            Item held = inventory.getItemInHand();
                            if (!(held instanceof ItemEdible) && !held.is(Item.POTION) && !held.is(Item.MILK_BUCKET) && (!(held instanceof ItemChemicalTickable tickable) || tickable.isActivated())) {
                                break;
                            }

                            EntityEventPacket pk = new EntityEventPacket();
                            pk.eid = getId();
                            pk.event = EntityEventPacket.EATING_ITEM;
                            pk.data = (held.getId() << 16) | held.getDamage();
                            this.dataPacket(pk);
                            Server.broadcastPacket(this.getViewers().values(), pk);
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
                    CommandRequestPacket commandRequestPacket = (CommandRequestPacket) packet;

                    VIOLATION_LISTENER.onCommandRequest(this, commandRequestPacket.command.length());

                    if (!commandRequestPacket.command.startsWith("/")) {
                        break;
                    }

                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    this.resetCraftingGridType();
                    this.craftingType = CRAFTING_SMALL;

                    if (this.messageCounter <= 0) {
                        break;
                    }

                    if (commandRequestPacket.command.length() > 512) {
                        break;
                    }

                    this.messageCounter--;

                    String command = commandRequestPacket.command;
                    if (this.removeFormat) {
                        command = TextFormat.clean(command, true);
                    }

                    PlayerCommandPreprocessEvent playerCommandPreprocessEvent = new PlayerCommandPreprocessEvent(this, command);
                    this.server.getPluginManager().callEvent(playerCommandPreprocessEvent);
                    if (playerCommandPreprocessEvent.isCancelled()) {
                        break;
                    }

                    this.server.dispatchCommand(playerCommandPreprocessEvent.getPlayer(), playerCommandPreprocessEvent.getMessage().substring(1));
                    break;
                case ProtocolInfo.TEXT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    TextPacket textPacket = (TextPacket) packet;

                    if (textPacket.type == TextPacket.TYPE_CHAT) {
                        this.preChat(textPacket.message);
                    }
                    break;
                case ProtocolInfo.CONTAINER_CLOSE_PACKET:
                    ContainerClosePacket containerClosePacket = (ContainerClosePacket) packet;
                    if (!this.spawned || containerClosePacket.windowId == 0) {
                        break;
                    }
                    this.craftingType = CRAFTING_SMALL;
                    this.resetCraftingGridType();

                    Inventory windowInventory = this.windowIndex.get(containerClosePacket.windowId);
                    if (windowInventory != null) {
                        this.server.getPluginManager().callEvent(new InventoryCloseEvent(windowInventory, this));
                        this.removeWindow(this.windowIndex.get(containerClosePacket.windowId));
                    } else {
                        this.windowIndex.remove(containerClosePacket.windowId);
                    }
                    break;
                case ProtocolInfo.CRAFTING_EVENT_PACKET:
                    break;
                case ProtocolInfo.BLOCK_ACTOR_DATA_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    BlockEntityDataPacket blockEntityDataPacket = (BlockEntityDataPacket) packet;
                    this.craftingType = CRAFTING_SMALL;
                    this.resetCraftingGridType();

                    if (this.distanceSquared(blockEntityDataPacket.x, blockEntityDataPacket.y, blockEntityDataPacket.z) > 10000) {
                        break;
                    }

                    BlockEntity t = this.level.getBlockEntityIfLoaded(blockEntityDataPacket.x, blockEntityDataPacket.y, blockEntityDataPacket.z);
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
                    this.viewDistance = Mth.clamp(requestChunkRadiusPacket.radius, 4, 96);
                    int requestedRadius = Math.min(this.viewDistance, this.getMaxViewDistance());

                    // 玩家已登录后主动改变视距时触发事件
                    if (this.spawned) {
                        PlayerChunkRadiusRequestEvent event = new PlayerChunkRadiusRequestEvent(this, this.chunkRadius, requestedRadius);
                        this.server.getPluginManager().callEvent(event);

                        ChunkRadiusUpdatedPacket chunkRadiusUpdatePacket = new ChunkRadiusUpdatedPacket();
                        if (event.isCancelled()) {
                            // 取消：发送旧视距给客户端
                            chunkRadiusUpdatePacket.radius = this.chunkRadius;
                        } else {
                            // 使用插件设置的最终视距
                            this.chunkRadius = event.getRadius();
                            chunkRadiusUpdatePacket.radius = this.chunkRadius;
                        }
                        this.dataPacket(chunkRadiusUpdatePacket);
                    } else {
                        // 首次请求（登录过程中），直接应用
                        this.chunkRadius = requestedRadius;
                        ChunkRadiusUpdatedPacket chunkRadiusUpdatePacket = new ChunkRadiusUpdatedPacket();
                        chunkRadiusUpdatePacket.radius = this.chunkRadius;
                        this.dataPacket(chunkRadiusUpdatePacket);
                    }
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
                    if (isAdventure()) {
                        break;
                    }
                    ItemFrameDropItemPacket itemFrameDropItemPacket = (ItemFrameDropItemPacket) packet;
                    if (this.distanceSquared(itemFrameDropItemPacket.x, itemFrameDropItemPacket.y, itemFrameDropItemPacket.z) < 1000) {
                        BlockEntity itemFrame = this.level.getBlockEntityIfLoaded(itemFrameDropItemPacket.x, itemFrameDropItemPacket.y, itemFrameDropItemPacket.z);
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
                                    this.setDataFlag(DATA_FLAG_ACTION, false);

                                    if (this.canInteract(blockVector.add(0.5, 0.5, 0.5), this.isCreative() ? MAX_REACH_DISTANCE_CREATIVE : MAX_REACH_DISTANCE_SURVIVAL)) {
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
                                                if (i.getId() != 10000) {  // Hack
                                                    if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                                        inventory.setItemInHand(i);
                                                        inventory.sendHeldItem(this.getViewers().values());
                                                    }
                                                    break packetswitch;
                                                }
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

                                        if (door.isTop()) {
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

                                    if (this.canInteract(blockVector.add(0.5, 0.5, 0.5), this.isCreative() ? MAX_REACH_DISTANCE_CREATIVE : MAX_REACH_DISTANCE_SURVIVAL) && (i = this.level.useBreakOn(blockVector.asVector3(), i, this, true)) != null) {
                                        if (this.isSurvival()) {
                                            this.getFoodData().updateFoodExpLevel(0.005f);
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
                                        target = this.level.getBlock(blockVector);
                                        this.level.sendBlocks(new Player[]{this}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY);

                                        BlockEntity blockEntity = this.level.getBlockEntityIfLoaded(blockVector);
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
                                    if (isSpectator()) {
                                        interactEvent.setCancelled();
                                    }
                                    this.server.getPluginManager().callEvent(interactEvent);
                                    if (interactEvent.isCancelled()) {
                                        this.inventory.sendHeldItem(this);
                                        break packetswitch;
                                    }

                                    if (item.onClickAir(this, directionVector)) {
                                        if (this.isSurvivalLike()) {
                                            this.inventory.setItemInHand(item);
                                        }

                                        this.setUsingItem(item.canRelease());
                                    }

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
                                if (isSpectator()) {
                                    interactEvent.setCancelled();
                                }
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
                                    if (!this.canInteract(target, isCreative() ? MAX_REACH_DISTANCE_ENTITY_INTERACTION : 5)) {
                                        break;
                                    }

                                    PlayerInteractEntityEvent playerInteractEntityEvent = new PlayerInteractEntityEvent(this, target, item, useItemOnEntityData.clickPos);
                                    if (this.isSpectator()) playerInteractEntityEvent.setCancelled();
                                    getServer().getPluginManager().callEvent(playerInteractEntityEvent);

                                    if (playerInteractEntityEvent.isCancelled()) {
                                        break;
                                    }

                                    if (target.onInteract(this, item) && this.isSurvival()) {
                                        if (item.isTool()) {
                                            if (item.useOn(target) && item.getDamage() > item.getMaxDurability()) {
                                                item = Items.air();
                                                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BREAK);
                                            }
                                        } else {
                                            if (item.count > 1) {
                                                item.count--;
                                            } else {
                                                item = Items.air();
                                            }
                                        }

                                        this.inventory.setItemInHand(item);
                                    }
                                    break;
                                case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_ATTACK:
                                    if (!this.canInteract(target, isCreative() ? MAX_REACH_DISTANCE_ENTITY_INTERACTION : 5)) {
                                        break;
                                    } else if (target instanceof Player) {
                                        if ((((Player) target).getGamemode() & 0x01) > 0) {
                                            break;
                                        } else if (!this.server.getConfiguration().isPvp() || this.level.getDifficulty() == 0 || !level.getGameRules().getBoolean(GameRule.PVP)) {
                                            break;
                                        }
                                    }

                                    Enchantment[] enchantments = item.getId() != Item.ENCHANTED_BOOK ? item.getEnchantments() : Enchantment.EMPTY;

                                    ItemAttackDamageEvent event = new ItemAttackDamageEvent(item);
                                    this.server.getPluginManager().callEvent(event);

                                    float itemDamage = event.getAttackDamage();

                                    float damageBonus = 0;
                                    for (Enchantment enchantment : enchantments) {
                                        damageBonus += enchantment.getDamageBonus(this, target);
                                    }
                                    itemDamage += Mth.floor(damageBonus);

                                    Map<DamageModifier, Float> damage = new EnumMap<>(DamageModifier.class);
                                    damage.put(DamageModifier.BASE, itemDamage);

                                    float knockBackH = EntityDamageByEntityEvent.GLOBAL_KNOCKBACK_H;
                                    float knockBackV = EntityDamageByEntityEvent.GLOBAL_KNOCKBACK_V;
                                    int knockBackEnchantment = !item.is(Item.ENCHANTED_BOOK) ? item.getEnchantmentLevel(Enchantment.KNOCKBACK) : 0;
                                    if (knockBackEnchantment > 0) {
                                        knockBackH += knockBackEnchantment * 0.1f;
                                        knockBackV += knockBackEnchantment * 0.1f;
                                    }

                                    EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(this, target, DamageCause.ENTITY_ATTACK, damage, knockBackH, knockBackV, enchantments);
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

                                    for (Enchantment enchantment : enchantments) {
                                        enchantment.doPostAttack(item, this, target, null);
                                    }

                                    if (item.isTool() && this.isSurvivalLike()) {
                                        if (item.useOn(target)) {
                                            if (item.getDamage() > item.getMaxDurability()) {
                                                this.inventory.setItemInHand(Items.air());
                                                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BREAK);
                                            } else {
                                                this.inventory.setItemInHand(item);
                                            }
                                        } else {
                                            if (item.getId() == Item.AIR || this.inventory.getItemInHand().getId() == item.getId()) {
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

                                            Potion potion = Potion.getPotion(itemInHand.getDamage());

                                            if (this.getGamemode() == SURVIVAL) {
                                                --itemInHand.count;
                                                this.inventory.setItemInHand(itemInHand);
                                                this.inventory.addItem(Item.get(Item.GLASS_BOTTLE));
                                            }

                                            if (potion != null) {
                                                potion.applyPotion(this, itemInHand);
                                            }
                                        } else if (itemInHand.getId() == Item.MILK_BUCKET) {
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
                                                this.inventory.addItem(Item.get(Item.BUCKET));
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
                        settingsRequestEvent.getSettings().forEach((id, formWindow) -> {
                            ServerSettingsResponsePacket re = new ServerSettingsResponsePacket();
                            re.formId = id;
                            re.data = formWindow.getJSONData();
                            this.dataPacket(re);
                        });
                    }
                    break;
                case ProtocolInfo.BOOK_EDIT_PACKET:
                    BookEditPacket bookEditPacket = (BookEditPacket) packet;

                    if (isSpectator()) {
                        return;
                    }

                    if (isCreative()) {
                        // handled in InventoryTransactionPacket
                        return;
                    }

                    Item oldBook = this.inventory.getItem(bookEditPacket.inventorySlot);
                    if (oldBook.getId() != Item.WRITABLE_BOOK) {
                        return;
                    }

                    if (bookEditPacket.text != null && bookEditPacket.text.length() > ItemBookAndQuill.MAX_PAGE_LENGTH) {
                        this.getServer().getLogger().debug(this.getName() + ": BookEditPacket with too long text");
                        return;
                    }

                    if (bookEditPacket.pageNumber < 0 || bookEditPacket.pageNumber >= ItemBookAndQuill.MAX_PAGES) {
                        this.getServer().getLogger().debug(this.getName() + ": Invalid BookEditPacket page " + bookEditPacket.pageNumber);
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
                            if (bookEditPacket.title == null || bookEditPacket.author == null || bookEditPacket.xuid == null
                                    || bookEditPacket.title.isEmpty() || bookEditPacket.author.isEmpty()
                                    || bookEditPacket.title.length() > ItemBookWritten.MAX_TITLE_LENGTHE
                                    || bookEditPacket.author.length() > ItemBookWritten.MAX_AUTHOR_LENGTHE
                                    || bookEditPacket.xuid.length() > 64) {
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
                            this.inventory.setItem(bookEditPacket.inventorySlot, editBookEvent.getNewBook(), bookEditPacket.action != BookEditPacket.Action.SWAP_PAGES);
                        }
                    }
                    break;
                case ProtocolInfo.PASSENGER_JUMP_PACKET:
                    RiderJumpPacket riderJumpPacket = (RiderJumpPacket) packet;
                    new PlayerPassengerJumpEvent(this, riderJumpPacket.strength).call();
                    break;
                case ProtocolInfo.BOSS_EVENT_PACKET:
                    BossEventPacket bossEventPacket = (BossEventPacket) packet;
                    switch (bossEventPacket.type) {
                        case BossEventPacket.TYPE_QUERY:
                            DummyBossBar bossBar = dummyBossBars.get(bossEventPacket.bossEid);
                            if (bossBar != null) {
                                bossBar.reshow();
                            }
                            break;
                    }
                default:
                    break;
            }
        }
    }

    public void preChat(String message) {
        chat(message);
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

        if (this.messageCounter <= 0) {
            VIOLATION_LISTENER.onChatTooFast(this, message.length());
            return false;
        }

        if (message.length() > this.messageCounter * 512 + 1) {
            VIOLATION_LISTENER.onChatTooLong(this, message.length());
            return false;
        }

        if (this.removeFormat) {
            message = TextFormat.clean(message, true);
        }

        for (String msg : message.split("\n", this.messageCounter + 1)) {
            if (!msg.trim().isEmpty() && msg.length() <= 512 && this.messageCounter-- > 0) {
                PlayerChatEvent chatEvent = new PlayerChatEvent(this, msg);
                this.server.getPluginManager().callEvent(chatEvent);
                if (!chatEvent.isCancelled()) {
                    this.server.broadcastMessage(this.getServer().getLanguage().translate(chatEvent.getFormat(), chatEvent.getPlayer().getDisplayName(), new LiteralContainer(chatEvent.getMessage())), chatEvent.getRecipients());
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

    public boolean setViewDistance(int distance) {
        int viewDistance = Math.max(4, Math.min(distance, this.viewDistance));
        if (this.chunkRadius == viewDistance) {
            return false;
        }
        this.chunkRadius = viewDistance;

        ChunkRadiusUpdatedPacket pk = new ChunkRadiusUpdatedPacket();
        pk.radius = viewDistance;
        this.dataPacket(pk);
        return true;
    }

    public int getViewDistance() {
        return this.chunkRadius;
    }

    @Override
    public void sendMessage(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_RAW;
        pk.message = message;
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
        this.sendTranslation(message, new Object[0]);
    }

    public void sendTranslation(String message, Object[] parameters) {
        TextPacket pk = new TextPacket();
        if (!this.server.isLanguageForced()) {
            String translated = this.server.getLanguage().translateOnly("nukkit.", message, parameters);
            if (translated == null) {
                pk.type = TextPacket.TYPE_TRANSLATION;
                pk.isLocalized = true;
                pk.message = message;
                String[] params = new String[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    String param = String.valueOf(parameters[i]);
                    String translatedParam = this.server.getLanguage().translateOnly("nukkit.", param, parameters);
                    params[i] = translatedParam != null ? translatedParam : param;
                }
                pk.parameters = params;
            } else {
                pk.type = TextPacket.TYPE_RAW;
                pk.message = translated;
            }
        } else {
            pk.type = TextPacket.TYPE_RAW;
            pk.message = this.server.getLanguage().translate(message, parameters);
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
        pk.message = message;
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
        if (!subtitle.isEmpty()) {
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
            if (notify && !reason.isEmpty()) {
                sendDisconnectScreen(reason);
            }

            this.connected = false;
            PlayerQuitEvent ev = null;
            if (this.getName() != null && !this.getName().isEmpty()) {
                this.server.getPluginManager().callEvent(ev = new PlayerQuitEvent(this, message, true, reason));
                if (this.loggedIn && ev.getAutoSave()) {
                    this.save();
                }
                if (this.fishing != null) {
                    this.stopFishing(false);
                }
            }

            for (Player player : this.server.getOnlinePlayerList()) {
                if (!player.canSee(this)) {
                    player.showPlayer(this);
                }
            }

            this.hiddenPlayers = new Object2ObjectOpenHashMap<>();

            this.removeAllWindows(true);

            List<Player> needRemovePlayerListFrom = this.getServer().getOnlinePlayers().values().stream()
                    .filter(p -> p != this && p.sentSkins.contains(this.getUniqueId()))
                    .collect(Collectors.toList());
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
            log.info(this.getServer().getLanguage().translate("nukkit.player.logOut",
                    TextFormat.AQUA + (this.getName() == null ? "" : this.getName()) + TextFormat.WHITE,
                    this.getAddress(),
                    this.getPort(),
                    reason));
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
            this.namedTag.putInt("DimensionId", this.level.getDimension().getId());
            if (this.spawnPosition != null && this.spawnPosition.getLevel() != null) {
                this.namedTag.putString("SpawnLevel", this.spawnPosition.getLevel().getFolderName());
                this.namedTag.putInt("SpawnX", (int) this.spawnPosition.x);
                this.namedTag.putInt("SpawnY", (int) this.spawnPosition.y);
                this.namedTag.putInt("SpawnZ", (int) this.spawnPosition.z);
                this.namedTag.putInt("SpawnDimension", this.spawnPosition.getLevel().getDimension().getId());
            } else {
                this.namedTag.remove("SpawnLevel");
                this.namedTag.remove("SpawnX");
                this.namedTag.remove("SpawnY");
                this.namedTag.remove("SpawnZ");
                this.namedTag.remove("SpawnDimension");
            }
            if (this.spawnBlockPosition != null && this.spawnBlockPosition.level != null) {
                this.namedTag.putString("SpawnBlockPositionLevel", this.spawnBlockPosition.level.getFolderName());
                this.namedTag.putInt("SpawnBlockPositionX", this.spawnBlockPosition.getFloorX());
                this.namedTag.putInt("SpawnBlockPositionY", this.spawnBlockPosition.getFloorY());
                this.namedTag.putInt("SpawnBlockPositionZ", this.spawnBlockPosition.getFloorZ());
            } else {
/*
                this.namedTag.remove("SpawnBlockPositionLevel");
                this.namedTag.remove("SpawnBlockPositionX");
                this.namedTag.remove("SpawnBlockPositionY");
                this.namedTag.remove("SpawnBlockPositionZ");
*/
            }

            this.namedTag.putInt("playerGameType", this.gamemode);
            this.namedTag.putLong("lastPlayed", System.currentTimeMillis() / 1000);

            this.namedTag.putString("lastIP", this.getAddress());

            this.namedTag.putInt("EXP", this.getExperience());
            this.namedTag.putInt("expLevel", this.getExperienceLevel());

            this.namedTag.putInt("EnchantmentSeed", this.enchantmentSeed);

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
        String message = "";

        List<String> params;
        if (showMessages) {
            params = new ObjectArrayList<>();
            params.add(this.getDisplayName());

            EntityDamageEvent cause = this.getLastDamageCause();
            switch (cause == null ? DamageCause.CUSTOM : cause.getCause()) {
                case ENTITY_ATTACK:
                    if (cause instanceof EntityDamageByEntityEvent) {
                        Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                        killer = e;
                        if (e instanceof Player) {
                            message = "death.attack.player";
                            params.add(((Player) e).getDisplayName());
                        } else if (e instanceof EntityLiving) {
                            message = "death.attack.mob";
                            String nameTag = e.getNameTag();
                            params.add(!"".equals(nameTag) ? nameTag : e.getName());
                        } else {
                            params.add("Unknown");
                        }
                    }
                    break;
                case THORNS:
                    if (cause instanceof EntityDamageByEntityEvent) {
                        Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                        killer = e;
                        if (e instanceof Player) {
                            message = "death.attack.thorns";
                            params.add(((Player) e).getDisplayName());
                        } else if (e instanceof EntityLiving) {
                            message = "death.attack.thorns";
                            String nameTag = e.getNameTag();
                            params.add(!"".equals(nameTag) ? nameTag : e.getName());
                        } else {
                            params.add("Unknown");
                        }
                    }
                    break;
                case PROJECTILE:
                    if (cause instanceof EntityDamageByEntityEvent) {
                        Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                        int entityType;
                        if (cause instanceof EntityDamageByChildEntityEvent) {
                            Entity projectile = ((EntityDamageByChildEntityEvent) cause).getChild();
                            entityType = projectile.getNetworkId();
                        } else {
                            entityType = EntityID.ARROW;
                        }
                        killer = e;
                        if (e instanceof Player) {
                            switch (entityType) {
                                default:
                                case EntityID.ARROW:
                                    message = "death.attack.arrow";
                                    break;
                                case EntityID.THROWN_TRIDENT:
                                    message = "death.attack.trident";
                                    break;
                                case EntityID.SHULKER_BULLET:
                                    message = "death.attack.bullet";
                                    break;
                                case EntityID.LLAMA_SPIT:
                                    message = "death.attack.spit";
                                    break;
                                case EntityID.SMALL_FIREBALL:
                                case EntityID.FIREBALL:
                                    message = "death.attack.fireball";
                                    break;
                                case EntityID.SNOWBALL:
                                case EntityID.EGG:
                                case EntityID.ENDER_PEARL:
                                    message = "death.attack.thrown";
                                    break;
                            }
                            params.add(((Player) e).getDisplayName());
                        } else if (e instanceof EntityLiving) {
                            switch (entityType) {
                                default:
                                case EntityID.ARROW:
                                    message = "death.attack.arrow";
                                    break;
                                case EntityID.THROWN_TRIDENT:
                                    message = "death.attack.trident";
                                    break;
                                case EntityID.SHULKER_BULLET:
                                    message = "death.attack.bullet";
                                    break;
                                case EntityID.LLAMA_SPIT:
                                    message = "death.attack.spit";
                                    break;
                                case EntityID.SMALL_FIREBALL:
                                case EntityID.FIREBALL:
                                    message = "death.attack.fireball";
                                    break;
                                case EntityID.SNOWBALL:
                                case EntityID.EGG:
                                case EntityID.ENDER_PEARL:
                                    message = "death.attack.thrown";
                                    break;
                            }
                            String nameTag = e.getNameTag();
                            params.add(!"".equals(nameTag) ? nameTag : e.getName());
                        } else {
                            params.add("Unknown");
                        }
                    }
                    break;
                case VOID:
                    message = "death.attack.outOfWorld";
                    break;
                case FLY_INTO_WALL:
                    message = "death.attack.flyIntoWall";
                    break;
                case FALL:
                    if (cause.getFinalDamage() > 2) {
                        ObjectIntPair<EntityDamageByEntityEvent> playerDamage = getLastPlayerDamageCause(5 * 20);
                        if (playerDamage != null) {
                            Player damager = (Player) playerDamage.left().getDamager();
                            if (damager != this) {
                                message = "death.fell.assist";
                                params.add(damager.getDisplayName());
                                break;
                            }
                        }
                        message = "death.fell.accident.generic";
                        break;
                    }
                    message = "death.attack.fall";
                    break;
                case STALAGMITE:
                    message = "death.attack.stalagmite";
                    break;
                case SUFFOCATION:
                    message = "death.attack.inWall";
                    break;
                case LAVA:
                    ObjectIntPair<EntityDamageByEntityEvent> playerDamage = getLastPlayerDamageCause(5 * 20);
                    if (playerDamage != null) {
                        Player damager = (Player) playerDamage.left().getDamager();
                        if (damager != this) {
                            message = "death.attack.lava.player";
                            params.add(damager.getDisplayName());
                            break;
                        }
                    }
                    message = "death.attack.lava";
                    break;
                case MAGMA:
                    playerDamage = getLastPlayerDamageCause(5 * 20);
                    if (playerDamage != null) {
                        Player damager = (Player) playerDamage.left().getDamager();
                        if (damager != this) {
                            message = "death.attack.magma.player";
                            params.add(damager.getDisplayName());
                            break;
                        }
                    }
                    message = "death.attack.magma";
                    break;
                case FIRE:
                case CAMPFIRE:
                case SOUL_CAMPFIRE:
                    playerDamage = getLastPlayerDamageCause(5 * 20);
                    if (playerDamage != null) {
                        Player damager = (Player) playerDamage.left().getDamager();
                        if (damager != this) {
                            message = "death.attack.inFire.player";
                            params.add(damager.getDisplayName());
                            break;
                        }
                    }
                    message = "death.attack.inFire";
                    break;
                case FIRE_TICK:
                    playerDamage = getLastPlayerDamageCause(5 * 20);
                    if (playerDamage != null) {
                        Player damager = (Player) playerDamage.left().getDamager();
                        if (damager != this) {
                            message = "death.attack.onFire.player";
                            params.add(damager.getDisplayName());
                            break;
                        }
                    }
                    message = "death.attack.onFire";
                    break;
                case DROWNING:
                    playerDamage = getLastPlayerDamageCause(5 * 20);
                    if (playerDamage != null) {
                        Player damager = (Player) playerDamage.left().getDamager();
                        if (damager != this) {
                            message = "death.attack.drown.player";
                            params.add(damager.getDisplayName());
                            break;
                        }
                    }
                    message = "death.attack.drown";
                    break;
                case DEHYDRATION:
                    message = "death.attack.dehydration";
                    break;
                case CONTACT:
                    if (cause instanceof EntityDamageByBlockEvent) {
                        int id = ((EntityDamageByBlockEvent) cause).getDamager().getId();
                        if (id == Block.CACTUS) {
                            playerDamage = getLastPlayerDamageCause(5 * 20);
                            if (playerDamage != null) {
                                Player damager = (Player) playerDamage.left().getDamager();
                                if (damager != this) {
                                    message = "death.attack.cactus.player";
                                    params.add(damager.getDisplayName());
                                    break;
                                }
                            }
                            message = "death.attack.cactus";
                        } else if (id == Block.SWEET_BERRY_BUSH) {
                            message = "death.attack.sweetBerry";
                        }
                    }
                    break;
                case FALLING_BLOCK:
                    message = "death.attack.fallingBlock";
                    break;
                case ANVIL:
                    message = "death.attack.anvil";
                    break;
                case STALACTITE:
                    message = "death.attack.stalactite";
                    break;
                case BLOCK_EXPLOSION:
                case ENTITY_EXPLOSION:
                    if (cause instanceof EntityDamageByEntityEvent) {
                        Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                        killer = e;
                        if (e instanceof Player) {
                            message = "death.attack.explosion.player";
                            params.add(((Player) e).getDisplayName());
                            break;
                        } else if (e instanceof EntityLiving) {
                            message = "death.attack.explosion.player";
                            String nameTag = e.getNameTag();
                            params.add(!"".equals(nameTag) ? nameTag : e.getName());
                            break;
                        }
                    } else if (cause instanceof EntityDamageByBlockEvent) {
                        message = "death.attack.explosion.by.bed";
                        break;
                    }
                    message = "death.attack.explosion";
                    break;
                case FIREWORKS:
                    message = "death.attack.fireworks";
                    break;
                case MAGIC:
                    if (cause instanceof EntityDamageByEntityEvent) {
                        Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                        killer = e;
                        if (e instanceof Player) {
                            message = "death.attack.indirectMagic";
                            params.add(((Player) e).getDisplayName());
                            break;
                        } else if (e instanceof EntityLiving) {
                            message = "death.attack.indirectMagic";
                            String nameTag = e.getNameTag();
                            params.add(!"".equals(nameTag) ? nameTag : e.getName());
                            break;
                        }
                    }
                    message = "death.attack.magic";
                    break;
                case WITHER:
                    message = "death.attack.wither";
                    break;
                case SONIC_BOOM:
                    playerDamage = getLastPlayerDamageCause(5 * 20);
                    if (playerDamage != null) {
                        Player damager = (Player) playerDamage.left().getDamager();
                        if (damager != this) {
                            message = "death.attack.sonicBoom.player";
                            params.add(damager.getDisplayName());
                            break;
                        }
                    }
                    message = "death.attack.sonicBoom";
                    break;
                case LIGHTNING:
                    message = "death.attack.lightningBolt";
                    break;
                case HUNGER:
                    message = "death.attack.starve";
                    break;
                case FREEZE:
                    message = "death.attack.freeze";
                    break;
                case MACE_SMASH:
                    playerDamage = getLastPlayerDamageCause(5 * 20);
                    if (playerDamage != null) {
                        Player damager = (Player) playerDamage.left().getDamager();
                        if (damager != this) {
                            message = "death.attack.maceSmash.player";
                            params.add(damager.getDisplayName());
                            break;
                        }
                    }
                default:
                    message = "death.attack.generic";
                    break;
            }
        } else {
            params = Collections.emptyList();
        }

        float health = this.health;
        this.health = 0;

        PlayerDeathEvent ev = new PlayerDeathEvent(this, this.getDrops(), new TranslationContainer(message, params.toArray()), this.getExperienceLevel());
        ev.setKeepExperience(this.level.gameRules.getBoolean(GameRule.KEEP_INVENTORY));
        ev.setKeepInventory(ev.getKeepExperience());
        this.server.getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            this.health = health;
            return;
        }

        if (this.fishing != null) {
            this.stopFishing(false);
        }

        this.extinguish();
        this.scheduleUpdate();

        if (!ev.getKeepInventory() && this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            for (Item item : ev.getDrops()) {
                if (item.getId() == Item.ENCHANTED_BOOK || !item.hasEnchantment(Enchantment.VANISHING)) {
                    this.level.dropItem(this, item, null, true, 40);
                }
            }

            if (this.inventory != null) {
                for (int slot = 0; slot < inventory.getSize(); slot++) {
                    Item item = inventory.getItem(slot);
                    if (!item.isKeepOnDeath()) {
                        inventory.setItem(slot, Items.air());
                    }
                }
            }
            if (this.offhandInventory != null) {
                Item item = offhandInventory.getItem(0);
                if (!item.isKeepOnDeath()) {
                    offhandInventory.setItem(0, Items.air());
                }
            }
        }

        if (!ev.getKeepExperience() && this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            if (this.isSurvivalLike()) {
                int exp = ev.getExperience() * 7;
                if (exp > 100) exp = 100;
                this.getLevel().dropExpOrb(this, exp);
            }
            this.setExperience(0, 0);
        }

        if (showMessages && !ev.getDeathMessage().toString().isEmpty()) {
            this.server.broadcast(ev.getDeathMessage(), Server.BROADCAST_CHANNEL_USERS);

            this.sendDeathInfo(ev.getDeathMessage());
        }

        RespawnPacket pk = new RespawnPacket();
        Position pos = this.getSpawn();
        pk.x = (float) pos.x;
        pk.y = (float) pos.y;
        pk.z = (float) pos.z;
        this.dataPacket(pk);
    }

    protected void respawn() {
        if (this.server.isHardcore()) {
            this.setBanned(true);
            return;
        }

        this.craftingType = CRAFTING_SMALL;
        this.resetCraftingGridType();

        PlayerRespawnEvent playerRespawnEvent = new PlayerRespawnEvent(this, this.getSpawn());
        this.server.getPluginManager().callEvent(playerRespawnEvent);

        Position respawnPos = playerRespawnEvent.getRespawnPosition();
        initiateLegacyRespawn(respawnPos);

        this.sendExperience();
        this.sendExperienceLevel();

        this.setSprinting(false);
        this.setSneaking(false);
        this.setGliding(false);
        this.setSwimming(false);
        this.setCrawling(false);

        this.setDataProperty(new ShortEntityData(Player.DATA_AIR, 300), false);
        this.deadTicks = 0;
        this.noDamageTicks = 60;

        this.removeAllEffects();

        SetHealthPacket healthPacket = new SetHealthPacket();
        healthPacket.health = getMaxHealth();
        this.dataPacket(healthPacket);

        this.setHealth(this.getMaxHealth());
        this.getFoodData().setLevel(20, 20);

        this.sendData(this);
        this.sendData(this.getViewers().values().toArray(new Player[0]));

        this.setMovementSpeed(DEFAULT_SPEED);

        this.getAdventureSettings().update();
        this.inventory.sendContents(this);
        this.armorInventory.sendContents(this);
        this.offhandInventory.sendContents(this);

        this.teleport(respawnPos, null);
        this.spawnToAll();
        this.scheduleUpdate();
    }

    protected void initiateLegacyRespawn(Position respawnPos) {
        RespawnPacket respawnPacket = new RespawnPacket();
        respawnPacket.x = (float) respawnPos.x;
        respawnPacket.y = (float) respawnPos.y;
        respawnPacket.z = (float) respawnPos.z;
        this.dataPacket(respawnPacket);
    }

    @Override
    public boolean setHealth(float health) {
        if (health < 1) {
            health = 0;
        }

        if (!super.setHealth(health)) {
            return false;
        }

        if (!this.spawned) {
            return true;
        }

        this.setAttribute(Attribute.getAttribute(Attribute.HEALTH)
                .setMaxValue(this.getMaxHealth())
                .setValue(this.health > 0 ? (this.health < getMaxHealth() ? this.health : getMaxHealth()) : 0));
        return true;
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);

        if (!this.spawned) {
            return;
        }

        this.setAttribute(Attribute.getAttribute(Attribute.HEALTH)
                .setMaxValue(this.getMaxHealth())
                .setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0));
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
        this.setExperience(added, level, true);
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
        setExperience(exp, level, false);
    }

    public void setExperience(int exp, int level, boolean playSound) {
        level = Mth.clamp(level, 0, 24791);

        if (playSound) {
            int after = level / 5;
            if (after > expLevel / 5) {
                this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_LEVELUP, after <= 6 ? after * 2097152 : 12582911);
            }
        }

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

    public void setAttribute(Attribute... attribute) {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entries = attribute;
        pk.entityId = this.getId();
        this.dataPacket(pk);
    }

    @Override
    public void setMovementSpeed(float speed) {
        setMovementSpeed(speed, true);
    }

    public void setMovementSpeed(float speed, boolean send) {
        super.setMovementSpeed(speed);

        if (this.spawned && send) {
            movementSpeedAttribute.setValue(speed);
            movementSpeedAttribute.setDirty();
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
        if (this.isCreativeLike()) {
            source.setCancelled();
            return false;
        }
        if (this.getAdventureSettings().get(Type.ALLOW_FLIGHT) && source.getCause() == DamageCause.FALL) {
            //source.setCancelled();
            return false;
        }
        if (source.getCause() == DamageCause.FALL) {
            if (this.getLevel().getBlock(floor().add(0.5, -1, 0.5)).getId() == Block.SLIME) {
                //source.setCancelled();
                this.resetFallDistance();
                return false;
            }
        }

        if (isDamageBlocked(source) && blockUsingShield(source)) {
            return false;
        }

        boolean critical = false;
        if (source instanceof EntityDamageByEntityEvent event && source.getCause() == DamageCause.ENTITY_ATTACK) {
            Entity damager = event.getDamager();
            if (damager instanceof Player player) {
                player.getFoodData().updateFoodExpLevel(0.1f);
            }

            //Critical hit
            if (!damager.onGround && damager instanceof Player player && !damager.hasEffect(Effect.BLINDNESS) && !damager.isRiding() && !damager.isInsideOfWater(false)) {
                if (player.speed != null && player.speed.y > 0) {
                    if (player.attackCriticalThisJump <= 0) {
                        critical = true;
                        source.setDamage(source.getDamage() * 1.3f);
                    }
                }
            }
        }

        if (super.attack(source)) { //!source.isCancelled()
            this.stopSleep();

            // 攻击生效了
            if (this.getLastDamageCause() == source && this.spawned) {
                if (critical) {
                    if (((EntityDamageByEntityEvent) source).getDamager() instanceof Player player) {
                        player.attackCriticalThisJump++;
                    }

                    // 在这里发送暴击，因为事件可能被取消
                    AnimatePacket animate = new AnimatePacket();
                    animate.action = AnimatePacket.Action.CRITICAL_HIT;
                    animate.data = 55;
                    animate.eid = getId();
                    this.getLevel().addChunkPacket(this.getChunkX(), this.getChunkZ(), animate);

                    this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ATTACK_STRONG);
                }
            }
            return true;
        }
        return false;
    }

    protected boolean isDamageBlocked(EntityDamageEvent source) {
        if (!isBlocking()) {
            return false;
        }

        Vector3 damager;
        if (source instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) source;
            damager = event.getDamager();
        } else if (source instanceof EntityDamageByBlockEvent) {
            EntityDamageByBlockEvent event = (EntityDamageByBlockEvent) source;
            damager = event.getDamager();
        } else {
            return false;
        }

        DamageCause cause = source.getCause();
        switch (cause) {
            case BLOCK_EXPLOSION:
            case ENTITY_EXPLOSION:
                return true;
            case ENTITY_ATTACK:
                if (damager instanceof Entity) {
                    Entity entity = (Entity) damager;
                    if (entity.getNetworkId() == EntityID.GUARDIAN || entity.getNetworkId() == EntityID.ELDER_GUARDIAN) {
                        return false;
                    }
                }
                break;
            case PROJECTILE:
                break;
            case CONTACT:
                if (damager instanceof Entity && ((Entity) damager).getNetworkId() == EntityID.PUFFERFISH) {
                    break;
                }
            default:
                return false;
        }

        return damager.subtract(this).xz().dot(getDirectionVector()) > 0;
    }

    protected boolean blockUsingShield(EntityDamageEvent source) {
        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ITEM_SHIELD_BLOCK);

        if (source.getCause() == DamageCause.ENTITY_ATTACK && source instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
            damager.blockedByShield(this);

            if (damager.canDisableShield()) {
                shieldCooldown = server.getTick() + 5 * 20;
                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BREAK);
            }
        }

        setDataFlag(DATA_FLAG_BLOCKED_USING_SHIELD, true);

        if (isCreativeLike()) {
            return true;
        }

        Pair<Inventory, Item> itemStack = getCurrentActiveShield();
        if (itemStack != null) {
            float damage = source.getDamage();
            if (damage >= 3) {
                Item shield = itemStack.value();
                if (shield.hurtAndBreak(Mth.floor(damage) + 1) < 0) {
                    shield.pop();
                    level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BREAK);
                }
                if (itemStack.key().getType() == InventoryType.OFFHAND) {
                    offhandInventory.setItem(shield);
                } else {
                    inventory.setItemInHand(shield);
                }

                setDataFlag(DATA_FLAG_BLOCKED_USING_DAMAGED_SHIELD, true);
            }
        }

        if (source.getCause() == DamageCause.PROJECTILE && source instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
            EntityProjectile projectile = null;
            if (damager instanceof EntityProjectile) {
                projectile = (EntityProjectile) damager;
            } else if (source instanceof EntityDamageByChildEntityEvent) {
                Entity child = ((EntityDamageByChildEntityEvent) source).getChild();
                if (child instanceof EntityProjectile) {
                    projectile = (EntityProjectile) child;
                }
            }
            return projectile == null || projectile.getEntityHitCount() <= 1;
        }

        return true;
    }

    @Override
    public boolean isBlocking() {
        if (!getDataFlag(DATA_FLAG_BLOCKING)) {
            return false;
        }

        Pair<Inventory, Item> shield = getCurrentActiveShield();
        if (shield == null) {
            return false;
        }

        return server.getTick() - shieldBlockingTick > 5;
    }

    @Nullable
    public Pair<Inventory, Item> getCurrentActiveShield() {
        Item offhand = offhandInventory.getItem();
        if (offhand.getId() == Item.SHIELD) {
            return Pair.of(offhandInventory, offhand);
        }
        Item mainhand = inventory.getItemInHand();
        if (mainhand.getId() == Item.SHIELD) {
            return Pair.of(inventory, mainhand);
        }
        return null;
    }

    @Override
    public boolean canDisableShield() {
        return inventory.getItemInHand().isAxe();
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

        this.setDataFlag(DATA_FLAG_ACTION, false);
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
        pk.y = (float) (pos.y + this.getBaseOffset());
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
            if (!this.usedChunks.get(centerChunkIndex)) {
                if (this.teleportChunkLoaded) {
                    this.lastImmobile = this.isImmobile();
                }

                this.teleportChunkIndex = centerChunkIndex;
                this.teleportChunkLoaded = false;
            }

            for (int X = -1; X <= 1; ++X) {
                for (int Z = -1; Z <= 1; ++Z) {
                    long index = Level.chunkHash(chunkX + X, chunkZ + Z);
                    if (!this.usedChunks.get(index)) {
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
            if (to.level != null && from.getLevel() != to.getLevel()) { //Different level, update compass position
                SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
                pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
                Position spawn = to.getLevel().getSpawnLocation(Position::new);
                pk.x = spawn.getFloorX();
                pk.y = spawn.getFloorY();
                pk.z = spawn.getFloorZ();
                dataPacket(pk);
            }
        }

        //TODO Remove it! A hack to solve the client-side teleporting bug! (inside into the block)
        if (super.teleport(to.getY() == to.getFloorY() ? to.add(0, 0.00001, 0) : to, null)) { // null to prevent fire of duplicate EntityTeleportEvent
            this.removeAllWindows();

            postTeleport(false);
            return true;
        }

        return false;
    }

    protected void postTeleport(boolean synapse) {
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
    }

    protected void forceSendEmptyChunks() {
        this.forceSendEmptyChunks(this.chunkRadius);
    }

    protected void forceSendEmptyChunks(int chunkRadius) {
        this.forceSendEmptyChunks(chunkRadius, getFloorX(), getFloorZ());
    }

    protected void forceSendEmptyChunks(int chunkRadius, int centerChunkX, int centerChunkZ) {
        this.forceSendEmptyChunks(this.chunkRadius, centerChunkX, centerChunkZ, level.getDimension().getId());
    }

    protected void forceSendEmptyChunks(int chunkRadius, int centerChunkX, int centerChunkZ, int dimension) {
        List<FullChunkDataPacket> pkList = new ObjectArrayList<>();
        for (int x = -chunkRadius; x < chunkRadius; x++) {
            for (int z = -chunkRadius; z < chunkRadius; z++) {
                FullChunkDataPacket chunk = new FullChunkDataPacket();
                chunk.chunkX = centerChunkX + x;
                chunk.chunkZ = centerChunkZ + z;
                chunk.data = new byte[0];
                pkList.add(chunk);
            }
        }
        pkList.sort(AroundChunkComparator.create(centerChunkX, centerChunkZ));
        pkList.forEach(this::dataPacket);
    }

    public void teleportImmediate(Location location) {
        this.teleportImmediate(location, TeleportCause.PLUGIN);
    }

    public void teleportImmediate(Location location, TeleportCause cause) {
        Location from = this.getLocation();
        if (super.teleport(location, cause)) {
            this.removeAllWindows();

            if (from.getLevel() != location.getLevel()) { //Different level, update compass position
                SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
                pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
                Position spawn = location.getLevel().getSpawnLocation(Position::new);
                pk.x = spawn.getFloorX();
                pk.y = spawn.getFloorY();
                pk.z = spawn.getFloorZ();
                dataPacket(pk);

                this.getLevel().sendTime(this);
                this.getLevel().sendWeather(this);
            }

            this.forceMovement = new Vector3(this.x, this.y, this.z);
            this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESPAWN);

            this.resetFallDistance();
            this.orderChunks();
            this.nextChunkOrderRun = 0;
            this.newPosition = null;

            this.getDummyBossBars().values().forEach(DummyBossBar::reshow);
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
//        requestCloseFormWindow();

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
        DummyBossBar bossBar = DummyBossBar.builder(this).text(text).length(length).build();
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
        DummyBossBar bossBar = this.dummyBossBars.get(bossBarId);
        if (bossBar != null) {
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
        DummyBossBar bossBar = this.dummyBossBars.remove(bossBarId);
        if (bossBar != null) {
            bossBar.destroy();
        }
    }

    public int getWindowId(Inventory inventory) {
        Integer id = this.windows.get(inventory);
        if (id != null) {
            return id;
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
        Integer index = this.windows.get(inventory);
        if (index != null) {
            return index;
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
        Integer id = this.windows.get(inventory);
        if (id != null) {
            this.windows.remove(this.windowIndex.remove(id));
        }
    }

    public void sendAllInventories() {
        for (Inventory inv : this.windowIndex.values()) {
            inv.sendContents(this);
        }
    }

    protected void addDefaultWindows() {
        this.addWindow(this.getInventory(), ContainerIds.INVENTORY, true, true);
        this.addWindow(this.armorInventory, ContainerIds.ARMOR, true, true);

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
            for (Item drop : drops) {
                this.dropItem(drop);
            }

            drops = this.inventory.addItem(this.getCursorInventory().getItem(0));
            for (Item drop : drops) {
                this.dropItem(drop);
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

    public void resetClosingWindowId(int windowId) {
        if (this.closingWindowId != windowId) {
            return;
        }
        this.closingWindowId = Integer.MIN_VALUE;
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
    public int getLoaderId() {
        return this.loaderId;
    }

    @Override
    public boolean isLoaderActive() {
        return this.isConnected();
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

    public void setCheckMovement(boolean checkMovement) {
        this.checkMovement = checkMovement;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return this.locale;
    }

    @Override
    public boolean setSprinting(boolean value) {
        if (super.setSprinting(value)) {
            if (value) {
                movementSpeedAttribute.addModifier(AttributeModifiers.SPRINTING_BOOST);
            } else {
                movementSpeedAttribute.removeModifier(AttributeModifiers.SPRINTING_BOOST.getId());
            }
            return true;
        }
        return false;
    }

    public void transfer(InetSocketAddress address) {
        InetAddress addr = address.getAddress();
        if (addr == null) {
            log.warn("Invalid transfer address {}", address);
            return;
        }
        String hostName = addr.getHostAddress();
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
                EntityArrow arrow = (EntityArrow) entity;
                Item item = arrow.getItem();

                Item offhandItem = offhandInventory.getItem();
                boolean offhand = offhandItem.getId() == Item.ARROW && offhandItem.getCount() < offhandItem.getMaxStackSize() && item.equals(offhandItem);

                if (!this.isCreative() && !offhand && !this.inventory.canAddItem(item)) {
                    return false;
                }

                InventoryPickupArrowEvent ev = new InventoryPickupArrowEvent(offhand ? offhandInventory : this.inventory, arrow);

                int pickupMode = arrow.getPickupMode();
                if (pickupMode == EntityArrow.PICKUP_NONE) {
                    ev.setCancelled();
                }

                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return false;
                }

                if (pickupMode != EntityArrow.PICKUP_CREATIVE) {
                    TakeItemEntityPacket pk = new TakeItemEntityPacket();
                    pk.entityId = this.getId();
                    pk.target = entity.getId();
                    Server.broadcastPacket(entity.getViewers().values(), pk);
                    this.dataPacket(pk);

                    ev.getInventory().addItem(item);
                } else {
                    level.addLevelEvent(entity, LevelEventPacket.EVENT_SOUND_INFINITY_ARROW_PICKUP, (int) ((ThreadLocalRandom.current().nextGaussian() * 0.7 + 1) * 2 * 1000));
                }

                entity.close();
                return true;
            } else if (entity instanceof EntityThrownTrident trident && trident.hadCollision && !trident.hasLoyalty()) {
                Item item = trident.getItem();
                if (!this.isCreative() && !this.inventory.canAddItem(item)) {
                    return false;
                }

                InventoryPickupTridentEvent ev = new InventoryPickupTridentEvent(this.inventory, trident);

                int pickupMode = trident.getPickupMode();
                if (pickupMode == EntityThrownTrident.PICKUP_NONE) {
                    ev.setCancelled();
                }

                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return false;
                }

                if (pickupMode != EntityThrownTrident.PICKUP_CREATIVE && !this.isCreative()) {
                    TakeItemEntityPacket pk = new TakeItemEntityPacket();
                    pk.entityId = this.getId();
                    pk.target = entity.getId();
                    Server.broadcastPacket(entity.getViewers().values(), pk);
                    this.dataPacket(pk);

                    ev.getInventory().addItem(item);
                } else {
                    level.addLevelEvent(entity, LevelEventPacket.EVENT_SOUND_INFINITY_ARROW_PICKUP, (int) ((ThreadLocalRandom.current().nextGaussian() * 0.7 + 1) * 2 * 1000));
                }

                entity.close();
                return true;
            } else if (entity instanceof EntityItem itemEntity) {
                if (itemEntity.getPickupDelay() <= 0) {
                    Item item = itemEntity.getItem();

                    if (item != null) {
                        Item offhandItem = offhandInventory.getItem();
                        boolean offhand = !offhandItem.isNull() && offhandItem.getCount() < offhandItem.getMaxStackSize() && item.equals(offhandItem);

                        Item copy = item.clone();
                        copy.setCount(1);
                        if (!this.isCreative() && !offhand && !this.inventory.canAddItem(copy)) {
                            return false;
                        }
                        copy.setCount(item.getCount());

                        InventoryPickupItemEvent ev = new InventoryPickupItemEvent(offhand ? offhandInventory : this.inventory, itemEntity);
                        this.server.getPluginManager().callEvent(ev);
                        if (ev.isCancelled()) {
                            return false;
                        }

                        Item[] drops = ev.getInventory().addItem(copy);
                        if (drops.length != 0 && !isCreative()) {
                            item.setCount(drops[0].getCount());
                        } else {
                            TakeItemEntityPacket pk = new TakeItemEntityPacket();
                            pk.entityId = this.getId();
                            pk.target = entity.getId();
                            Server.broadcastPacket(entity.getViewers().values(), pk);
                            this.dataPacket(pk);

                            entity.close();
                        }
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
                entity.close();
                this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_EXPERIENCE_ORB);
                pickedXPOrb = tick;

                if (exp > 0) {
                    IntList itemsWithMending = new IntArrayList(4 + 2);
                    for (int i = 0; i < 4; i++) {
                        Item item = armorInventory.getItem(i);
                        if (!(item instanceof ItemDurable)) {
                            continue;
                        }
                        if (item.getDamage() <= 0) {
                            continue;
                        }
                        int mending = armorInventory.getItem(i).getEnchantmentLevel(Enchantment.MENDING);
                        if (mending > 0) {
                            itemsWithMending.add(-i - 1);
                        }
                    }
                    Item item = inventory.getItemInHand();
                    if (item instanceof ItemDurable && item.getDamage() > 0) {
                        int mending = item.getEnchantmentLevel(Enchantment.MENDING);
                        if (mending > 0) {
                            itemsWithMending.add(inventory.getHeldItemIndex());
                        }
                    }
                    Item offhandItem = offhandInventory.getItem(0);
                    if (offhandItem instanceof ItemDurable && offhandItem.getDamage() > 0) {
                        int mending = offhandItem.getEnchantmentLevel(Enchantment.MENDING);
                        if (mending > 0) {
                            itemsWithMending.add(-106);
                        }
                    }
                    if (!itemsWithMending.isEmpty()) {
                        int index = itemsWithMending.getInt(ThreadLocalRandom.current().nextInt(itemsWithMending.size()));
                        Inventory inv = inventory;
                        if (index == -106) {
                            inv = offhandInventory;
                            index = 0;
                        } else if (index < 0) {
                            inv = armorInventory;
                            index = -index - 1;
                        }
                        Item mend = inv.getItem(index);
                        int oldDamage = mend.getDamage();
                        int newDamage = Math.max(oldDamage - exp * 2, 0);
                        PlayerItemMendEvent event = new PlayerItemMendEvent(this, mend, xpOrb, oldDamage - newDamage);
                        event.call();
                        if (!event.isCancelled() && event.getRepairAmount() > 0) {
                            newDamage = Math.max(oldDamage - event.getRepairAmount(), 0);
                            exp -= (oldDamage - newDamage) / 2;
                            mend.setDamage(newDamage);
                            inv.setItem(index, mend);
                        }
                    }
                }

                if (exp > 0) {
                    new PlayerExpChangeEvent(this, exp).call();
                }
                this.addExperience(exp);
                return true;
            }
        }

        return false;
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
        if (isSpectator()) {
            return;
        }

        Vector3 motion = this.getDirectionVector().multiply(2);
        CompoundTag nbt = Entity.getDefaultNBT(x + motion.x * 0.5, y + this.getEyeHeight() + motion.y * 0.5, z + motion.z * 0.5, motion, (float) yaw, (float) pitch);
        EntityFishingHook fishingHook = new EntityFishingHook(getChunk(), nbt, this);
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
            this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_THROW, EntityFullNames.PLAYER);
        }
    }

    /**
     * Stop fishing
     * @param click clicked or forced
     */
    public int stopFishing(boolean click) {
        int itemDamage = 0;
        if (this.fishing != null && click) {
            itemDamage = fishing.reelLine();
        } else if (this.fishing != null) {
            this.fishing.close();
        }

        this.fishing = null;
        return itemDamage;
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
    public boolean setImmobile(boolean value) {
        if (this.teleportChunkLoaded) {
            this.lastImmobile = value;
        }
        return super.setImmobile(value);
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
    public void sendSubChunks(int dimension, int x, int z, int subChunkCount, ChunkCachedData cachedData, Map<StaticVersion, byte[][]> payload, byte[] heightMapType, byte[][] heightMap) {
    }

    /**
     * 发送缓存在内存的子区块.
     * @since 1.18.0
     */
    public void sendSubChunks(int dimension, int x, int z, int subChunkCount, ChunkCachedData cachedData, byte[] heightMapType, byte[][] heightMap) {
    }

    public void onSubChunkRequestFail(int dimension, int x, int z) {
    }

    protected boolean hasSubChunkRequest() {
        return false;
    }

    protected void processSubChunkRequest() {
    }

    protected boolean onLevelSwitch(Level oldLevel, Level newLevel) {
        return false;
    }

    protected void onDimensionChangeSuccess() {
    }

    public int getDummyDimension() {
        return level.getDimension().getId();
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
    public void spawnParticleEffect(Vector3f position, String identifier, long entityUniqueId, @Nullable String molangVariables) {
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
     * @since 1.18.0
     */
    public void updateSubChunkBlocks(int subChunkBlockX, int subChunkBlockY, int subChunkBlockZ, BlockChangeEntry[] layer0, BlockChangeEntry[] layer1) {
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

    protected static boolean validateFloat(float value) {
        return Float.isFinite(value);
    }

    protected static boolean validateCoordinate(float value) {
        return -30_000_000 <= value && value <= 30_000_000;
    }

    protected static boolean validateVehicleInput(float value) {
//        return -1 <= value && value <= 1;
//        return -1.1f <= value && value <= 1.1f; //client precision bug??? (non-Xbox gamepad: authInput.moveVecY = -1.000_000_1)
        return validateFloat(value);
    }

    public void onPacketViolation(PacketViolationReason reason) {
        onPacketViolation(reason, "");
    }

    public void onPacketViolation(PacketViolationReason reason, String tag) {
        onPacketViolation(reason, tag, "");
    }

    public void onPacketViolation(PacketViolationReason reason, String tag, String context) {
        violated = true;

        PlayerServerboundPacketViolationEvent event = new PlayerServerboundPacketViolationEvent(this, reason, tag, context);
        event.call();

        if (event.isKick()) {
            if (isClosed()) {
                return;
            }
            String message = event.getMessage();
            kick(Reason.PACKET_VIOLATION, message != null ? message : reason.toString(), false);
        } else {
            violation = 0;
            violated = false;
        }
    }

    protected void checkViolation() {
        if (BREAKPOINT_DEBUGGING) {
            return;
        }

        if (violated) {
            onPacketViolation(PacketViolationReason.VIOLATION_OVER_THRESHOLD, "insta");
            return;
        }

        if (violation <= 0) {
            return;
        }

        if (violation >= VIOLATION_THRESHOLD) {
            onPacketViolation(PacketViolationReason.VIOLATION_OVER_THRESHOLD, "tick");
        }

        violation--;
    }

    public int getViolationLevel() {
        return violation;
    }

    public void addViolationLevel(int delta, String reason) {
        violation += delta;
        violationRecords.offer(new PlayerViolationRecord(Server.getInstance().getTick(), reason, delta));
    }

    public void resetViolationState() {
        violation = 0;
        violated = false;
    }

    public boolean isViolated() {
        return violated;
    }

    public void setViolated(String reason) {
        violated = true;
        asyncViolationRecord = new PlayerViolationRecord(Server.getInstance().getTick(), reason, 0);
    }

    public Queue<PlayerViolationRecord> getViolationRecords() {
        return violationRecords;
    }

    @Nullable
    public PlayerViolationRecord getAsyncViolationRecord() {
        return asyncViolationRecord;
    }

    public static void setViolationListener(PlayerViolationListener listener) {
        Objects.requireNonNull(listener, "listener");
        VIOLATION_LISTENER = listener;
    }

    /**
     * @since 1.16.0
     */
    public boolean isServerAuthoritativeMovementEnabled() {
        return false;
    }

    /**
     * @since 1.16.0
     */
    public boolean isServerAuthoritativeInventoryEnabled() {
        return false;
    }

    /**
     * @since 1.19.80
     */
    public void openSignEditor(int x, int y, int z) {
        openSignEditor(x, y, z, true);
    }

    /**
     * @since 1.19.80
     */
    public void openSignEditor(int x, int y, int z, boolean front) {
    }

    /**
     * @since 1.16.0
     */
    public void playEmote(String emoteId, long entityRuntimeId, int flags) {
    }

    /**
     * @since 1.21.30
     */
    public void playEmote(String emoteId, long entityRuntimeId, int flags, int emoteTicks) {
    }

    /**
     * @since 1.19.10
     */
    protected void sendDeathInfo(TextContainer message) {
    }

    /**
     * @since 1.16.100
     */
    public void sendMotionPredictionHints(long entityRuntimeId, float motionX, float motionY, float motionZ, boolean onGround) {
    }

    @Nullable
    public StaticVersion getBlockVersion() {
        return blockVersion;
    }

    @Beta
    public Attribute getMovementSpeedAttribute() {
        return movementSpeedAttribute;
    }

    private void syncAttributeMap() {
        if (movementSpeedAttribute.isDirty()) {
            setAttribute(movementSpeedAttribute.copy().setValue(movementSpeedAttribute.getModifiedValue()));
        }
    }

    @Override
    protected void setFreezeEffectStrength(float freezeEffectStrength) {
        super.setFreezeEffectStrength(freezeEffectStrength);

        if (freezeEffectStrength <= 0) {
            movementSpeedAttribute.removeModifier(AttributeModifiers.FREEZE_EFFECT_UUID);
            return;
        }

        movementSpeedAttribute.replaceModifier(AttributeModifiers.freezeEffect(freezeEffectStrength));
    }

    public void sendDisconnectScreen(@Nullable String message) {
        sendDisconnectScreen(DisconnectPacket.REASON_DISCONNECTED, message);
    }

    public void sendDisconnectScreen(int reason, @Nullable String message) {
        DisconnectPacket packet = new DisconnectPacket();
        packet.reason = reason;
        if (message != null) {
            packet.message = message;
        } else {
            packet.hideDisconnectionScreen = true;
        }
        dataPacket(packet);
    }

    /**
     * @see EffectID
     * @since 1.11.0
     */
    public void playOnScreenEffectAnimation(int effectId) {
    }

    /**
     * @since 1.19.50
     */
    public void lockInput() {
        lockInput(InputLock.ALL);
    }

    /**
     * @since 1.19.50
     */
    public void lockInput(boolean movement) {
        lockInput(movement, false);
    }

    /**
     * @since 1.19.50
     */
    public void lockInput(boolean movement, boolean rotation) {
    }

    /**
     * @see InputLock
     * @since 1.19.50
     */
    public void lockInput(int flags) {
    }

    /**
     * @since 1.19.50
     */
    public void unlockInput() {
        lockInput(InputLock.NONE);
    }

    public void openBlockEditor(int x, int y, int z, int type) {
        ContainerOpenPacket packet = new ContainerOpenPacket();
        packet.type = type;
        packet.x = x;
        packet.y = y;
        packet.z = z;
        packet.windowId = Math.max(FIRST_AVAILABLE_WINDOW_ID, ++windowCnt % 99);
        dataPacket(packet);
    }

    /**
     * @since 1.20.60
     */
    public void hideHud() {
    }

    /**
     * @see HudElement
     * @since 1.20.60
     */
    public void hideHudElements(int... elements) {
    }

    /**
     * @since 1.20.60
     */
    public void showHud() {
    }

    /**
     * @see HudElement
     * @since 1.20.60
     */
    public void showHudElements(int... elements) {
    }

    public void swingArm() {
        swingArm(SwingSource.EVENT);
    }

    public void swingArm(SwingSource swingSource) {
        AnimatePacket pk = new AnimatePacket();
        pk.eid = getId();
        pk.action = AnimatePacket.Action.SWING_ARM;
        pk.swingSource = swingSource;
        dataPacket(pk);
        Server.broadcastPacket(getViewers().values(), pk);
    }

    public int getMaxViewDistance() {
        return server.getViewDistance();
    }

    @Override
    public Origin getCommandOrigin() {
        return Origin.PLAYER;
    }

    /**
     * @see Fogs
     * @since 1.16.100
     */
    public void sendFogStack(String... fogStack) {
    }

    /**
     * @since 1.21.2
     */
    public void requestCloseFormWindow() {
    }

    /**
     * @since 1.19.40
     */
    public void sendEntityPropertyInt(Entity entity, String propertyName, int value) {
    }

    /**
     * @since 1.19.40
     */
    public void sendEntityPropertyFloat(Entity entity, String propertyName, float value) {
    }

    /**
     * @since 1.19.40
     */
    public void sendEntityPropertyBool(Entity entity, String propertyName, boolean value) {
    }

    /**
     * @since 1.19.40
     */
    public void sendEntityPropertyEnum(Entity entity, String propertyName, String value) {
    }

    /**
     * @since 1.21.70
     */
    public void setEntityPropertyOverride(Entity entity, String propertyName, int value) {
    }

    /**
     * @since 1.21.70
     */
    public void setEntityPropertyOverride(Entity entity, IntEntityProperty property, int value) {
    }

    /**
     * @since 1.21.70
     */
    public void setEntityPropertyOverride(Entity entity, String propertyName, float value) {
    }

    /**
     * @since 1.21.70
     */
    public void setEntityPropertyOverride(Entity entity, FloatEntityProperty property, float value) {
    }

    /**
     * @since 1.21.70
     */
    public void setEntityPropertyOverride(Entity entity, String propertyName, boolean value) {
    }

    /**
     * @since 1.21.70
     */
    public void setEntityPropertyOverride(Entity entity, BooleanEntityProperty property, boolean value) {
    }

    /**
     * @since 1.21.70
     */
    public void setEntityPropertyOverride(Entity entity, String propertyName, String value) {
    }

    /**
     * @since 1.21.70
     */
    public void setEntityPropertyOverride(Entity entity, EnumEntityProperty property, String value) {
    }

    /**
     * @since 1.21.70
     */
    public void removeEntityPropertyOverride(Entity entity, String propertyName) {
    }

    /**
     * @since 1.21.70
     */
    public void removeEntityPropertyOverride(Entity entity, EntityProperty property) {
    }

    /**
     * @since 1.21.70
     */
    public void removeEntityPropertyOverrides(Entity entity) {
    }

    /**
     * @see MovementEffectType
     * @since 1.21.40
     */
    public void sendMovementEffect(long entityRuntimeId, int type, int duration) {
    }

    public void sendContainerData(Inventory inventory, int propertyId, int value) {
        int windowId = getWindowId(inventory);
        if (windowId <= 0) {
            return;
        }

        ContainerSetDataPacket packet = new ContainerSetDataPacket();
        packet.windowId = windowId;
        packet.property = propertyId;
        packet.value = value;
        dataPacket(packet);
    }

    public int getEnchantmentSeed() {
        return enchantmentSeed;
    }

    public void setEnchantmentSeed(int seed) {
        this.enchantmentSeed = seed;
    }

    /**
     * @since 1.16.0
     */
    public void sendEnchantingTableOptions(List<EnchantmentOption> options) {
    }

    /**
     * @since 1.21.80
     */
    public void updateLocator(long entityUniqueId, float x, float y, float z) {
    }

    /**
     * @since 1.21.80
     */
    public void hideLocator(long entityUniqueId) {
    }

    public boolean isLocatorBarEnabled() {
        return false;
    }

    protected void updateLocatorBar() {
        if (!level.gameRules.getBoolean(GameRule.LOCATOR_BAR)) {
            return;
        }

        Item helmet;
        if (!isAlive() || isSpectator() || hasEffect(Effect.INVISIBILITY) || isSneaking() || (helmet = armorInventory.getHelmet()).is(Item.SKULL) || helmet.is(ItemBlockID.CARVED_PUMPKIN)) {
            if (!hiddenLocator) {
                hiddenLocator = true;

                for (Player player : level.getPlayers().values()) {
                    if (player == this) {
                        continue;
                    }
                    player.hideLocator(getId());
                }
            }
            return;
        }
        hiddenLocator = false;

        Vector3f currentPos = asVector3f();
        if (locatorPos.equalsVec(currentPos)) {
            return;
        }
        locatorPos.setComponents(currentPos);

        for (Player player : level.getPlayers().values()) {
            if (player == this) {
                continue;
            }
            player.updateLocator(getId(), currentPos.x, currentPos.y, currentPos.z);
        }
    }

    /**
     * @since 1.6
     */
    public void setSoftEnum(String enumName, String... values) {
    }

    /**
     * @since 1.6
     */
    public void addSoftEnum(String enumName, String... values) {
    }

    /**
     * @since 1.6
     */
    public void removeSoftEnum(String enumName, String... values) {
    }

    /**
     * @since 1.21.80
     */
    public void setControlScheme(int scheme) {
    }

    /**
     * @since 1.21.80
     */
    public void resetControlScheme() {
    }

    /**
     * @since 1.21.120
     */
    public void setSkyZenithColor(Color color, String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.120
     */
    public void resetSkyZenithColor(String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void setSkyHorizonColor(Color color, String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void resetSkyHorizonColor(String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void setHorizonBlendMin(float blendMin, String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void resetHorizonBlendMin(String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void setHorizonBlendMax(float blendMax, String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void resetHorizonBlendMax(String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void setHorizonBlendStart(float blendStart, String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void resetHorizonBlendStart(String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void setHorizonBlendMieStart(float blendMieStart, String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void resetHorizonBlendMieStart(String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void setRayleighStrength(float rayleighStrength, String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void resetRayleighStrength(String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void setSunMieStrength(float sunMieStrength, String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void resetSunMieStrength(String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void setMoonMieStrength(float moonMieStrength, String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void resetMoonMieStrength(String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void setSunGlareShape(float sunGlareShape, String... biomeIdentifiers) {
    }

    /**
     * @since 1.21.130
     */
    public void resetSunGlareShape(String... biomeIdentifiers) {
    }
}
