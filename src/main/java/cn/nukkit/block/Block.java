package cn.nukkit.block;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.state.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.Position;
import cn.nukkit.math.*;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.JsonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public abstract class Block extends Position implements Metadatable, Cloneable, AxisAlignedBB, BlockID {

    // Nukkit runtime definitions
    public static final int CUSTOM_BLOCK_FIRST_ID = 1000;
    /**
     * @since 1.20.50
     */
    public static final int CUSTOM_BLOCK_FIRST_ID_NEW = 10000;
    public static final int BLOCK_ID_COUNT = Mth.smallestEncompassingPowerOfTwo(UNDEFINED + CUSTOM_BLOCK_FIRST_ID_NEW);
    public static final int BLOCK_ID_MASK = BLOCK_ID_COUNT - 1;
    public static final int BLOCK_META_COUNT = Mth.smallestEncompassingPowerOfTwo(5469); // wtf cobblestone_wall
    public static final int BLOCK_META_MASK = BLOCK_META_COUNT - 1;
    public static final int BLOCK_META_BITS = Mth.log2PowerOfTwo(BLOCK_META_COUNT);
    public static final int FULL_BLOCK_COUNT = BLOCK_ID_COUNT << BLOCK_META_BITS;
    public static final int FULL_BLOCK_MASK = FULL_BLOCK_COUNT - 1;
    public static final int FULL_BLOCK_ID_MASK = BLOCK_ID_MASK << BLOCK_META_BITS;
    public static final int CUSTOM_BLOCK_CAPACITY = BLOCK_ID_COUNT - CUSTOM_BLOCK_FIRST_ID_NEW;

    private static boolean initialized;
    public static final Class<? extends Block>[] list = new Class[BLOCK_ID_COUNT];
    public static final Block[][] variantList = new Block[BLOCK_ID_COUNT][];
    public static final int[] metaMax = new int[BLOCK_ID_COUNT];
    public static final int[] metaMask = new int[BLOCK_ID_COUNT];
    /**
     * if a block has can have variants
     */
    public static final boolean[] hasMeta = new boolean[BLOCK_ID_COUNT];

    public static final byte[] light = new byte[BLOCK_ID_COUNT];
    public static final byte[] lightBlock = new byte[BLOCK_ID_COUNT];
    public static final boolean[] lightBlocking = new boolean[BLOCK_ID_COUNT];
    public static final byte[] lightFilter = new byte[BLOCK_ID_COUNT];
    public static final boolean[] solid = new boolean[BLOCK_ID_COUNT];
    public static final boolean[] transparent = new boolean[BLOCK_ID_COUNT];

    private int meta;

    protected Block() {
        this(0);
    }

    protected Block(int meta) {
        this.meta = meta;
    }

    protected void init(int meta) {
        setDamage(meta);
    }

    @SuppressWarnings("unchecked")
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        log.debug("Custom block capacity: {}", CUSTOM_BLOCK_CAPACITY);

        Object2IntMap<String> metaTable; // auto-generated from development client
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("block_meta_table.json");
             InputStreamReader reader = new InputStreamReader(stream)) {
            metaTable = new Gson().fromJson(reader, Object2IntOpenHashMap.class);
        } catch (NullPointerException | IOException e) {
            throw new AssertionError("Unable to load block_meta_table.json", e);
        }

