package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPlanksBirch extends BlockPlanks {
    BlockPlanksBirch() {

    }

    @Override
    public int getId() {
        return BIRCH_PLANKS;
    }

    @Override
    public String getName() {
        return "Birch Planks";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public String getDescriptionId() {
        return "tile.planks.birch.name";
    }
}
