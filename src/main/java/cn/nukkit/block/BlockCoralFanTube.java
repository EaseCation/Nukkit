package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralFanTube extends BlockCoralFan {
    BlockCoralFanTube() {

    }

    @Override
    public int getId() {
        return TUBE_CORAL_FAN;
    }

    @Override
    public String getName() {
        return "Tube Coral Fan";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLUE_BLOCK_COLOR;
    }

    @Override
    protected int getWallBlockId(boolean dead) {
        return dead ? DEAD_TUBE_CORAL_WALL_FAN : TUBE_CORAL_WALL_FAN;
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_TUBE_CORAL_FAN;
    }
}
