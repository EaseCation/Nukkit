package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockCoralFanHang extends BlockFlowable implements Faceable {
    public static final int[] WALL_CORAL_FANS = {
            TUBE_CORAL_WALL_FAN,
            BRAIN_CORAL_WALL_FAN,
            DEAD_TUBE_CORAL_WALL_FAN,
            DEAD_BRAIN_CORAL_WALL_FAN,
    };
    public static final int[] DEAD_WALL_CORAL_FANS = {
            DEAD_TUBE_CORAL_WALL_FAN,
            DEAD_BRAIN_CORAL_WALL_FAN,
    };
    public static final int[] WALL_CORAL_FANS2 = {
            BUBBLE_CORAL_WALL_FAN,
            FIRE_CORAL_WALL_FAN,
            DEAD_BUBBLE_CORAL_WALL_FAN,
            DEAD_FIRE_CORAL_WALL_FAN,
    };
    public static final int[] DEAD_WALL_CORAL_FANS2 = {
            DEAD_BUBBLE_CORAL_WALL_FAN,
            DEAD_FIRE_CORAL_WALL_FAN,
    };
    public static final int[] WALL_CORAL_FANS3 = {
            HORN_CORAL_WALL_FAN,
            HORN_CORAL_WALL_FAN,
            DEAD_HORN_CORAL_WALL_FAN,
            DEAD_HORN_CORAL_WALL_FAN,
    };
    public static final int[] DEAD_WALL_CORAL_FANS3 = {
            DEAD_HORN_CORAL_WALL_FAN,
            DEAD_HORN_CORAL_WALL_FAN,
    };

    @Deprecated
    public static final int TYPE_BIT = 0b1;
    @Deprecated
    public static final int DEAD_BIT = 0b10;
    public static final int DIRECTION_MASK = 0b11;

    public static final int BLUE = 0;
    public static final int PINK = 1;

    public static final int PURPLE = 0;
    public static final int RED = 1;

    public static final int YELLOW = 0;

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            BlockFace face = getBlockFace();
            if (!SupportType.hasFullSupport(getSide(face.getOpposite()), face)) {
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

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromReversedHorizontalIndex(getDirection()).getOpposite();
    }

    public int getDirection() {
        return getDamage();
    }

    public void setDirection(int direction) {
        setDamage(direction & DIRECTION_MASK);
    }

    protected abstract int getDeadBlockId();
}
