package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsPolishedTuff extends BlockStairs {
    public BlockStairsPolishedTuff() {
        this(0);
    }

    public BlockStairsPolishedTuff(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_TUFF_STAIRS;
    }

    @Override
    public String getName() {
        return "Polished Tuff Stairs";
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
