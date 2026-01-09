package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabAcacia extends BlockSlabWood {
    BlockSlabAcacia() {

    }

    @Override
    public int getId() {
        return ACACIA_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Acacia Slab" : "Acacia Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return ACACIA_DOUBLE_SLAB;
    }
}
