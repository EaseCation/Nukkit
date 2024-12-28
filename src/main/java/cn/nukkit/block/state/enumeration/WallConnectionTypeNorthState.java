package cn.nukkit.block.state.enumeration;

public enum WallConnectionTypeNorthState {
    NONE("none"),
    SHORT("short"),
    TALL("tall"),
    ;

    private final String name;

    WallConnectionTypeNorthState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
