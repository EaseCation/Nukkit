package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralFanBrain extends BlockCoralFan {
    BlockCoralFanBrain() {

    }

    @Override
    public int getId() {
        return BRAIN_CORAL_FAN;
    }

    @Override
    public String getName() {
        return "Brain Coral Fan";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PINK_BLOCK_COLOR;
    }

    @Override
    protected int getWallBlockId(boolean dead) {
        return dead ? DEAD_BRAIN_CORAL_WALL_FAN : BRAIN_CORAL_WALL_FAN;
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_BRAIN_CORAL_FAN;
    }

    @Override
    public String getDescriptionId() {
        return "tile.coral_fan.pink_fan.name";
    }
}
