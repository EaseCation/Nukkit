package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockHangingSignWarped extends BlockHangingSign {
    public BlockHangingSignWarped() {
        this(0);
    }

    public BlockHangingSignWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_HANGING_SIGN;
    }

    @Override
    public String getName() {
        return "Warped Hanging Sign";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    public int getFuelTime() {
        return 0;
    }
}
