package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPlanksOak extends BlockPlanks {
    BlockPlanksOak() {

    }

    @Override
    public int getId() {
        return OAK_PLANKS;
    }

    @Override
    public String getName() {
        return "Oak Planks";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
