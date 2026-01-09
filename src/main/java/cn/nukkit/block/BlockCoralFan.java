package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockCoralFan extends BlockFlowable {
    public static final int[] CORAL_FANS = {
            TUBE_CORAL_FAN,
            BRAIN_CORAL_FAN,
            BUBBLE_CORAL_FAN,
            FIRE_CORAL_FAN,
            HORN_CORAL_FAN,
    };

    @Deprecated
    public static final int COLOR_MASK = 0b111;
    public static final int DIRECTION_BIT = 0b1;

    public static final int BLUE = 0;
    public static final int PINK = 1;
    public static final int PURPLE = 2;
    public static final int RED = 3;
    public static final int YELLOW = 4;

    private static final int DIRECTION_X_AXIS = 0;
    private static final int DIRECTION_Z_AXIS = 1;

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
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
                    setDirectionType(angle <= 45 || 315 <= angle || 135 <= angle && angle <= 225);
                }

                if (block.isWater() || extra != null && extra.isWater() && !extra.isLiquidSource()) {
                    level.setExtraBlock(this, get(FLOWING_WATER), true, false);
                }

                if (!block.isWater() && (extra == null || !extra.isWater())) {
                    return level.setBlock(this, get(getDeadBlockId(), getDamage()), true);
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

                boolean dead = !block.isWater() && (extra == null || !extra.isWater());

                Block hangBlock = get(getWallBlockId(dead), direction);
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

            if (isDeadCoral()) {
                return 0;
            }

            level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(40, 50));
            return type;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (level.getExtraBlock(this).isWaterSource()) {
                return 0;
            }

            level.setBlock(this, get(getDeadBlockId(), getDamage()), true);
            return type;
        }

        return 0;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    protected abstract int getWallBlockId(boolean dead);

    protected abstract int getDeadBlockId();

    /**
     * @return {@code true} if the direction is Z axis, {@code false} if X axis
     */
    public boolean getDirectionType() {
        return getDamage() == DIRECTION_Z_AXIS;
    }

    /**
     * @param zAxis {@code true} to set Z axis, {@code false} to set X axis
     */
    public void setDirectionType(boolean zAxis) {
        setDamage(zAxis ? DIRECTION_Z_AXIS : DIRECTION_X_AXIS);
    }
}
