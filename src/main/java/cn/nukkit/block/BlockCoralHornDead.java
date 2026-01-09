package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralHornDead extends BlockCoral {
    BlockCoralHornDead() {

    }

    @Override
    public int getId() {
        return DEAD_HORN_CORAL;
    }

    @Override
    public String getName() {
        return "Dead Horn Coral";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }

    @Override
    public boolean isDeadCoral() {
        return true;
    }
}
