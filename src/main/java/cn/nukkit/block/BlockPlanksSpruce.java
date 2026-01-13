package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPlanksSpruce extends BlockPlanks {
    BlockPlanksSpruce() {

    }

    @Override
    public int getId() {
        return SPRUCE_PLANKS;
    }

    @Override
    public String getName() {
        return "Spruce Planks";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PODZOL_BLOCK_COLOR;
    }

    @Override
    public String getDescriptionId() {
        return "tile.planks.spruce.name";
    }
}
