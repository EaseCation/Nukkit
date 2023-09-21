package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockOreDiamondDeepslate extends BlockOreDiamond {
    public BlockOreDiamondDeepslate() {
    }

    @Override
    public int getId() {
        return DEEPSLATE_DIAMOND_ORE;
    }

    @Override
    public String getName() {
        return "Deepslate Diamond Ore";
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
