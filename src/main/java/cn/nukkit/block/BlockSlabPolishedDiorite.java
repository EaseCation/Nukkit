package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabPolishedDiorite extends BlockSlabStone {
    BlockSlabPolishedDiorite() {

    }

    @Override
    public int getId() {
        return POLISHED_DIORITE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Polished Diorite Slab" : "Polished Diorite Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return POLISHED_DIORITE_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab3.diorite.smooth.name";
    }
}
