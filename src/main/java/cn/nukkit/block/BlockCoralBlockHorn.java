package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralBlockHorn extends BlockCoralBlock {
    BlockCoralBlockHorn() {

    }

    @Override
    public int getId() {
        return HORN_CORAL_BLOCK;
    }

    @Override
    public String getName() {
        return "Horn Coral Block";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_HORN_CORAL_BLOCK;
    }
}
