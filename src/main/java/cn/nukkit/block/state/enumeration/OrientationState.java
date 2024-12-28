package cn.nukkit.block.state.enumeration;

public enum OrientationState {
    DOWN_EAST("down_east"),
    DOWN_NORTH("down_north"),
    DOWN_SOUTH("down_south"),
    DOWN_WEST("down_west"),
    UP_EAST("up_east"),
    UP_NORTH("up_north"),
    UP_SOUTH("up_south"),
    UP_WEST("up_west"),
    WEST_UP("west_up"),
    EAST_UP("east_up"),
    NORTH_UP("north_up"),
    SOUTH_UP("south_up"),
    ;

    private final String name;

    OrientationState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
