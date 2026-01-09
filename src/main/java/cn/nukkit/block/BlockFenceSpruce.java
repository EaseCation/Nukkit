package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceSpruce extends BlockFence {
    BlockFenceSpruce() {

    }

    @Override
    public int getId() {
        return SPRUCE_FENCE;
    }

    @Override
    public String getName() {
        return "Spruce Fence";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PODZOL_BLOCK_COLOR;
    }
}
