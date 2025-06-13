package cn.nukkit.block.state.enumeration;

import cn.nukkit.Player;
import cn.nukkit.math.BlockFace;

public enum MinecraftCardinalDirectionState implements BlockFaceState {
    SOUTH("south", BlockFace.SOUTH),
    WEST("west", BlockFace.WEST),
    NORTH("north", BlockFace.NORTH),
    EAST("east", BlockFace.EAST),
    ;

    private static final MinecraftCardinalDirectionState[] VALUES = values();

    private final String name;
    private final BlockFace facing;

    MinecraftCardinalDirectionState(String name, BlockFace facing) {
        this.name = name;
        this.facing = facing;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public BlockFace getBlockFace() {
        return facing;
    }

    public static MinecraftCardinalDirectionState from(BlockFace facing) {
        if (facing.isVertical()) {
            return SOUTH;
        }
        return from(facing.getHorizontalIndex());
    }

    public static MinecraftCardinalDirectionState from(int value) {
        return VALUES[value];
    }

    public static MinecraftCardinalDirectionState getPlacementDirection(Player player) {
        return from(player.getHorizontalFacing().getHorizontalIndex());
    }
}
