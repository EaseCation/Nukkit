package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsBamboo extends BlockStairsWood {
    BlockStairsBamboo() {

    }

    @Override
    public int getId() {
        return BAMBOO_STAIRS;
    }

    @Override
    public String getName() {
        return "Bamboo Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
