package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabPurpur extends BlockSlabStone {
    BlockSlabPurpur() {

    }

    @Override
    public int getId() {
        return PURPUR_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Purpur Slab" : "Purpur Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return PURPUR_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab2.purpur.name";
    }
}
