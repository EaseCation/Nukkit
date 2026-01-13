package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabGranite extends BlockSlabStone {
    BlockSlabGranite() {

    }

    @Override
    public int getId() {
        return GRANITE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Granite Slab" : "Granite Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return GRANITE_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab3.granite.name";
    }
}
