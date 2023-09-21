package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockOreCopperDeepslate extends BlockOreCopper {
    public BlockOreCopperDeepslate() {
    }

    @Override
    public int getId() {
        return DEEPSLATE_COPPER_ORE;
    }

    @Override
    public String getName() {
        return "Deepslate Copper Ore";
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_BLOCK_COLOR;
    }
}
