package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralFanBubble extends BlockCoralFan {
    BlockCoralFanBubble() {

    }

    @Override
    public int getId() {
        return BUBBLE_CORAL_FAN;
    }

    @Override
    public String getName() {
        return "Bubble Coral Fan";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }

    @Override
    protected int getWallBlockId(boolean dead) {
        return dead ? DEAD_BUBBLE_CORAL_WALL_FAN : BUBBLE_CORAL_WALL_FAN;
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_BUBBLE_CORAL_FAN;
    }
}
