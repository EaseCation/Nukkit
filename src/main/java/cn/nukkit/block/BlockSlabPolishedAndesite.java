package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabPolishedAndesite extends BlockSlabStone {
    BlockSlabPolishedAndesite() {

    }

    @Override
    public int getId() {
        return POLISHED_ANDESITE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Polished Andesite Slab" : "Polished Andesite Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return POLISHED_ANDESITE_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab3.andesite.smooth.name";
    }
}
