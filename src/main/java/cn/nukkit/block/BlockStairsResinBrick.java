package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsResinBrick extends BlockStairs {
    BlockStairsResinBrick() {

    }

    @Override
    public int getId() {
        return RESIN_BRICK_STAIRS;
    }

    @Override
    public String getName() {
        return "Resin Brick Stairs";
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
        return BlockColor.ORANGE_TERRACOTA_BLOCK_COLOR;
    }
}
