package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockNetherRootsCrimson extends BlockNetherRoots {
    protected BlockNetherRootsCrimson() {
    }

    @Override
    public int getId() {
        return CRIMSON_ROOTS;
    }

    @Override
    public String getName() {
        return "Crimson Roots";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHER_BLOCK_COLOR;
    }
}
