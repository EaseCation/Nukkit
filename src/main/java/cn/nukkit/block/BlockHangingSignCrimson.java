package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockHangingSignCrimson extends BlockHangingSign {
    public BlockHangingSignCrimson() {
        this(0);
    }

    public BlockHangingSignCrimson(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CRIMSON_HANGING_SIGN;
    }

    @Override
    public String getName() {
        return "Crimson Hanging Sign";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHER_BLOCK_COLOR;
    }

    @Override
    public int getFuelTime() {
        return 0;
    }
}
