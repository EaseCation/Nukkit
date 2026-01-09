package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabDeepslateCobbled extends BlockSlabStone {
    BlockSlabDeepslateCobbled() {

    }

    @Override
    public int getId() {
        return COBBLED_DEEPSLATE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Cobbled Deepslate Slab" : "Cobbled Deepslate Slab";
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
        return COBBLED_DEEPSLATE_DOUBLE_SLAB;
    }
}
