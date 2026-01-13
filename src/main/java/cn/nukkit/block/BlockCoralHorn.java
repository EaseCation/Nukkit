package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralHorn extends BlockCoral {
    BlockCoralHorn() {

    }

    @Override
    public int getId() {
        return HORN_CORAL;
    }

    @Override
    public String getName() {
        return "Horn Coral";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_HORN_CORAL;
    }

    @Override
    public String getDescriptionId() {
        return "tile.coral.yellow.name";
    }
}
