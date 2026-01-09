package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCommandChain extends BlockCommand {

    BlockCommandChain() {

    }

    @Override
    public int getId() {
        return CHAIN_COMMAND_BLOCK;
    }

    @Override
    public String getName() {
        return "Chain Command Block";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_BLOCK_COLOR;
    }
}
