package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockOreIronDeepslate extends BlockOreIron {
    public BlockOreIronDeepslate() {
    }

    @Override
    public int getId() {
        return DEEPSLATE_IRON_ORE;
    }

    @Override
    public String getName() {
        return "Deepslate Iron Ore";
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

    @Override
    public double getResistance() {
        return 9;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_BLOCK_COLOR;
    }
}