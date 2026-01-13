package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralFanFire extends BlockCoralFan {
    BlockCoralFanFire() {

    }

    @Override
    public int getId() {
        return FIRE_CORAL_FAN;
    }

    @Override
    public String getName() {
        return "Fire Coral Fan";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    protected int getWallBlockId(boolean dead) {
        return dead ? DEAD_FIRE_CORAL_WALL_FAN : FIRE_CORAL_WALL_FAN;
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_FIRE_CORAL_FAN;
    }

    @Override
    public String getDescriptionId() {
        return "tile.coral_fan.red_fan.name";
    }
}
