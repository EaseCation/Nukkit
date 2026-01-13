package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralBlockDeadTube extends BlockCoralBlockDead {
    BlockCoralBlockDeadTube() {

    }

    @Override
    public int getId() {
        return DEAD_TUBE_CORAL_BLOCK;
    }

    @Override
    public String getName() {
        return "Dead Tube Coral Block";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }

    @Override
    public String getDescriptionId() {
        return "tile.coral_block.blue_dead.name";
    }
}
