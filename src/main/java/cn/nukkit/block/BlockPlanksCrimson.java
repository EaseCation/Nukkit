package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPlanksCrimson extends BlockPlanksAbstract {
    public BlockPlanksCrimson() {
    }

    @Override
    public int getId() {
        return CRIMSON_PLANKS;
    }

    @Override
    public String getName() {
        return "Crimson Planks";
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CRIMSON_STEM_BLOCK_COLOR;
    }

    @Override
    public int getFuelTime() {
        return 0;
    }
}
