package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabDeepslatePolished extends BlockSlabStone {
    BlockSlabDeepslatePolished() {

    }

    @Override
    public int getId() {
        return POLISHED_DEEPSLATE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Polished Deepslate Slab" : "Polished Deepslate Slab";
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
        return POLISHED_DEEPSLATE_DOUBLE_SLAB;
    }
}
