package cn.nukkit.block.state.enumeration;

public enum LeverDirectionState {
    DOWN_EAST_WEST("down_east_west"),
    EAST("east"),
    WEST("west"),
    SOUTH("south"),
    NORTH("north"),
    UP_NORTH_SOUTH("up_north_south"),
    UP_EAST_WEST("up_east_west"),
    DOWN_NORTH_SOUTH("down_north_south"),
    ;

    private final String name;

    LeverDirectionState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
