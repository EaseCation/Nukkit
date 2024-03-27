package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsSmoothSandstone extends BlockStairs {

    public BlockStairsSmoothSandstone() {
        this(0);
    }

    public BlockStairsSmoothSandstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SMOOTH_SANDSTONE_STAIRS;
    }

    @Override
    public String getName() {
        return "Smooth Sandstone Stairs";
    }

    @Override
    public float getHardness() {
        return 2;
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
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
