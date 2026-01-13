package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockNetherRootsWarped extends BlockNetherRoots {
    BlockNetherRootsWarped() {

    }

    @Override
    public int getId() {
        return WARPED_ROOTS;
    }

    @Override
    public String getDescriptionId() {
        return "tile.warped_roots.warpedRoots.name";
    }

    @Override
    public String getName() {
        return "Warped Roots";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }
}
