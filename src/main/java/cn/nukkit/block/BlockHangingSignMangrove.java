package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockHangingSignMangrove extends BlockHangingSign {
    BlockHangingSignMangrove() {

    }

    @Override
    public int getId() {
        return MANGROVE_HANGING_SIGN;
    }

    @Override
    public String getName() {
        return "Mangrove Hanging Sign";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
