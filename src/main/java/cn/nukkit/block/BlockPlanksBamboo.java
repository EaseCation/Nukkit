package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPlanksBamboo extends BlockPlanksAbstract {
    public BlockPlanksBamboo() {
    }

    @Override
    public int getId() {
        return BAMBOO_PLANKS;
    }

    @Override
    public String getName() {
        return "Bamboo Planks";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
