package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockHangingSignAcacia extends BlockHangingSign {
    BlockHangingSignAcacia() {

    }

    @Override
    public int getId() {
        return ACACIA_HANGING_SIGN;
    }

    @Override
    public String getName() {
        return "Acacia Hanging Sign";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
