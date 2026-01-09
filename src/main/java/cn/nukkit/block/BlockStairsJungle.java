package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockStairsJungle extends BlockStairsWood {

    BlockStairsJungle() {

    }

    @Override
    public int getId() {
        return JUNGLE_STAIRS;
    }

    @Override
    public String getName() {
        return "Jungle Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

}
