package cn.nukkit.block.state.enumeration;

import cn.nukkit.math.Orientation;

public enum OrientationState {
    DOWN_EAST("down_east", Orientation.DOWN_EAST),
    DOWN_NORTH("down_north", Orientation.DOWN_NORTH),
    DOWN_SOUTH("down_south", Orientation.DOWN_SOUTH),
    DOWN_WEST("down_west", Orientation.DOWN_WEST),
    UP_EAST("up_east", Orientation.UP_EAST),
    UP_NORTH("up_north", Orientation.UP_NORTH),
    UP_SOUTH("up_south", Orientation.UP_SOUTH),
    UP_WEST("up_west", Orientation.UP_WEST),
    WEST_UP("west_up", Orientation.WEST_UP),
    EAST_UP("east_up", Orientation.EAST_UP),
    NORTH_UP("north_up", Orientation.NORTH_UP),
    SOUTH_UP("south_up", Orientation.SOUTH_UP),
    ;

    private static final OrientationState[] VALUES = values();

    private final String name;
    private final Orientation orientation;

    OrientationState(String name, Orientation orientation) {
        this.name = name;
        this.orientation = orientation;
    }

    @Override
    public String toString() {
        return name;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public static OrientationState from(Orientation orientation) {
        return from(orientation.ordinal());
    }

    public static OrientationState from(int value) {
        return VALUES[value];
    }
}
