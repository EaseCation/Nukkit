package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

public class BlockStairsPolishedAndesite extends BlockStairs {

    public BlockStairsPolishedAndesite() {
        this(0);
    }

    public BlockStairsPolishedAndesite(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_ANDESITE_STAIRS;
    }

    @Override
    public String getName() {
        return "Polished Andesite Stairs";
    }

    @Override
    public float getHardness() {
        if (ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_20_30.isAvailable()) {
            return 1.5f;
        }
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
