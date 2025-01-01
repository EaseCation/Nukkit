package cn.nukkit.block.state.enumeration;

public enum MinecraftBlockFaceState {
    DOWN("down"),
    UP("up"),
    NORTH("north"),
    SOUTH("south"),
    WEST("west"),
    EAST("east"),
    ;

    private static final MinecraftBlockFaceState[] VALUES = values();

    private final String name;

    MinecraftBlockFaceState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static MinecraftBlockFaceState from(int value) {
        return VALUES[value];
    }
}
