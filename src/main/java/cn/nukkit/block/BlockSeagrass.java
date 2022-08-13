package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockSeagrass extends BlockTransparentMeta {

    public static final int DEFAULT_SEAGRASS = 0;
    public static final int DOUBLE_SEAGRASS_TOP = 1;
    public static final int DOUBLE_SEAGRASS_BOTTOM = 2;

    public BlockSeagrass() {
        this(0);
    }

    public BlockSeagrass(int meta) {
        super(meta & 0x3);
    }

    @Override
    public String getName() {
        return "Seagrass";
    }

    @Override
    public int getId() {
        return SEAGRASS;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WATER_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public Item[] getDrops(Item item) {
        if (!item.isShears()) {
            return new Item[0];
        }
        return new Item[]{toItem(true)};
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!block.isWater() || !block.isFullLiquid()) {
            return false;
        }

        Block below = down();
        if (!canSurvive(below)) {
            return false;
        }

        level.setExtraBlock(this, Block.get(FLOWING_WATER), true, false);

        setDamage(DEFAULT_SEAGRASS);
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.getId() == ItemID.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            if (getDamage() != DEFAULT_SEAGRASS) {
                return true;
            }

            if (y >= level.getMaxHeight()) {
                return true;
            }

            Block up = up();
            if (!up.isWater() || !up.isFullLiquid()) {
                return true;
            }

            if (player != null && !player.isCreative()) {
                item.count--;
            }

            setDamage(DOUBLE_SEAGRASS_BOTTOM);
            level.setBlock(this, this, true, false);

            level.setExtraBlock(up, Block.get(FLOWING_WATER), true, false);
            level.setBlock(up, Block.get(SEAGRASS, DOUBLE_SEAGRASS_TOP), true);

            level.addParticle(new BoneMealParticle(this));
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block extra = level.getExtraBlock(this);
            if (!extra.isWater() || !extra.isFullLiquid()) {
                level.useBreakOn(this);
                return type;
            }

            int meta = getDamage();
            Block below = down();

            if (meta == DOUBLE_SEAGRASS_TOP) {
                if (below.getId() == SEAGRASS && below.getDamage() == DOUBLE_SEAGRASS_BOTTOM) {
                    return 0;
                }
                level.useBreakOn(this);
                return type;
            }

            if (meta == DOUBLE_SEAGRASS_BOTTOM) {
                Block up = up();
                if (up.getId() != SEAGRASS || up.getDamage() != DOUBLE_SEAGRASS_TOP) {
                    level.useBreakOn(this);
                    return type;
                }
            }

            if (canSurvive(below)) {
                return 0;
            }

            level.useBreakOn(this);
            return type;
        }

        return 0;
    }

    private boolean canSurvive(Block below) {
        int id = below.getId();
        return id != MAGMA && id != SOUL_SAND && SupportType.hasFullSupport(below, BlockFace.UP);
    }
}
