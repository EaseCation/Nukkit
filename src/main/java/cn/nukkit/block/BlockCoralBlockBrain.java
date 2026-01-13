package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralBlockBrain extends BlockCoralBlock {
    BlockCoralBlockBrain() {

    }

    @Override
    public int getId() {
        return BRAIN_CORAL_BLOCK;
    }

    @Override
    public String getName() {
        return "Brain Coral Block";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PINK_BLOCK_COLOR;
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_BRAIN_CORAL_BLOCK;
    }

    @Override
    public String getDescriptionId() {
        return "tile.coral_block.pink.name";
    }
}
