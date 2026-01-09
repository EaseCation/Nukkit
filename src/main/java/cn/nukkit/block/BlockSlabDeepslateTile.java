package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabDeepslateTile extends BlockSlabStone {
    BlockSlabDeepslateTile() {

    }

    @Override
    public int getId() {
        return DEEPSLATE_TILE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Deepslate Tile Slab" : "Deepslate Tile Slab";
    }

    @Override
    public float getHardness() {
        return 3.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return DEEPSLATE_TILE_DOUBLE_SLAB;
    }
}
