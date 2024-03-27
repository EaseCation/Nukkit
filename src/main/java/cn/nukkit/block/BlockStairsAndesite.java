package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

public class BlockStairsAndesite extends BlockStairs {

    public BlockStairsAndesite() {
        this(0);
    }

    public BlockStairsAndesite(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ANDESITE_STAIRS;
    }

    @Override
    public String getName() {
        return "Andesite Stairs";
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
