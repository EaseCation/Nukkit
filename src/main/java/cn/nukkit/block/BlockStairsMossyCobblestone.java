package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsMossyCobblestone extends BlockStairs {

    public BlockStairsMossyCobblestone() {
        this(0);
    }

    public BlockStairsMossyCobblestone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MOSSY_COBBLESTONE_STAIRS;
    }

    @Override
    public String getName() {
        return "Mossy Cobblestone Stairs";
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
        return BlockColor.STONE_BLOCK_COLOR;
    }
}
