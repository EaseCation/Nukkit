package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsSulfur extends BlockStairs {
    BlockStairsSulfur() {
    }

    @Override
    public int getId() {
        return SULFUR_STAIRS;
    }

    @Override
    public String getName() {
        return "Sulfur Stairs";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
