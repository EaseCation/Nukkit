package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;

public class BlockBubbleColumn extends BlockTransparentMeta {

    public static final int DRAG_DOWN_BIT = 0b1;

    public BlockBubbleColumn() {
        this(0);
    }

    public BlockBubbleColumn(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BUBBLE_COLUMN;
    }

    @Override
    public String getName() {
        return "Bubble Column";
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
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
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(AIR);
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            level.scheduleUpdate(this, 6);
            return type;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            Block extra = level.getExtraBlock(this);
            if (!extra.isWater()) {
                level.setBlock(this, get(AIR), true);
                return type;
            }

            if (!extra.isLiquidSource()) {
                level.setExtraBlock(this, get(AIR), true, false);
                level.setBlock(this, extra, true);
                return type;
            }

            Block down = down();
            int id = down.getId();
            if (isDragDown()) {
                if (id != MAGMA && (id != BUBBLE_COLUMN || down.getDamage() != getDamage())) {
                    level.useBreakOn(this);
                    return type;
                }
            } else if (id != SOUL_SAND && (id != BUBBLE_COLUMN || down.getDamage() != getDamage())) {
                level.useBreakOn(this);
                return type;
            }

            Block up = up();
            if (!up.isWaterSource()) {
                return 0;
            }

            level.setExtraBlock(up, get(FLOWING_WATER), true, false);
            Block clone = clone();
            level.scheduleUpdate(clone, up, 1);
            level.setBlock(up, clone, true);
            return type;
        }

        return 0;
    }

    public boolean isDragDown() {
        return (getDamage() & DRAG_DOWN_BIT) == DRAG_DOWN_BIT;
    }
}
