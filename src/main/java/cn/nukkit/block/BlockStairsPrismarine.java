package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsPrismarine extends BlockStairs {

    BlockStairsPrismarine() {

    }

    @Override
    public int getId() {
        return PRISMARINE_STAIRS;
    }

    @Override
    public String getName() {
        return "Prismarine Stairs";
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
        return BlockColor.CYAN_BLOCK_COLOR;
    }
}
