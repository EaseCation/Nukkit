package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralFire extends BlockCoral {
    public BlockCoralFire() {
    }

    @Override
    public int getId() {
        return FIRE_CORAL;
    }

    @Override
    public String getName() {
        return "Fire Coral";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_FIRE_CORAL;
    }
}
