package cn.nukkit.block.state.enumeration;

public enum WallConnectionTypeWestState {
    NONE("none"),
    SHORT("short"),
    TALL("tall"),
    ;

    private final String name;

    WallConnectionTypeWestState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
