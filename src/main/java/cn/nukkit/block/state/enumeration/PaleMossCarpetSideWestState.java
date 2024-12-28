package cn.nukkit.block.state.enumeration;

public enum PaleMossCarpetSideWestState {
    NONE("none"),
    SHORT("short"),
    TALL("tall"),
    ;

    private final String name;

    PaleMossCarpetSideWestState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
