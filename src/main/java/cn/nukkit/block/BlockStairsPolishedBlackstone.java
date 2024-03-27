package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;

public class BlockStairsPolishedBlackstone extends BlockStairs {
    public BlockStairsPolishedBlackstone() {
        this(0);
    }

    public BlockStairsPolishedBlackstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_STAIRS;
    }

    @Override
    public String getName() {
        return "Polished Blackstone Stairs";
    }

    @Override
    public float getHardness() {
        if (V1_20_30.isAvailable()) {
            return 2;
        }
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
