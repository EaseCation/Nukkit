package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockStairsDarkOak extends BlockStairsWood {

    BlockStairsDarkOak() {

    }

    @Override
    public int getId() {
        return DARK_OAK_STAIRS;
    }

    @Override
    public String getName() {
        return "Dark Oak Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }

}
