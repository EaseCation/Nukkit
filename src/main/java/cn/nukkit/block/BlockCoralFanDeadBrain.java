package cn.nukkit.block;

public class BlockCoralFanDeadBrain extends BlockCoralFanDead {
    BlockCoralFanDeadBrain() {

    }

    @Override
    public int getId() {
        return DEAD_BRAIN_CORAL_FAN;
    }

    @Override
    public String getName() {
        return "Dead Brain Coral Fan";
    }

    @Override
    protected int getWallBlockId(boolean dead) {
        return DEAD_BRAIN_CORAL_WALL_FAN;
    }
}
