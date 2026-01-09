package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogStrippedAcacia extends BlockLogStripped {

    BlockLogStrippedAcacia() {

    }

    @Override
    public int getId() {
        return STRIPPED_ACACIA_LOG;
    }

    @Override
    public String getName() {
        return "Stripped Acacia Log";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
