package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

public class BlockStairsDiorite extends BlockStairs {

    public BlockStairsDiorite() {
        this(0);
    }

    public BlockStairsDiorite(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DIORITE_STAIRS;
    }

    @Override
    public String getName() {
        return "Diorite Stairs";
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
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }
}
