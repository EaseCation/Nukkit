package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralBubble extends BlockCoral {
    BlockCoralBubble() {

    }

    @Override
    public int getId() {
        return BUBBLE_CORAL;
    }

    @Override
    public String getName() {
        return "Bubble Coral";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_BUBBLE_CORAL;
    }
}
