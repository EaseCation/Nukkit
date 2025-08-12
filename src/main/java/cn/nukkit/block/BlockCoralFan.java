package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockCoralFan extends BlockFlowable {

    public static final int COLOR_MASK = 0b111;
    public static final int COLOR_BITS = 3;
    public static final int DIRECTION_BIT = 0b1000;

    public static final int BLUE = 0;
    public static final int PINK = 1;
    public static final int PURPLE = 2;
    public static final int RED = 3;
    public static final int YELLOW = 4;

    public static final int DIRECTION_X_AXIS = 0;
    public static final int DIRECTION_Z_AXIS = 1;

    private static final String[] NAMES = new String[]{
            "Tube Coral Fan",
            "Brain Coral Fan",
            "Bubble Coral Fan",
            "Fire Coral Fan",
            "Horn Coral Fan",
            "Coral Fan",
            "Coral Fan",
            "Coral Fan",
    };

    public BlockCoralFan() {
        this(0);
    }

    public BlockCoralFan(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CORAL_FAN;
    }

    @Override
    public String getName() {
        return NAMES[getCoralColor()];
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public BlockColor getColor() {
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
    public boolean canContainWater() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), getDamage() & COLOR_MASK);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        Block extra = null;
        if (block.getId() == SEAGRASS || block.isLava() || block.isWater() && !block.isFullLiquid()
                || !block.isAir() && (extra = level.getExtraBlock(this)).isWater() && !extra.isFullLiquid()) {
            return false;
        }

        switch (face) {
            case DOWN:
                return false;
            case UP:
                if (!SupportType.hasFullSupport(target, face)) {
                    return false;
                }

                if (player != null) {
                    Vector3 direction = subtract(player.floor()).normalize();
                    double angle = Math.toDegrees(Mth.atan2(direction.getZ(), direction.getX()));
                    // vanilla bug see https://bugs.mojang.com/browse/MCPE-125311
                    if (angle <= 45 || 315 <= angle || (135 <= angle && angle <= 225)) {
                        setDirection(DIRECTION_Z_AXIS);
                    } else {
                        setDirection(DIRECTION_X_AXIS);
                    }
                }

                if (block.isWater() || extra != null && extra.isWater() && !extra.isLiquidSource()) {
                    level.setExtraBlock(this, get(FLOWING_WATER), true, false);
                }

                if (!block.isWater() && (extra == null || !extra.isWater())) {
                    return level.setBlock(this, get(CORAL_FAN_DEAD, getDamage()), true);
                }

                if (!super.place(item, block, target, face, fx, fy, fz, player)) {
                    return false;
                }

                level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(40, 50));
                return true;
            default:
                if (!SupportType.hasFullSupport(target, face)) {
                    return false;
                }

                if (block.isWater() || extra != null && extra.isWater() && !extra.isLiquidSource()) {
                    level.setExtraBlock(this, get(FLOWING_WATER), true, false);
                }

                int direction;
                switch (face) {
                    default:
                    case WEST:
                        direction = 0;
                        break;
                    case EAST:
                        direction = 1;
                        break;
                    case NORTH:
                        direction = 2;
                        break;
                    case SOUTH:
                        direction = 3;
                        break;
                }

                Block hangBlock = getHangBlock(direction);
                boolean dead = false;
                if (!block.isWater() && (extra == null || !extra.isWater())) {
                    dead = true;
                    hangBlock.setDamage(hangBlock.getDamage() | BlockCoralFanHang.DEAD_BIT);
                }

                if (!level.setBlock(this, hangBlock, true)) {
                    return false;
                }

                if (!dead) {
                    level.scheduleRandomUpdate(hangBlock, this, ThreadLocalRandom.current().nextInt(40, 50));
                }
                return true;
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!SupportType.hasFullSupport(down(), BlockFace.UP)) {
                level.useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            if (!needCheckAlive()) {
                return 0;
            }

            level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(40, 50));
            return type;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (level.getExtraBlock(this).isWaterSource()) {
                return 0;
            }

            level.setBlock(this, get(CORAL_FAN_DEAD, getDamage()), true);
            return type;
        }

        return 0;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    protected boolean needCheckAlive() {
        return true;
    }

    protected Block getHangBlock(int direction) {
        switch (getCoralColor()) {
            default:
            case BLUE:
                return Block.get(CORAL_FAN_HANG, BlockCoralFanHang.BLUE | (direction << BlockCoralFanHang.TYPE_DEAD_BITS));
            case PINK:
                return Block.get(CORAL_FAN_HANG, BlockCoralFanHang.PINK | (direction << BlockCoralFanHang.TYPE_DEAD_BITS));
            case PURPLE:
                return Block.get(CORAL_FAN_HANG2, BlockCoralFanHang2.PURPLE | (direction << BlockCoralFanHang.TYPE_DEAD_BITS));
            case RED:
                return Block.get(CORAL_FAN_HANG2, BlockCoralFanHang2.RED | (direction << BlockCoralFanHang.TYPE_DEAD_BITS));
            case YELLOW:
                return Block.get(CORAL_FAN_HANG3, BlockCoralFanHang3.YELLOW | (direction << BlockCoralFanHang.TYPE_DEAD_BITS));
        }
    }

    public int getCoralColor() {
        return getDamage() & COLOR_MASK;
    }

    public void setCoralColor(int color) {
        setDamage((getDamage() & ~COLOR_MASK) | (color & COLOR_MASK));
    }

    public int getDirection() {
        return (getDamage() & DIRECTION_BIT) >> COLOR_BITS;
    }

    public void setDirection(int direction) {
        setDamage((getDamage() & ~DIRECTION_BIT) | ((direction << COLOR_BITS) & DIRECTION_BIT));
    }
}
