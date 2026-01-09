package cn.nukkit.block;

public class BlockCoralFanDeadHorn extends BlockCoralFanDead {
    BlockCoralFanDeadHorn() {

    }

    @Override
    public int getId() {
        return DEAD_HORN_CORAL_FAN;
    }

    @Override
    public String getName() {
        return "Dead Horn Coral Fan";
    }

    @Override
    protected int getWallBlockId(boolean dead) {
        return DEAD_HORN_CORAL_WALL_FAN;
    }
}
