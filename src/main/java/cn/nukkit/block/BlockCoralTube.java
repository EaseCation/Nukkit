package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralTube extends BlockCoral {
    BlockCoralTube() {

    }

    @Override
    public int getId() {
        return TUBE_CORAL;
    }

    @Override
    public String getName() {
        return "Tube Coral";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLUE_BLOCK_COLOR;
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_TUBE_CORAL;
    }

    @Override
    public String getDescriptionId() {
        return "tile.coral.blue.name";
    }
}
