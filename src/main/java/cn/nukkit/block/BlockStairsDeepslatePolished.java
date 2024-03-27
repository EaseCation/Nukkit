package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsDeepslatePolished extends BlockStairs {
    public BlockStairsDeepslatePolished() {
        this(0);
    }

    public BlockStairsDeepslatePolished(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_DEEPSLATE_STAIRS;
    }

    @Override
    public String getName() {
        return "Polished Deepslate Stairs";
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
