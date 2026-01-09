package cn.nukkit.block;

public class BlockCoralFanDeadTube extends BlockCoralFanDead {
    BlockCoralFanDeadTube() {

    }

    @Override
    public int getId() {
        return DEAD_TUBE_CORAL_FAN;
    }

    @Override
    public String getName() {
        return "Dead Tube Coral Fan";
    }

    @Override
    protected int getWallBlockId(boolean dead) {
        return DEAD_TUBE_CORAL_WALL_FAN;
    }
}
