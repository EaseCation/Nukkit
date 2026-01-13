package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralBlockDeadBrain extends BlockCoralBlockDead {
    BlockCoralBlockDeadBrain() {

    }

    @Override
    public int getId() {
        return DEAD_BRAIN_CORAL_BLOCK;
    }

    @Override
    public String getName() {
        return "Dead Brain Coral Block";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }

    @Override
    public String getDescriptionId() {
        return "tile.coral_block.pink_dead.name";
    }
}
