package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralBlockDeadHorn extends BlockCoralBlockDead {
    BlockCoralBlockDeadHorn() {

    }

    @Override
    public int getId() {
        return DEAD_HORN_CORAL_BLOCK;
    }

    @Override
    public String getName() {
        return "Dead Horn Coral Block";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }

    @Override
    public String getDescriptionId() {
        return "tile.coral_block.yellow_dead.name";
    }
}
