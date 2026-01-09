package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralFanHorn extends BlockCoralFan {
    BlockCoralFanHorn() {

    }

    @Override
    public int getId() {
        return HORN_CORAL_FAN;
    }

    @Override
    public String getName() {
        return "Horn Coral Fan";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    protected int getWallBlockId(boolean dead) {
        return dead ? DEAD_HORN_CORAL_WALL_FAN : HORN_CORAL_WALL_FAN;
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_HORN_CORAL_FAN;
    }
}
