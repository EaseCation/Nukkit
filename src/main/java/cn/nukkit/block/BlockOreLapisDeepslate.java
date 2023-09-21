package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockOreLapisDeepslate extends BlockOreLapis {
    public BlockOreLapisDeepslate() {
    }

    @Override
    public int getId() {
        return DEEPSLATE_LAPIS_ORE;
    }

    @Override
    public String getName() {
        return "Deepslate Lapis Lazuli Ore";
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
