package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsDeepslateBrick extends BlockStairs {
    public BlockStairsDeepslateBrick() {
        this(0);
    }

    public BlockStairsDeepslateBrick(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DEEPSLATE_BRICK_STAIRS;
    }

    @Override
    public String getName() {
        return "Deepslate Brick Stairs";
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
