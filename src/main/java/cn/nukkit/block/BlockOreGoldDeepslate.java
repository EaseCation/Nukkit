package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockOreGoldDeepslate extends BlockOreGold {
    public BlockOreGoldDeepslate() {
    }

    @Override
    public int getId() {
        return DEEPSLATE_GOLD_ORE;
    }

    @Override
    public String getName() {
        return "Deepslate Gold Ore";
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