//        metaTable.put(String.valueOf(LEAVES2), 15); //TODO: HACK

        for (Object2IntMap.Entry<String> entry : metaTable.object2IntEntrySet()) {
            int id;
            try {
                id = Integer.parseInt(entry.getKey());
            } catch (NumberFormatException e) {
                throw new AssertionError("Invalid block_meta_table.json", e);
            }

            if (id >= UNDEFINED) {
                log.trace("Skip unsupported block: {}", id);
                continue;
            }

            int maxMeta = entry.getIntValue();
            if (maxMeta == 0) {
                metaMax[id] = 0;
                metaMask[id] = 0;
                variantList[id] = new Block[1];
            } else {
                int count = Mth.smallestEncompassingPowerOfTwo(maxMeta + 1);
                metaMax[id] = maxMeta;
                metaMask[id] = count - 1;
                variantList[id] = new Block[count];
                hasMeta[id] = true;
            }
        }

        Blocks.registerVanillaBlocks();

        BlockEntry[] propertiesTable; // auto-generated
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("block_properties_table.json")) {
            propertiesTable = JsonUtil.TRUSTED_JSON_MAPPER.readValue(stream, BlockEntry[].class);
        } catch (NullPointerException | IOException e) {
            throw new AssertionError("Unable to load block_properties_table.json", e);
        }

        Arrays.fill(lightBlock, (byte) 15);

        for (BlockEntry entry : propertiesTable) {
            assert entry.mId >= 0;

            int id = entry.mId;
            if (id >= UNDEFINED) {
                log.trace("Skip unsupported block entry: {} ({})", entry.nameId, id);
                continue;
            }

            lightBlock[id] = entry.mLightBlock;
        }

        for (int id = 0; id < BLOCK_ID_COUNT; id++) {
            Class<? extends Block> c = list[id];
            if (c != null) {
                Block[] variants = variantList[id];
                if (variants == null) {
                    variantList[id] = new Block[]{new BlockUnknown(id)};
                    log.debug("Block {} is not available in current base game version", id);
                    continue;
                }

                Block block;
                try {
                    Constructor<? extends Block> constructor = c.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    block = constructor.newInstance();
                    int defaultMeta = block.getDefaultMeta();
                    variants[0] = block;
                    for (int data = 1; data < variants.length; ++data) {
                        Block variant = constructor.newInstance();
                        if (block.isValidMeta(data)) {
                            variant.init(data);
                        } else {
                            variant.init(defaultMeta);
                        }
                        variants[data] = variant;
                    }
                } catch (Exception e) {
                    log.error("Error while registering " + c.getName(), e);
                    variants[0] = new BlockUnknown(id);
                    for (int data = 1; data < variants.length; ++data) {
                        variants[data] = new BlockUnknown(id, data);
                    }
                    continue;
                }

                solid[id] = block.isSolid();
                transparent[id] = block.isTransparent();
                light[id] = (byte) block.getLightLevel();
                assert light[id] >= 0 && light[id] <= 15;
//                lightBlock[id] = block.getLightBlock();
                assert lightBlock[id] >= 0 && lightBlock[id] <= 15;
//                lightBlocking[id] = block.getLightBlock() > 0 || block.isSlab();
                lightBlocking[id] = lightBlock[id] > 0 || block.isSlab();

                if (block.isSolid()) {
                    if (block.isTransparent()) {
                        if (block.isLiquid() || block.is(ICE)) {
                            lightFilter[id] = 2;
                        } else {
                            lightFilter[id] = 1;
                        }
                    } else {
                        lightFilter[id] = 15;
                    }
                } else {
                    lightFilter[id] = 1;
                }
            } else {
                lightFilter[id] = 1;

                Block[] variants = variantList[id];
                if (variants == null) {
                    variantList[id] = new Block[]{new BlockUnknown(id)};
                } else {
                    variants[0] = new BlockUnknown(id);
                    for (int i = 1; i < variants.length; i++) {
                        variants[i] = new BlockUnknown(id, i);
                    }
                }
            }
        }
    }

    public static Block get(int id) {
        return get(id, 0);
    }

    public static Block get(int id, Integer meta) {
        if (meta != null) {
            return get(id, (int) meta);
        } else {
            return get(id);
        }
    }

    public static Block get(int id, Integer meta, Position pos) {
        Block block = get(id, meta == null ? 0 : meta);
        if (pos != null) {
            block.x = pos.x;
            block.y = pos.y;
            block.z = pos.z;
            block.level = pos.level;
        }
        return block;
    }

    public static Block get(int id, int data) {
        try {
            Block[] variants = variantList[id];
            if (data < 0 || data >= variants.length) {
                if (LOG_INVALID_BLOCK_AUX_ACCESS) {
                    log.warn("Invalid block meta: id {}, meta {}", id, data, new IllegalArgumentException());
                }
                return variants[0].clone();
            }
            return variants[data].clone();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Invalid block id: " + id);
        }
    }

    public static Block getUnsafe(int id) {
        return getUnsafe(id, 0);
    }

    public static Block getUnsafe(int id, Integer meta) {
        if (meta == null) {
            return getUnsafe(id);
        }
        return getUnsafe(id, (int) meta);
    }

    public static Block getUnsafe(int id, int data) {
        try {
            Block[] variants = variantList[id];
            if (data < 0 || data >= variants.length) {
                if (LOG_INVALID_BLOCK_AUX_ACCESS) {
                    log.warn("Invalid block meta: id {}, meta {}", id, data, new IllegalArgumentException());
                }
                return variants[0];
            }
            return variants[data];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Invalid block id: " + id);
        }
    }

    public static Block get(int fullId, Level level, int x, int y, int z) {
        Block block = fromFullId(fullId);
        block.x = x;
        block.y = y;
        block.z = z;
        block.level = level;
        return block;
    }

    public static Block fromFullId(int fullId) {
        return get(fullId >> BLOCK_META_BITS, fullId & BLOCK_META_MASK);
    }

    public static int itemIdToBlockId(int id) {
        return id >= 0 ? id : 0xff - id;
    }

    public static Block fromItemId(int id) {
        return get(itemIdToBlockId(id));
    }

    public static Block fromItemId(int id, Integer meta) {
        return get(itemIdToBlockId(id), meta);
    }

    public static Block fromItemId(int id, int data) {
        return get(itemIdToBlockId(id), data);
    }

    public static Block fromString(String str) {
        return fromString(str, false);
    }

    public static Block fromString(String str, boolean lookupAlias) {
        String[] split = str.split(":", 3);

        String name;
        int meta;
        if (split.length > 2) {
            name = split[0] + ":" + split[1];
            try {
                meta = Integer.parseInt(split[2]);
            } catch (NumberFormatException e) {
                meta = 0;
            }
        } else if (split.length == 2) {
            try {
                meta = Integer.parseInt(split[1]);
                name = split[0];
            } catch (NumberFormatException e) {
                name = str;
                meta = 0;
            }
        } else {
            name = str;
            meta = 0;
        }

        int id;
        try {
            id = Integer.parseInt(name);
        } catch (NumberFormatException e) {
            int fullId = Blocks.getFullIdByBlockName(name, lookupAlias);
            if (fullId != -1) {
                id = Block.getIdFromFullId(fullId);
                int auxVal = Block.getDamageFromFullId(fullId);
                if (auxVal != 0) {
                    meta = auxVal;
                }
            } else {
                id = AIR;
            }
        }

        return get(id, meta);
    }

    @Nullable
    public static Block fromStringNullable(String str) {
        return fromStringNullable(str, false);
    }

    @Nullable
    public static Block fromStringNullable(String str, boolean lookupAlias) {
        String[] split = str.split(":", 3);

        String name;
        int meta;
        if (split.length > 2) {
            name = split[0] + ":" + split[1];
            try {
                meta = Integer.parseInt(split[2]);
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (split.length == 2) {
            try {
                meta = Integer.parseInt(split[1]);
                name = split[0];
            } catch (NumberFormatException e) {
                name = str;
                meta = 0;
            }
        } else {
            name = str;
            meta = 0;
        }

        int id;
        try {
            id = Integer.parseInt(name);
        } catch (NumberFormatException e) {
            int fullId = Blocks.getFullIdByBlockName(name, lookupAlias);
            if (fullId == -1) {
                return null;
            }
            id = Block.getIdFromFullId(fullId);
            int auxVal = Block.getDamageFromFullId(fullId);
            if (auxVal != 0) {
                meta = auxVal;
            }
        }

        return get(id, meta);
    }

    @Nullable
    public static Block fromIdentifier(String identifier) {
        return fromIdentifier(identifier, 0);
    }

    @Nullable
    public static Block fromIdentifier(String identifier, boolean lookupAlias) {
        return fromIdentifier(identifier, 0, lookupAlias);
    }

    @Nullable
    public static Block fromIdentifier(String identifier, int meta) {
        return fromIdentifier(identifier, meta, false);
    }

    @Nullable
    public static Block fromIdentifier(String identifier, int meta, boolean lookupAlias) {
        int fullId = Blocks.getFullIdByBlockName(identifier, lookupAlias);
        if (fullId == -1) {
            return null;
        }

        int auxVal = Block.getDamageFromFullId(fullId);
        if (auxVal != 0) {
            meta = auxVal;
        }

        return get(Block.getIdFromFullId(fullId), meta);
    }

    public static int parseFullId(String str) {
        return parseFullId(str, false);
    }

    public static int parseFullId(String str, boolean lookupAlias) {
        try {
            return Block.getFullId(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            return Blocks.getFullIdByBlockName(str, lookupAlias);
        }
    }

    /**
     * Places the Block, using block space and block target, and side. Returns if the block has been placed.
     * @param block replace block
     * @param target clicked block
     * @param face clicked block face
     */
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, @Nullable Player player) {
        return this.getLevel().setBlock(this, this, true, true);
    }

    //http://minecraft.gamepedia.com/Breaking
    public boolean canHarvestWithHand() {  //used for calculating breaking time
        return true;
    }

    public boolean isBreakable(Item item) {
        return true;
    }

    public int tickRate() {
        return 10;
    }

    public boolean onBreak(Item item) {
        return onBreak(item, null);
    }

    public boolean onBreak(Item item, @Nullable Player player) {
        return this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
    }

    public int onUpdate(int type) {
        return 0;
    }

    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    public boolean onActivate(Item item, @Nullable Player player) {
        return onActivate(item, null, player);
    }

    public boolean onActivate(Item item, @Nullable BlockFace face, @Nullable Player player) {
        return onActivate(item, face, 0.5f, 0.5f, 0.5f, player);
    }

    public boolean onActivate(Item item, @Nullable BlockFace face, float fx, float fy, float fz, @Nullable Player player) {
        return false;
    }

    public float getHardness() {
        return 0;
    }

    public float getResistance() {
        return getHardness() * 5;
    }

    public int getBurnChance() {
        return 0;
    }

    /**
     * @return burn odds
     */
    public int getBurnAbility() {
        return 0;
    }

    public int getToolType() {
        return BlockToolType.NONE;
    }

    public float getFrictionFactor() {
        return 0.6f;
    }

    public int getLightLevel() {
        return 0;
    }

    //TODO
    public int getLightBlock() {
        return 15;
    }

    public boolean canBePlaced() {
        return true;
    }

    public boolean canBeReplaced() {
        return false;
    }

    public boolean isTransparent() {
        return false;
    }

    public boolean isSolid() {
        return true;
    }

    public boolean canBeFlowedInto() {
        return false;
    }

    public boolean canBeActivated() {
        return false;
    }

    public boolean hasUI() {
        return false;
    }

    public boolean hasEntityCollision() {
        return false;
    }

    public boolean canPassThrough() {
        return false;
    }

    public boolean canBePushed() {
        return true;
    }

    public boolean canBePulled() {
        return true;
    }

    public boolean breaksWhenMoved() {
        return false;
    }

    public boolean sticksToPiston() {
        return true;
    }

    public boolean hasComparatorInputOverride() {
        return false;
    }

    public int getComparatorInputOverride() {
        return 0;
    }

    public boolean canBeClimbed() {
        return false;
    }

    public BlockColor getColor() {
        return BlockColor.VOID_BLOCK_COLOR;
    }

    public abstract String getName();

    public abstract int getId();

    public int getItemId() {
        return getItemId(this.getId());
    }

    public static int getItemId(int blockId) {
        return blockId <= 255 ? blockId : 255 - blockId;
    }

    /**
     * The full id is a combination of the id and data.
     * @return full id
     */
    public final int getFullId() {
        return getFullId(this.getId(), getDamage());
    }

    public static int getFullId(int id) {
        return id << BLOCK_META_BITS;
    }

    public static int getFullId(int id, int meta) {
        return getFullId(id) | meta;
    }

    public static int getIdFromFullId(int fullId) {
        return fullId >> BLOCK_META_BITS;
    }

    public static int getDamageFromFullId(int fullId) {
        return fullId & BLOCK_META_MASK;
    }

    public void addVelocityToEntity(Entity entity, Vector3 vector) {

    }

    public final int getDamage() {
        return meta;
    }

    public final void setDamage(int meta) {
        this.meta = meta;
    }

    public final void setDamage(Integer meta) {
        setDamage(meta == null ? 0 : meta);
    }

    public int getDefaultMeta() {
        return 0;
    }

    public boolean isStackedByData() {
        return true;
    }

    public boolean isValidMeta(int meta) {
        return meta <= metaMax[getId()];
    }

    @Nullable
    protected BlockLegacy getBlockLegacy() {
        return BlockTypes.getBlockRegistry().getBlock(getId());
    }

    public final boolean setStateUnsafe(BlockState state, int value) {
        BlockLegacy legacyBlock = getBlockLegacy();
        if (legacyBlock == null) {
            return false;
        }
        BlockInstance instanceBlock = legacyBlock.setStateNullable(state, value, getDamage());
        if (instanceBlock == null) {
            return false;
        }
        setDamage(instanceBlock.meta);
        return true;
    }

    public final boolean setState(IntegerBlockState state, int value) {
        return setStateUnsafe(state, value);
    }

    public final boolean setState(BooleanBlockState state, boolean value) {
        return setStateUnsafe(state, value ? 1 : 0);
    }

    public final <T extends Enum<?>> boolean setState(EnumBlockState<T> state, T value) {
        return setStateUnsafe(state, value.ordinal());
    }

    public final boolean setState(StringBlockState state, String value) {
        return setStateUnsafe(state, state.getValues().indexOf(value));
    }

    public final int getStateUnsafe(BlockState state) {
        BlockLegacy legacyBlock = getBlockLegacy();
        if (legacyBlock == null) {
            return 0;
        }
        return legacyBlock.getState(state, getDamage());
    }

    public final int getState(IntegerBlockState state) {
        return getStateUnsafe(state);
    }

    public final boolean getState(BooleanBlockState state) {
        return getStateUnsafe(state) == 1;
    }

    public final <T extends Enum<?>> T getState(EnumBlockState<T> state) {
        return state.getValues().get(getStateUnsafe(state));
    }

    public final String getState(StringBlockState state) {
        return state.getValues().get(getStateUnsafe(state));
    }

    public final boolean hasState(BlockState state) {
        BlockLegacy legacyBlock = getBlockLegacy();
        if (legacyBlock == null) {
            return false;
        }
        return legacyBlock.hasState(state);
    }

    public void rotate(Rotation rotation) {
        setDamage(rotated(rotation));
    }

    public int rotated(Rotation rotation) {
        int meta = getDamage();
        if (rotation == Rotation.NONE) {
            return meta;
        }
        BlockLegacy legacyBlock = getBlockLegacy();
        if (legacyBlock == null) {
            return meta;
        }
        return legacyBlock.rotate(meta, rotation);
    }

    public void mirror(Mirror mirror) {
        setDamage(mirrored(mirror));
    }

    public int mirrored(Mirror mirror) {
        int meta = getDamage();
        if (mirror == Mirror.NONE) {
            return meta;
        }
        BlockLegacy legacyBlock = getBlockLegacy();
        if (legacyBlock == null) {
            return meta;
        }
        return legacyBlock.mirror(meta, mirror);
    }

    public final void position(Position v) {
        this.x = (int) v.x;
        this.y = (int) v.y;
        this.z = (int) v.z;
        this.level = v.level;
    }

    public Item[] getDrops(Item item) {
        return getDrops(item, null);
    }

    public Item[] getDrops(Item item, @Nullable Player player) {
        return new Item[]{
                this.toItem(true)
        };
    }

    private float getDestroySpeed(Item item, boolean correctTool) {
        int toolType = item.getBlockToolType();
        if (toolType == BlockToolType.SWORD) {
            if (V1_21_20.isAvailable() && item.is(Item.MACE)) {
                return 1;
            }
            if (is(WEB)) {
                return 15;
            }
            if (is(BAMBOO) || (ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_21_50.isAvailable()) && is(BAMBOO_SAPLING)) {
                return 10;
            }
            if (V1_21_50.isAvailable() && !correctTool) {
                return 1;
            }
            return 1.5f;
        }
        if (!correctTool) {
            return 1;
        }
        if (toolType == BlockToolType.SHEARS) {
            if (is(WEB) || isLeaves()) {
                return 15;
            }
            if (isWool()) {
                return 5;
            }
            if (is(VINE) || is(GLOW_LICHEN)) {
                return 2;
            }
            return 1;
        }
        if (toolType == BlockToolType.NONE) {
            return 1;
        }
        switch (item.getTier()) {
            case ItemTool.TIER_WOODEN:
                return 2;
            case ItemTool.TIER_STONE:
                return 4;
            case ItemTool.TIER_COPPER:
                return 5;
            case ItemTool.TIER_IRON:
                return 6;
            case ItemTool.TIER_DIAMOND:
                return 8;
            case ItemTool.TIER_NETHERITE:
                return 9;
            case ItemTool.TIER_GOLD:
                return 12;
            default:
                return 1;
        }
    }

    public float getBreakTime(Item item, Player player) {
        Objects.requireNonNull(item, "getBreakTime: Item can not be null");
        Objects.requireNonNull(player, "getBreakTime: Player can not be null");
        float blockHardness = getHardness();

        if (blockHardness == 0) {
            return 0;
        }

        int toolType = getToolType();
        boolean correctTool = isToolCompatible(toolType, item.getBlockToolType());
        float baseTime = (correctTool || canHarvestWithHand() ? 1.5f : 5) * blockHardness;
        float speed = 1 / baseTime;

        float destroySpeedBonus = 0;
        if (correctTool) {
            int efficiency = item.getEnchantmentLevel(Enchantment.EFFICIENCY);
            if (efficiency > 0) {
                destroySpeedBonus = efficiency * efficiency + 1;
            }
        }

        speed *= getDestroySpeed(item, correctTool) + destroySpeedBonus;

        int amp = 0;
        Effect digSpeed = player.getEffect(Effect.HASTE);
        if (digSpeed != null) {
            amp = digSpeed.getAmplifier() + 1;
        }
        Effect conduitPower = player.getEffect(Effect.CONDUIT_POWER);
        if (conduitPower != null) {
            amp = Math.max(amp, conduitPower.getAmplifier() + 1);
        }
        if (amp > 0) {
            speed *= 1 + 0.2f * amp;
        }

        Effect digSlowdown = player.getEffect(Effect.MINING_FATIGUE);
        if (digSlowdown != null) {
            speed *= Math.pow(0.3f, digSlowdown.getAmplifier() + 1);
        }

        if (player.isRiding() || !player.isOnGround() && !player.getAdventureSettings().get(AdventureSettings.Type.NO_CLIP) && !player.getAdventureSettings().get(AdventureSettings.Type.FLYING)) {
            speed *= 0.2f;
        }
        if (player.isInsideOfWater() && !player.getArmorInventory().getHelmet().hasEnchantment(Enchantment.AQUA_AFFINITY)) {
            speed *= 0.2f;
        }

        return 1 / speed;
    }

    public boolean isToolCompatible(Item item) {
        return isToolCompatible(item.getBlockToolType());
    }

    public boolean isToolCompatible(int toolType) {
        return isToolCompatible(getToolType(), toolType);
    }

    public static boolean isToolCompatible(int blockToolType, int itemToolType) {
        if (blockToolType == BlockToolType.NONE) {
            return false;
        }
        return (blockToolType & itemToolType) != 0;
    }

    @Override
    public Block getSide(BlockFace face) {
        if (face == null) {
            return this;
        }
        if (this.isValid()) {
            return this.getLevel().getBlock((int) x + face.getXOffset(), (int) y + face.getYOffset(), (int) z + face.getZOffset());
        }
        return this.getSide(face, 1);
    }

    @Override
    public Block getSide(BlockFace face, int step) {
        if (step == 0 || face == null) {
            return this;
        }

        if (this.isValid()) {
            if (step == 1) {
                return this.getLevel().getBlock((int) x + face.getXOffset(), (int) y + face.getYOffset(), (int) z + face.getZOffset());
            } else {
                return this.getLevel().getBlock((int) x + face.getXOffset() * step, (int) y + face.getYOffset() * step, (int) z + face.getZOffset() * step);
            }
        }
        Block block = Block.get(Item.AIR, 0);
        block.x = (int) x + face.getXOffset() * step;
        block.y = (int) y + face.getYOffset() * step;
        block.z = (int) z + face.getZOffset() * step;
        return block;
    }

    @Override
    public Block up() {
        return up(1);
    }

    @Override
    public Block up(int step) {
        return getSide(BlockFace.UP, step);
    }

    @Override
    public Block down() {
        return down(1);
    }

    @Override
    public Block down(int step) {
        return getSide(BlockFace.DOWN, step);
    }

    @Override
    public Block north() {
        return north(1);
    }

    @Override
    public Block north(int step) {
        return getSide(BlockFace.NORTH, step);
    }

    @Override
    public Block south() {
        return south(1);
    }

    @Override
    public Block south(int step) {
        return getSide(BlockFace.SOUTH, step);
    }

    @Override
    public Block east() {
        return east(1);
    }

    @Override
    public Block east(int step) {
        return getSide(BlockFace.EAST, step);
    }

    @Override
    public Block west() {
        return west(1);
    }

    @Override
    public Block west(int step) {
        return getSide(BlockFace.WEST, step);
    }

    @Override
    public String toString() {
        return "Block[" + this.getName() + "] (" + this.getId() + ":" + this.getDamage() + ")";
    }

    public String superToString() {
        return super.toString();
    }

    public boolean collidesWithBB(AxisAlignedBB bb) {
        return collidesWithBB(bb, false);
    }

    public boolean collidesWithBB(AxisAlignedBB bb, boolean collisionBB) {
        AxisAlignedBB bb1 = collisionBB ? this.getCollisionBoundingBox() : this.getBoundingBox();
        return bb1 != null && bb.intersectsWith(bb1);
    }

    public void onEntityCollide(Entity entity) {

    }

    public AxisAlignedBB getBoundingBox() {
        return this.recalculateBoundingBox();
    }

    public AxisAlignedBB getCollisionBoundingBox() {
        return this.recalculateCollisionBoundingBox();
    }

    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public double getMinX() {
        return this.x;
    }

    @Override
    public double getMinY() {
        return this.y;
    }

    @Override
    public double getMinZ() {
        return this.z;
    }

    @Override
    public double getMaxX() {
        return this.x + 1;
    }

    @Override
    public double getMaxY() {
        return this.y + 1;
    }

    @Override
    public double getMaxZ() {
        return this.z + 1;
    }

    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return getBoundingBox();
    }

    @Override
    public MovingObjectPosition calculateIntercept(Vector3 pos1, Vector3 pos2) {
        return clip(pos1, pos2, this::getBoundingBox);
    }

    public MovingObjectPosition clip(Vector3 pos1, Vector3 pos2, Supplier<AxisAlignedBB> aabbGetter) {
        AxisAlignedBB bb = aabbGetter.get();
        if (bb == null) {
            return null;
        }

        Vector3 v1 = pos1.getIntermediateWithXValue(pos2, bb.getMinX());
        Vector3 v2 = pos1.getIntermediateWithXValue(pos2, bb.getMaxX());
        Vector3 v3 = pos1.getIntermediateWithYValue(pos2, bb.getMinY());
        Vector3 v4 = pos1.getIntermediateWithYValue(pos2, bb.getMaxY());
        Vector3 v5 = pos1.getIntermediateWithZValue(pos2, bb.getMinZ());
        Vector3 v6 = pos1.getIntermediateWithZValue(pos2, bb.getMaxZ());

        if (v1 != null && !bb.isVectorInYZ(v1)) {
            v1 = null;
        }

        if (v2 != null && !bb.isVectorInYZ(v2)) {
            v2 = null;
        }

        if (v3 != null && !bb.isVectorInXZ(v3)) {
            v3 = null;
        }

        if (v4 != null && !bb.isVectorInXZ(v4)) {
            v4 = null;
        }

        if (v5 != null && !bb.isVectorInXY(v5)) {
            v5 = null;
        }

        if (v6 != null && !bb.isVectorInXY(v6)) {
            v6 = null;
        }

        Vector3 vector = v1;

        if (v2 != null && (vector == null || pos1.distanceSquared(v2) < pos1.distanceSquared(vector))) {
            vector = v2;
        }

        if (v3 != null && (vector == null || pos1.distanceSquared(v3) < pos1.distanceSquared(vector))) {
            vector = v3;
        }

        if (v4 != null && (vector == null || pos1.distanceSquared(v4) < pos1.distanceSquared(vector))) {
            vector = v4;
        }

        if (v5 != null && (vector == null || pos1.distanceSquared(v5) < pos1.distanceSquared(vector))) {
            vector = v5;
        }

        if (v6 != null && (vector == null || pos1.distanceSquared(v6) < pos1.distanceSquared(vector))) {
            vector = v6;
        }

        if (vector == null) {
            return null;
        }

        int f = -1;

        if (vector == v1) {
            f = 4;
        } else if (vector == v2) {
            f = 5;
        } else if (vector == v3) {
            f = 0;
        } else if (vector == v4) {
            f = 1;
        } else if (vector == v5) {
            f = 2;
        } else if (vector == v6) {
            f = 3;
        }

        return MovingObjectPosition.fromBlock(this, f, vector);
    }

    public String getSaveId() {
        String name = getClass().getName();
        return name.substring(16);
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) throws Exception {
        if (this.getLevel() != null) {
            this.getLevel().getBlockMetadata().setMetadata(this, metadataKey, newMetadataValue);
        }
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) throws Exception {
        if (this.getLevel() != null) {
            return this.getLevel().getBlockMetadata().getMetadata(this, metadataKey);

        }
        return null;
    }

    @Override
    public boolean hasMetadata(String metadataKey) throws Exception {
        return this.getLevel() != null && this.getLevel().getBlockMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) throws Exception {
        if (this.getLevel() != null) {
            this.getLevel().getBlockMetadata().removeMetadata(this, metadataKey, owningPlugin);
        }
    }

    @Override
    public Block clone() {
        return (Block) super.clone();
    }

    public int getWeakPower(BlockFace face) {
        return 0;
    }

    /**
     * @return direct signal
     */
    public int getStrongPower(BlockFace side) {
        return 0;
    }

    public boolean isPowerSource() {
        return false;
    }

    public int getDropExp() {
        return 0;
    }

    public int getFuelTime() {
        return 0;
    }

    public float getFurnaceXpMultiplier() {
        return 0;
    }

    public int getCompostableChance() {
        return 0;
    }

    public boolean isNormalBlock() {
        return !isTransparent() && isSolid() && !isPowerSource();
    }

    public static boolean equals(Block b1, Block b2) {
        return equals(b1, b2, true);
    }

    public static boolean equals(Block b1, Block b2, boolean checkDamage) {
        return b1 != null && b2 != null && b1.getId() == b2.getId() && (!checkDamage || b1.getDamage() == b2.getDamage());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        return obj instanceof Block && equals(this, (Block) obj) && (this.level == null || (this.level == ((Block) obj).level && super.equals(obj)));
    }

    @Override
    public int hashCode() {
        return getFullId();
    }

    /**
     * @since 1.19.50
     */
    public boolean isBlockItem() {
        return false;
    }

    public int getMaxStackSize() {
        return 64;
    }

    public Item toItem() {
        return toItem(false);
    }

    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId(), this.getDamage(), 1);
    }

    public Item pick(boolean addUserData) {
        return toItem(true);
    }

    public Item[] getSilkTouchResource() {
        return new Item[]{toItem(true)};
    }

    public boolean canSilkTouch() {
        return false;
    }

    public boolean canContainWater() {
        return false;
    }

    public boolean canContainFlowingWater() {
        return false;
    }

    public boolean canContainSnow() {
        return false;
    }

    public boolean canProvideSupport(BlockFace face) {
        return canProvideSupport(face, SupportType.FULL);
    }

    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return true;
    }

    public int getBlockEntityType() {
        return 0;
    }

    public boolean isFireResistant() {
        return false;
    }

    public boolean isExplodable() {
        return true;
    }

    public boolean isChemistryFeature() {
        return false;
    }

    //TODO: brewing stand, cauldron and composter
    public AxisAlignedBB[] getCollisionShape() {
        AxisAlignedBB aabb = this.getCollisionBoundingBox();
        if (aabb == null) {
            return null;
        }
        return new AxisAlignedBB[]{aabb};
    }

    public void onProjectileHit(EntityProjectile projectile, MovingObjectPosition hitResult) {
    }

    public boolean isVanilla() {
        return true;
    }

    public void playPlaceSound(Block target) {
        level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_PLACE, getFullId());
    }

    public boolean isFertilizable() {
        return false;
    }

    public final Block getBlock() {
        return this;
    }

    public boolean is(int id) {
        return getId() == id;
    }

    public boolean is(int id, int damage) {
        return getId() == id && getDamage() == damage;
    }

    public boolean is(Block block) {
        return getId() == block.getId();
    }

    public boolean is(Item item) {
        return getItemId() == item.getId();
    }

    public boolean isAir() {
        return false;
    }

    public boolean isLiquid() {
        return false;
    }

    public boolean isLiquidSource() {
        return false;
    }

    public boolean isFullLiquid() {
        return false;
    }

    public boolean isDownwardLiquid() {
        return false;
    }

    public boolean isSameLiquid(Block block) {
        return false;
    }

    public boolean isWater() {
        return false;
    }

    public boolean isWaterSource() {
        return false;
    }

    public boolean isLava() {
        return false;
    }

    //TODO: tag system

    public boolean isLeaves() {
        return false;
    }

    public boolean isLog() {
        return false;
    }

    public boolean isSapling() {
        return false;
    }

    public boolean isVegetation() {
        return false;
    }

    public boolean isCrop() {
        return false;
    }

    public boolean isStem() {
        return false;
    }

    public boolean isStairs() {
        return false;
    }

    public boolean isSlab() {
        return false;
    }

    public boolean isHalfSlab() {
        return false;
    }

    public boolean isDoubleSlab() {
        return false;
    }

    public boolean isRail() {
        return false;
    }

    public boolean isWall() {
        return false;
    }

    public boolean isFence() {
        return false;
    }

    public boolean isDoor() {
        return false;
    }

    public boolean isFenceGate() {
        return false;
    }

    public boolean isTrapdoor() {
        return false;
    }

    public boolean isPressurePlate() {
        return false;
    }

    public boolean isButton() {
        return false;
    }

    public boolean isDaylightDetector() {
        return false;
    }

    public boolean isDiode() {
        return false;
    }

    public boolean isHeavyBlock() {
        return false;
    }

    public boolean isCampfire() {
        return false;
    }

    public boolean isFire() {
        return false;
    }

    public boolean isTorch() {
        return false;
    }

    public boolean isSign() {
        return false;
    }

    public boolean isStandingSign() {
        return false;
    }

    public boolean isWallSign() {
        return false;
    }

    public boolean isHangingSign() {
        return false;
    }

    public boolean isItemFrame() {
        return false;
    }

    public boolean isBeehive() {
        return false;
    }

    public boolean isCake() {
        return false;
    }

    public boolean isCandleCake() {
        return false;
    }

    public boolean isCandle() {
        return false;
    }

    public boolean isCaveVines() {
        return false;
    }

    public boolean isGlass() {
        return false;
    }

    public boolean isCauldron() {
        return false;
    }

    public boolean isWool() {
        return false;
    }

    public boolean isCarpet() {
        return false;
    }

    public boolean isCoral() {
        return false;
    }

    public boolean isShulkerBox() {
        return false;
    }

    public boolean isConcrete() {
        return false;
    }

    public boolean isSkull() {
        return false;
    }

    public boolean isChest() {
        return false;
    }

    public boolean isFurnace() {
        return false;
    }

    public boolean isWaxed() {
        return false;
    }

    public boolean hasCopperBehavior() {
        return false;
    }

    public int getCopperAge() {
        return -1;
    }

    protected static AxisAlignedBB box(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new SimpleAxisAlignedBB(minX / 16, minY / 16, minZ / 16, maxX / 16, maxY / 16, maxZ / 16);
    }

    @ToString
    private static class BlockEntry {
        @JsonProperty("MaterialType")
        private int mMaterialType;
        @JsonProperty("_id")
        private short mId;
        @JsonProperty("_mFriction")
        private float mFriction;
        @JsonProperty("_mLightBlock")
        private byte mLightBlock;
        @JsonProperty("_mLightEmission")
        private byte mLightEmission;
        @JsonProperty("_mMapColorARGB")
        private int mMapColor;
        @JsonProperty("_mMinRequiredBaseGameVersion")
        private String mMinRequiredBaseGameVersion;

        private int blockEntityType;
        private int blockItemId;
        private int burnOdds;
        private boolean canBeOriginalSurface;
        private boolean canContainLiquid;
        private boolean canHurtAndBreakItem;
        private boolean canInstatick;
        private int creativeCategory;
        private float explosionResistance;
        private int flameOdds;
        private float hardness;
        private boolean hasBlockEntity;
        @JsonProperty("identifier")
        private String nameId;
        private boolean isLavaFlammable;
        private boolean isMotionBlockingBlock;
        private boolean isSolid;
        private boolean isSolidBlockingBlock;
        private boolean isWaterBlocking;
        private int renderLayer;
        private boolean shouldRandomTick;
        private float thickness;
    }
}
