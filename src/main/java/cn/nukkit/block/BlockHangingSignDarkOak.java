package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockHangingSignDarkOak extends BlockHangingSign {
    BlockHangingSignDarkOak() {

    }

    @Override
    public int getId() {
        return DARK_OAK_HANGING_SIGN;
    }

    @Override
    public String getName() {
        return "Dark Oak Hanging Sign";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }
}
