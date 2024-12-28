package cn.nukkit.block.state.enumeration;

public enum MinecraftFacingDirectionState {
    DOWN("down"),
    UP("up"),
    NORTH("north"),
    SOUTH("south"),
    WEST("west"),
    EAST("east"),
    ;

    private final String name;

    MinecraftFacingDirectionState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
