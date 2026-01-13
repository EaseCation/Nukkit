package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabNormalStone extends BlockSlabStone {
    BlockSlabNormalStone() {

    }

    @Override
    public int getId() {
        return NORMAL_STONE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Stone Slab" : "Stone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return NORMAL_STONE_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab4.stone.name";
    }
}
