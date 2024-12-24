package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsTuffBrick extends BlockStairs {
    public BlockStairsTuffBrick() {
        this(0);
    }

    public BlockStairsTuffBrick(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return TUFF_BRICK_STAIRS;
    }

    @Override
    public String getName() {
        return "Tuff Brick Stairs";
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
        return BlockColor.GRAY_TERRACOTA_BLOCK_COLOR;
    }
}
