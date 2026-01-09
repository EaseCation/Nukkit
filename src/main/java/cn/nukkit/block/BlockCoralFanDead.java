package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public abstract class BlockCoralFanDead extends BlockCoralFan {
    public static final int[] DEAD_CORAL_FANS = {
            DEAD_TUBE_CORAL_FAN,
            DEAD_BRAIN_CORAL_FAN,
            DEAD_BUBBLE_CORAL_FAN,
            DEAD_FIRE_CORAL_FAN,
            DEAD_HORN_CORAL_FAN,
    };
    @SuppressWarnings("unused")
    private static final int[] CORAL_FANS = DEAD_CORAL_FANS;

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }

    @Override
    public boolean isDeadCoral() {
        return true;
    }

    @Override
    protected int getDeadBlockId() {
        return getId();
    }
}
