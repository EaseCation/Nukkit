package cn.nukkit.block;

public class BlockCoralFanDeadFire extends BlockCoralFanDead {
    BlockCoralFanDeadFire() {

    }

    @Override
    public int getId() {
        return DEAD_FIRE_CORAL_FAN;
    }

    @Override
    public String getName() {
        return "Dead Fire Coral Fan";
    }

    @Override
    protected int getWallBlockId(boolean dead) {
        return DEAD_FIRE_CORAL_WALL_FAN;
    }
}
