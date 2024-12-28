package cn.nukkit.block.state.enumeration;

public enum PaleMossCarpetSideSouthState {
    NONE("none"),
    SHORT("short"),
    TALL("tall"),
    ;

    private final String name;

    PaleMossCarpetSideSouthState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
