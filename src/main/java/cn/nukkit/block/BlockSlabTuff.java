package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabTuff extends BlockSlabStone {
    BlockSlabTuff() {

    }

    @Override
    public int getId() {
        return TUFF_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Tuff Slab" : "Tuff Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return TUFF_DOUBLE_SLAB;
    }
}
