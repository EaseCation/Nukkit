package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPlanksDarkOak extends BlockPlanks {
    BlockPlanksDarkOak() {

    }

    @Override
    public int getId() {
        return DARK_OAK_PLANKS;
    }

    @Override
    public String getName() {
        return "Dark Oak Planks";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }

    @Override
    public String getDescriptionId() {
        return "tile.planks.big_oak.name";
    }
}
