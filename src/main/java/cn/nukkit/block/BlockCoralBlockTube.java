package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralBlockTube extends BlockCoralBlock {
    BlockCoralBlockTube() {

    }

    @Override
    public int getId() {
        return TUBE_CORAL_BLOCK;
    }

    @Override
    public String getName() {
        return "Tube Coral Block";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLUE_BLOCK_COLOR;
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_TUBE_CORAL_BLOCK;
    }

    @Override
    public String getDescriptionId() {
        return "tile.coral_block.blue.name";
    }
}
