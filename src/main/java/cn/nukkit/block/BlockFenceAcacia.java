package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceAcacia extends BlockFence {
    BlockFenceAcacia() {

    }

    @Override
    public int getId() {
        return ACACIA_FENCE;
    }

    @Override
    public String getName() {
        return "Acacia Fence";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public String getDescriptionId() {
        return "tile.acaciaFence.name";
    }
}
