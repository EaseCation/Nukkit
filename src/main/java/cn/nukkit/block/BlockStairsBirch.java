package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockStairsBirch extends BlockStairsWood {

    BlockStairsBirch() {

    }

    @Override
    public int getId() {
        return BIRCH_STAIRS;
    }

    @Override
    public String getName() {
        return "Birch Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

}
