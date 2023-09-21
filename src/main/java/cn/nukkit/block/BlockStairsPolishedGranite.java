package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

public class BlockStairsPolishedGranite extends BlockStairs {

    public BlockStairsPolishedGranite() {
        this(0);
    }

    public BlockStairsPolishedGranite(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_GRANITE_STAIRS;
    }

    @Override
    public String getName() {
        return "Polished Granite Stairs";
    }

    @Override
    public double getHardness() {
        if (ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_20_30.isAvailable()) {
            return 1.5;
        }
        return 2;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
