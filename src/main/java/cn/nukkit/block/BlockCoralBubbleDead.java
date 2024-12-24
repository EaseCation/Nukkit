package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralBubbleDead extends BlockCoral {
    public BlockCoralBubbleDead() {
    }

    @Override
    public int getId() {
        return DEAD_BUBBLE_CORAL;
    }

    @Override
    public String getName() {
        return "Dead Bubble Coral";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }

    @Override
    protected boolean isDead() {
        return true;
    }
}
