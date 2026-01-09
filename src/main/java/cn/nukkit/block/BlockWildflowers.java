package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWildflowers extends BlockSegmentable {
    BlockWildflowers() {

    }

    @Override
    public int getId() {
        return WILDFLOWERS;
    }

    @Override
    public String getName() {
        return "Wildflowers";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PLANT_BLOCK_COLOR;
    }
}
