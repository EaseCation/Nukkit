package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralFireDead extends BlockCoral {
    BlockCoralFireDead() {

    }

    @Override
    public int getId() {
        return DEAD_FIRE_CORAL;
    }

    @Override
    public String getName() {
        return "Dead Fire Coral";
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
