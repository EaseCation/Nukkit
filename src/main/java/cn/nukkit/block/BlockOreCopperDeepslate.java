package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockOreCopperDeepslate extends BlockOreCopper {
    BlockOreCopperDeepslate() {

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
    public float getHardness() {
        return 4.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_BLOCK_COLOR;
    }
}
