package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabCobblestone extends BlockSlabStone {
    BlockSlabCobblestone() {

    }

    @Override
    public int getId() {
        return COBBLESTONE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Cobblestone Slab" : "Cobblestone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return COBBLESTONE_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab.cobble.name";
    }
}
