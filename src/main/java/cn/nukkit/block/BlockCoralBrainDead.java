package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralBrainDead extends BlockCoral {
    public BlockCoralBrainDead() {
    }

    @Override
    public int getId() {
        return DEAD_BRAIN_CORAL;
    }

    @Override
    public String getName() {
        return "Dead Brain Coral";
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
