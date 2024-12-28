package cn.nukkit.block.state.enumeration;

public enum WallConnectionTypeSouthState {
    NONE("none"),
    SHORT("short"),
    TALL("tall"),
    ;

    private final String name;

    WallConnectionTypeSouthState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
