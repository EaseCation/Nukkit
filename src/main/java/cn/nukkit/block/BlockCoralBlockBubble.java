package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralBlockBubble extends BlockCoralBlock {
    BlockCoralBlockBubble() {

    }

    @Override
    public int getId() {
        return BUBBLE_CORAL_BLOCK;
    }

    @Override
    public String getName() {
        return "Bubble Coral Block";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_BUBBLE_CORAL_BLOCK;
    }
}
