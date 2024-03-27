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
    public float getHardness() {
        return 4.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_BLOCK_COLOR;
    }
}
