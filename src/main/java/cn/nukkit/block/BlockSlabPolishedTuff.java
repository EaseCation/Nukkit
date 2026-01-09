package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabPolishedTuff extends BlockSlabStone {
    BlockSlabPolishedTuff() {

    }

    @Override
    public int getId() {
        return POLISHED_TUFF_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Polished Tuff Slab" : "Polished Tuff Slab";
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
        return POLISHED_TUFF_DOUBLE_SLAB;
    }
}
