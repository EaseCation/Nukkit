package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabCinnabar extends BlockSlabStone {
    BlockSlabCinnabar() {
    }

    @Override
    public int getId() {
        return CINNABAR_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Cinnabar Slab" : "Cinnabar Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return CINNABAR_DOUBLE_SLAB;
    }
}
