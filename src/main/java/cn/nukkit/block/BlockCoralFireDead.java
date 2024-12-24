package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralFireDead extends BlockCoral {
    public BlockCoralFireDead() {
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
    protected boolean isDead() {
        return true;
    }
}
