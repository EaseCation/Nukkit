package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockHangingSignJungle extends BlockHangingSign {
    BlockHangingSignJungle() {

    }

    @Override
    public int getId() {
        return JUNGLE_HANGING_SIGN;
    }

    @Override
    public String getName() {
        return "Jungle Hanging Sign";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
