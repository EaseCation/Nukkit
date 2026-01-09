package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralBlockDeadBubble extends BlockCoralBlockDead {
    BlockCoralBlockDeadBubble() {

    }

    @Override
    public int getId() {
        return DEAD_BUBBLE_CORAL_BLOCK;
    }

    @Override
    public String getName() {
        return "Dead Bubble Coral Block";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }
}
