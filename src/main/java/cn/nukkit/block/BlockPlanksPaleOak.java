package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPlanksPaleOak extends BlockPlanksAbstract {
    public BlockPlanksPaleOak() {
    }

    @Override
    public int getId() {
        return PALE_OAK_PLANKS;
    }

    @Override
    public String getName() {
        return "Pale Oak Planks";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }
}
