package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabPrismarine extends BlockSlabStone {
    BlockSlabPrismarine() {

    }

    @Override
    public int getId() {
        return PRISMARINE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Prismarine Slab" : "Prismarine Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return PRISMARINE_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab2.prismarine.rough.name";
    }
}
