package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPlanksAcacia extends BlockPlanks {
    BlockPlanksAcacia() {

    }

    @Override
    public int getId() {
        return ACACIA_PLANKS;
    }

    @Override
    public String getName() {
        return "Acacia Planks";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
