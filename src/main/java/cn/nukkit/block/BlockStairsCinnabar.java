package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsCinnabar extends BlockStairs {
    BlockStairsCinnabar() {
    }

    @Override
    public int getId() {
        return CINNABAR_STAIRS;
    }

    @Override
    public String getName() {
        return "Cinnabar Stairs";
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
        return BlockColor.RED_BLOCK_COLOR;
    }
}
