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
    public float getHardness() {
        return 4.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_BLOCK_COLOR;
    }
}
