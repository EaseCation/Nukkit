package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralBrain extends BlockCoral {
    BlockCoralBrain() {

    }

    @Override
    public int getId() {
        return BRAIN_CORAL;
    }

    @Override
    public String getName() {
        return "Brain Coral";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PINK_BLOCK_COLOR;
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_BRAIN_CORAL;
    }
}
