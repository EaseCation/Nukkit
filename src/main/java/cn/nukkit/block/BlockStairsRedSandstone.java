package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public class BlockStairsRedSandstone extends BlockStairs {

    public BlockStairsRedSandstone() {
        this(0);
    }

    public BlockStairsRedSandstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return RED_SANDSTONE_STAIRS;
    }

    @Override
    public float getHardness() {
        return 0.8f;
    }

    @Override
    public float getResistance() {
        return 4;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public String getName() {
        return "Red Sandstone Stairs";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}