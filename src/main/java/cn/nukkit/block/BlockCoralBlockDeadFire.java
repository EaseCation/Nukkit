package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralBlockDeadFire extends BlockCoralBlockDead {
    BlockCoralBlockDeadFire() {

    }

    @Override
    public int getId() {
        return DEAD_FIRE_CORAL_BLOCK;
    }

    @Override
    public String getName() {
        return "Dead Fire Coral Block";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }
}
