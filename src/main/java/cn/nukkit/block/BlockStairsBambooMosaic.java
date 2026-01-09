package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsBambooMosaic extends BlockStairsWood {
    BlockStairsBambooMosaic() {

    }

    @Override
    public int getId() {
        return BAMBOO_MOSAIC_STAIRS;
    }

    @Override
    public String getName() {
        return "Bamboo Mosaic Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
