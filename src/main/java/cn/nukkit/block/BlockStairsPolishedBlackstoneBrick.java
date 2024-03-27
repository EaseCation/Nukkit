package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsPolishedBlackstoneBrick extends BlockStairs {
    public BlockStairsPolishedBlackstoneBrick() {
        this(0);
    }

    public BlockStairsPolishedBlackstoneBrick(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_BRICK_STAIRS;
    }

    @Override
    public String getName() {
        return "Polished Blackstone Brick Stairs";
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
        return BlockColor.BLACK_BLOCK_COLOR;
    }
}
