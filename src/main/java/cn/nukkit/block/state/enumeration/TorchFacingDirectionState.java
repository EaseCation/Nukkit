package cn.nukkit.block.state.enumeration;

public enum TorchFacingDirectionState {
    UNKNOWN("unknown"),
    WEST("west"),
    EAST("east"),
    NORTH("north"),
    SOUTH("south"),
    TOP("top"),
    ;

    private final String name;

    TorchFacingDirectionState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
