package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockHangingSignSpruce extends BlockHangingSign {
    BlockHangingSignSpruce() {

    }

    @Override
    public int getId() {
        return SPRUCE_HANGING_SIGN;
    }

    @Override
    public String getName() {
        return "Spruce Hanging Sign";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PODZOL_BLOCK_COLOR;
    }
}
