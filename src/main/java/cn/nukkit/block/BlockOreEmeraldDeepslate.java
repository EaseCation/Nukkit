package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockOreEmeraldDeepslate extends BlockOreEmerald {
    public BlockOreEmeraldDeepslate() {
    }

    @Override
    public int getId() {
        return DEEPSLATE_EMERALD_ORE;
    }

    @Override
    public String getName() {
        return "Deepslate Emerald Ore";
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
