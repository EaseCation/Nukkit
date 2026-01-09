package cn.nukkit.block;

public class BlockCoralFanDeadBubble extends BlockCoralFanDead {
    BlockCoralFanDeadBubble() {

    }

    @Override
    public int getId() {
        return DEAD_BUBBLE_CORAL_FAN;
    }

    @Override
    public String getName() {
        return "Dead Bubble Coral Fan";
    }

    @Override
    protected int getWallBlockId(boolean dead) {
        return DEAD_BUBBLE_CORAL_WALL_FAN;
    }
}
