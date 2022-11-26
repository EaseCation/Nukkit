package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPlanksMangrove extends BlockPlanksAbstract {
    public BlockPlanksMangrove() {
    }

    @Override
    public int getId() {
        return MANGROVE_PLANKS;
    }

    @Override
    public String getName() {
        return "Mangrove Planks";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
