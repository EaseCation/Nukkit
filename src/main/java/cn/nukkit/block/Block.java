package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
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
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static cn.nukkit.SharedConstants.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public abstract class Block extends Position implements Metadatable, Cloneable, AxisAlignedBB, BlockID {

    // Nukkit runtime definitions
    public static final int BLOCK_ID_COUNT = Mth.smallestEncompassingPowerOfTwo(UNDEFINED);
    public static final int BLOCK_ID_MASK = BLOCK_ID_COUNT - 1;
    public static final int BLOCK_META_COUNT = Mth.smallestEncompassingPowerOfTwo(5469); // wtf cobblestone_wall
    public static final int BLOCK_META_MASK = BLOCK_META_COUNT - 1;
    public static final int BLOCK_META_BITS = Mth.log2PowerOfTwo(BLOCK_META_COUNT);
    public static final int FULL_BLOCK_COUNT = BLOCK_ID_COUNT << BLOCK_META_BITS;
    public static final int FULL_BLOCK_MASK = FULL_BLOCK_COUNT - 1;
    public static final int FULL_BLOCK_ID_MASK = BLOCK_ID_MASK << BLOCK_META_BITS;

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

    protected Block() {}

    @SuppressWarnings("unchecked")
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        Object2IntMap<String> metaTable; // auto-generated from development client
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("block_meta_table.json");
             InputStreamReader reader = new InputStreamReader(stream)) {
            metaTable = new Gson().fromJson(reader, Object2IntOpenHashMap.class);
        } catch (NullPointerException | IOException e) {
            throw new AssertionError("Unable to load block_meta_table.json", e);
        }

        metaTable.put(String.valueOf(LEAVES2), 15); //TODO: HACK

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
            propertiesTable = JsonUtil.COMMON_JSON_MAPPER.readValue(stream, BlockEntry[].class);
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
            Class<?> c = list[id];
            if (c != null) {
                Block[] variants = variantList[id];
                if (variants == null) {
                    variantList[id] = new Block[]{new BlockUnknown(id)};
                    log.debug("Block {} is not available in current base game version", id);
                    continue;
                }

                Block block;
                try {
                    block = (Block) c.newInstance();
                    int defaultMeta = block.getDefaultMeta();
                    variants[0] = block;
                    try {
                        Constructor<?> constructor = c.getDeclaredConstructor(int.class);
                        constructor.setAccessible(true);

                        for (int data = 1; data < variants.length; ++data) {
                            if (block.isValidMeta(data)) {
                                variants[data] = (Block) constructor.newInstance(data);
                            } else {
                                variants[data] = (Block) constructor.newInstance(defaultMeta);
                            }
                        }
                    } catch (NoSuchMethodException ignore) {
                        for (int data = 1; data < variants.length; ++data) {
                            variants[data] = block;
                        }

                        if (hasMeta[id]) {
                            log.warn("meta mismatch: {} (expected 0-{})", id, metaMax[id]);
                        }
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

    private static final Pattern integerPattern = Pattern.compile("^[1-9]\\d*$");

    public static Block fromString(String str) {
        String[] b = str.trim().replace(' ', '_').replace("minecraft:", "").split(":", 2);

        int id = AIR;
        int meta = 0;
        if (integerPattern.matcher(b[0]).matches()) {
            id = Integer.parseInt(b[0]);
        } else {
            try {
                Field field = BlockID.class.getField(b[0].toUpperCase());
                field.setAccessible(true);
                id = field.getInt(null);
            } catch (Exception ignore) {
            }
        }

        if (b.length > 1) meta = Integer.parseInt(b[1]);

        return get(id, meta);
    }

    /**
     * Places the Block, using block space and block target, and side. Returns if the block has been placed.
     * @param block replace block
     * @param target clicked block
     * @param face clicked block face
     */
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
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
        return false;
    }

    public double getHardness() {
        return 10;
    }

    public double getResistance() {
        return 1;
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
        return ItemTool.TYPE_NONE;
    }

    public double getFrictionFactor() {
        return 0.6;
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
    public int getFullId() {
        return this.getId() << BLOCK_META_BITS;
    }

    public void addVelocityToEntity(Entity entity, Vector3 vector) {

    }

    public int getDamage() {
        return 0;
    }

    public void setDamage(int meta) {
        // Do nothing
    }

    public final void setDamage(Integer meta) {
        setDamage(meta == null ? 0 : meta);
    }

    public int getDefaultMeta() {
        return 0;
    }

    public boolean isValidMeta(int meta) {
        return meta <= metaMax[getId()];
    }

    public final void position(Position v) {
        this.x = (int) v.x;
        this.y = (int) v.y;
        this.z = (int) v.z;
        this.level = v.level;
    }

    public Item[] getDrops(Item item) {
        if (this.getId() < 0 || this.getId() > list.length) { //Unknown blocks
            return new Item[0];
        } else {
            return new Item[]{
                    this.toItem(true)
            };
        }
    }

    private static double toolBreakTimeBonus0(int toolType, int toolTier, int blockId) {
        if (toolType == ItemTool.TYPE_SWORD) return blockId == Block.WEB ? 15.0 : 1.0;
        if (toolType == ItemTool.TYPE_SHEARS) {
            if (blockId == Block.WOOL || blockId == LEAVES || blockId == LEAVES2) {
                return 5.0;
            } else if (blockId == WEB) {
                return 15.0;
            }
            return 1.0;
        }
        if (toolType == ItemTool.TYPE_NONE) return 1.0;
        switch (toolTier) {
            case ItemTool.TIER_WOODEN:
                return 2.0;
            case ItemTool.TIER_STONE:
                return 4.0;
            case ItemTool.TIER_IRON:
                return 6.0;
            case ItemTool.TIER_DIAMOND:
                return 8.0;
            case ItemTool.TIER_GOLD:
                return 12.0;
            default:
                return 1.0;
        }
    }

    private static double speedBonusByEfficiencyLore0(int efficiencyLoreLevel) {
        if (efficiencyLoreLevel == 0) return 0;
        return efficiencyLoreLevel * efficiencyLoreLevel + 1;
    }

    private static double speedRateByHasteLore0(int hasteLoreLevel) {
        return 1.0 + (0.2 * hasteLoreLevel);
    }

    private static int toolType0(Item item) {
        if (item.isSword()) return ItemTool.TYPE_SWORD;
        if (item.isShovel()) return ItemTool.TYPE_SHOVEL;
        if (item.isPickaxe()) return ItemTool.TYPE_PICKAXE;
        if (item.isAxe()) return ItemTool.TYPE_AXE;
        if (item.isHoe()) return ItemTool.TYPE_HOE;
        if (item.isShears()) return ItemTool.TYPE_SHEARS;
        return ItemTool.TYPE_NONE;
    }

    private static boolean correctTool0(int blockToolType, Item item) {
        return (blockToolType == ItemTool.TYPE_SWORD && item.isSword()) ||
                (blockToolType == ItemTool.TYPE_SHOVEL && item.isShovel()) ||
                (blockToolType == ItemTool.TYPE_PICKAXE && item.isPickaxe()) ||
                (blockToolType == ItemTool.TYPE_AXE && item.isAxe()) ||
                (blockToolType == ItemTool.TYPE_HOE && item.isHoe()) ||
                (blockToolType == ItemTool.TYPE_SHEARS && item.isShears()) ||
                blockToolType == ItemTool.TYPE_NONE;
    }

    //http://minecraft.gamepedia.com/Breaking
    private static double breakTime0(double blockHardness, boolean correctTool, boolean canHarvestWithHand,
                                     int blockId, int toolType, int toolTier, int efficiencyLoreLevel, int hasteEffectLevel,
                                     boolean insideOfWaterWithoutAquaAffinity, boolean outOfWaterButNotOnGround) {
        double baseTime = ((correctTool || canHarvestWithHand) ? 1.5 : 5.0) * blockHardness;
        double speed = 1.0 / baseTime;
        if (correctTool) speed *= toolBreakTimeBonus0(toolType, toolTier, blockId);
        speed += correctTool ? speedBonusByEfficiencyLore0(efficiencyLoreLevel) : 0;
        speed *= speedRateByHasteLore0(hasteEffectLevel);
        if (insideOfWaterWithoutAquaAffinity) speed *= 0.2;
        if (outOfWaterButNotOnGround) speed *= 0.2;
        return 1.0 / speed;
    }

    public double getBreakTime(Item item, Player player) {
        Objects.requireNonNull(item, "getBreakTime: Item can not be null");
        Objects.requireNonNull(player, "getBreakTime: Player can not be null");
        double blockHardness = getHardness();

        if (blockHardness == 0) {
            return 0;
        }

        int blockId = getId();
        boolean correctTool = correctTool0(getToolType(), item)
                || item.isShears() && (blockId == WEB || blockId == LEAVES || blockId == LEAVES2);
        boolean canHarvestWithHand = canHarvestWithHand();
        int itemToolType = toolType0(item);
        int itemTier = item.getTier();
        int efficiencyLoreLevel = Optional.ofNullable(item.getEnchantment(Enchantment.EFFICIENCY))
                .map(Enchantment::getLevel).orElse(0);
        int hasteEffectLevel = Optional.ofNullable(player.getEffect(Effect.HASTE))
                .map(Effect::getAmplifier).orElse(0);
        boolean insideOfWaterWithoutAquaAffinity = player.isInsideOfWater() &&
                Optional.ofNullable(player.getInventory().getHelmet().getEnchantment(Enchantment.AQUA_AFFINITY))
                        .map(Enchantment::getLevel).map(l -> l >= 1).orElse(false);
        boolean outOfWaterButNotOnGround = (!player.isInsideOfWater()) && (!player.isOnGround());
        return breakTime0(blockHardness, correctTool, canHarvestWithHand, blockId, itemToolType, itemTier,
                efficiencyLoreLevel, hasteEffectLevel, insideOfWaterWithoutAquaAffinity, outOfWaterButNotOnGround);
    }

    /**
     * @deprecated This function is lack of Player class and is not accurate enough, use #getBreakTime(Item, Player)
     * @param item item used
     * @return break time
     */
    @Deprecated
    public double getBreakTime(Item item) {
        double base = this.getHardness() * 1.5;
        if (this.canBeBrokenWith(item)) {
            if (this.getToolType() == ItemTool.TYPE_SHEARS && item.isShears()) {
                base /= 15;
            } else if (
                    (this.getToolType() == ItemTool.TYPE_PICKAXE && item.isPickaxe()) ||
                            (this.getToolType() == ItemTool.TYPE_AXE && item.isAxe()) ||
                            (this.getToolType() == ItemTool.TYPE_SHOVEL && item.isShovel()) ||
                            (this.getToolType() == ItemTool.TYPE_HOE && item.isHoe())
                    ) {
                int tier = item.getTier();
                switch (tier) {
                    case ItemTool.TIER_WOODEN:
                        base /= 2;
                        break;
                    case ItemTool.TIER_STONE:
                        base /= 4;
                        break;
                    case ItemTool.TIER_IRON:
                        base /= 6;
                        break;
                    case ItemTool.TIER_DIAMOND:
                        base /= 8;
                        break;
                    case ItemTool.TIER_GOLD:
                        base /= 12;
                        break;
                }
            }
        } else {
            base *= 3.33;
        }

        if (item.isSword()) {
            base *= 0.5;
        }

        return base;
    }

    public boolean canBeBrokenWith(Item item) {
        return this.getHardness() != -1;
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

    public String getLocationHash() {
        return this.getFloorX() + ":" + this.getFloorY() + ":" + this.getFloorZ();
    }

    public int getDropExp() {
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
        return obj instanceof Block && equals(this, (Block) obj) && (this.level == null || (this.level == ((Block) obj).level && super.equals(obj)));
    }

    public boolean superEquals(Object obj) {
        return super.equals(obj);
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

    public Item getSilkTouchResource() {
        return toItem(true);
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

    public boolean is(int id) {
        return getId() == id;
    }

    public boolean is(Block block) {
        return getId() == block.getId();
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

    public boolean isWaxed() {
        return false;
    }

    public boolean hasCopperBehavior() {
        return false;
    }

    public int getCopperAge() {
        return -1;
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
