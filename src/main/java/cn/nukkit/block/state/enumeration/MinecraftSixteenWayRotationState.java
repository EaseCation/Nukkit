package cn.nukkit.block.state.enumeration;

import cn.nukkit.math.BlockFace;

public enum MinecraftSixteenWayRotationState implements BlockFaceState {
    DOWN("down", BlockFace.DOWN),
    UP("up", BlockFace.UP),
    NORTH("north", BlockFace.NORTH),
    SOUTH("south", BlockFace.SOUTH),
    WEST("west", BlockFace.WEST),
    EAST("east", BlockFace.EAST),
    ;

    private static final MinecraftSixteenWayRotationState[] VALUES = values();

    private final String name;
    private final BlockFace facing;

    MinecraftSixteenWayRotationState(String name, BlockFace facing) {
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

    public static MinecraftSixteenWayRotationState from(BlockFace facing) {
        return from(facing.getIndex());
    }

    public static MinecraftSixteenWayRotationState from(int value) {
        return VALUES[value];
    }
}
