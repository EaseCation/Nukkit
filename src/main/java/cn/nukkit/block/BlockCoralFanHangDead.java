package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public abstract class BlockCoralFanHangDead extends BlockCoralFanHang {

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }

    @Override
    public boolean isDeadCoral() {
        return true;
    }

    @Override
    protected int getDeadBlockId() {
        return getId();
    }
}
