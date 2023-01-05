package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockOreCoalDeepslate extends BlockOreCoal {
    public BlockOreCoalDeepslate() {
    }

    @Override
    public int getId() {
        return DEEPSLATE_COAL_ORE;
    }

    @Override
    public String getName() {
        return "Deepslate Coal Ore";
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