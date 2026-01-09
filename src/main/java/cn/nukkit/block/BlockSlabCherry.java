package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabCherry extends BlockSlabWood {
    BlockSlabCherry() {

    }

    @Override
    public int getId() {
        return CHERRY_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Cherry Slab" : "Cherry Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WHITE_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return CHERRY_DOUBLE_SLAB;
    }
}
