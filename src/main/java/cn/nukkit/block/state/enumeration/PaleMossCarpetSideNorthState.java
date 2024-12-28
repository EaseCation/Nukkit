package cn.nukkit.block.state.enumeration;

public enum PaleMossCarpetSideNorthState {
    NONE("none"),
    SHORT("short"),
    TALL("tall"),
    ;

    private final String name;

    PaleMossCarpetSideNorthState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
