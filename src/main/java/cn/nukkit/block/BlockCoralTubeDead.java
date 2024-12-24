package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralTubeDead extends BlockCoral {
    public BlockCoralTubeDead() {
    }

    @Override
    public int getId() {
        return DEAD_TUBE_CORAL;
    }

    @Override
    public String getName() {
        return "Dead Tube Coral";
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
