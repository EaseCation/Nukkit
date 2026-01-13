package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockNetherRootsCrimson extends BlockNetherRoots {
    BlockNetherRootsCrimson() {

    }

    @Override
    public int getId() {
        return CRIMSON_ROOTS;
    }

    @Override
    public String getDescriptionId() {
        return "tile.crimson_roots.crimsonRoots.name";
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
