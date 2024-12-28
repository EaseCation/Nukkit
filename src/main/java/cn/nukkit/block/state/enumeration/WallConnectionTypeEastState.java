package cn.nukkit.block.state.enumeration;

public enum WallConnectionTypeEastState {
    NONE("none"),
    SHORT("short"),
    TALL("tall"),
    ;

    private final String name;

    WallConnectionTypeEastState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
