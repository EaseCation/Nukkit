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
    public float getHardness() {
        return 4.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_BLOCK_COLOR;
    }
}
