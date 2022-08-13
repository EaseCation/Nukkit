package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockCoral extends BlockFlowable {

    public static final int COLOR_MASK = 0b111;
    public static final int DEAD_BIT = 0b1000;

    public static final int BLUE = 0;
    public static final int PINK = 1;
    public static final int PURPLE = 2;
    public static final int RED = 3;
    public static final int YELLOW = 4;

    private static final String[] NAMES = new String[]{
            "Tube Coral",
            "Brain Coral",
            "Bubble Coral",
            "Fire Coral",
            "Horn Coral",
            "Coral",
            "Coral",
            "Coral",
    };

    public BlockCoral() {
        this(0);
    }

    public BlockCoral(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CORAL;
    }

    @Override
    public String getName() {
        return isDead() ? "Dead " : "" + NAMES[getCoralColor()];
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
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        if (isDead()) {
            return BlockColor.GRAY_BLOCK_COLOR;
        }

        switch (getCoralColor()) {
            default:
            case BLUE:
                return BlockColor.BLUE_BLOCK_COLOR;
            case PINK:
                return BlockColor.PINK_BLOCK_COLOR;
            case PURPLE:
                return BlockColor.PURPLE_BLOCK_COLOR;
            case RED:
                return BlockColor.RED_BLOCK_COLOR;
            case YELLOW:
                return BlockColor.YELLOW_BLOCK_COLOR;
        }
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
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block extra;
        if (block.isLava() || block.isWater() && !block.isFullLiquid()
                || !block.isAir() && (extra = level.getExtraBlock(this)).isWater() && !extra.isFullLiquid()
                || !SupportType.hasCenterSupport(down(), BlockFace.UP)) {
            return false;
        }

        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        if (!isDead()) {
            level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(40, 50));
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!SupportType.hasCenterSupport(down(), BlockFace.UP)) {
                level.useBreakOn(this);
                return type;
            }

            if (isDead()) {
                return 0;
            }

            level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(40, 50));
            return type;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (level.getExtraBlock(this).isWaterSource()) {
                return 0;
            }

            for (BlockFace face : Plane.HORIZONTAL) {
                Block block = getSide(face);
                if (block.isWater() || level.getExtraBlock(block).isWater()) {
                    return 0;
                }
            }

            setDead(true);
            level.setBlock(this, this, true);
            return type;
        }

        return 0;
    }

    public int getCoralColor() {
        return getDamage() & COLOR_MASK;
    }

    public void setCoralColor(int color) {
        setDamage((getDamage() & ~COLOR_MASK) | (color & COLOR_MASK));
    }

    public boolean isDead() {
        return (getDamage() & DEAD_BIT) == DEAD_BIT;
    }

    public void setDead(boolean dead) {
        setDamage(dead ? getDamage() | DEAD_BIT : getDamage() & ~DEAD_BIT);
    }

    protected boolean needCheckAlive() {
        return true;
    }
}
