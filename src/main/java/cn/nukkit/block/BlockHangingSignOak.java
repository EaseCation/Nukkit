package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockHangingSignOak extends BlockHangingSign {
    BlockHangingSignOak() {

    }

    @Override
    public int getId() {
        return OAK_HANGING_SIGN;
    }

    @Override
    public String getName() {
        return "Oak Hanging Sign";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
