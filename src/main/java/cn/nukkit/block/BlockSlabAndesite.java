package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabAndesite extends BlockSlabStone {
    BlockSlabAndesite() {

    }

    @Override
    public int getId() {
        return ANDESITE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Andesite Slab" : "Andesite Slab";
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
        return ANDESITE_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab3.andesite.name";
    }
}
