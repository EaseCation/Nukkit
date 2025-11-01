package cn.nukkit.block.state.enumeration;

public enum MinecraftCornerState {
    NONE("none"),
    INNER_LEFT("inner_left"),
    INNER_RIGHT("inner_right"),
    OUTER_LEFT("outer_left"),
    OUTER_RIGHT("outer_right"),
    ;

    private final String name;

    MinecraftCornerState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
