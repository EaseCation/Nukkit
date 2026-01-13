package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabDarkOak extends BlockSlabWood {
    BlockSlabDarkOak() {

    }

    @Override
    public int getId() {
        return DARK_OAK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Dark Oak Slab" : "Dark Oak Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return DARK_OAK_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.wooden_slab.big_oak.name";
    }
}
