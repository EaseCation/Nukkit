package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsDeepslateCobbled extends BlockStairs {
    BlockStairsDeepslateCobbled() {

    }

    @Override
    public int getId() {
        return COBBLED_DEEPSLATE_STAIRS;
    }

    @Override
    public String getName() {
        return "Cobbled Deepslate Stairs";
    }

    @Override
    public float getHardness() {
        return 3.5f;
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
        return BlockColor.DEEPSLATE_BLOCK_COLOR;
    }
}
