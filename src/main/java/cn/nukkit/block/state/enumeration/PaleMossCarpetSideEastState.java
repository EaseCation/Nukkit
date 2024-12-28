package cn.nukkit.block.state.enumeration;

public enum PaleMossCarpetSideEastState {
    NONE("none"),
    SHORT("short"),
    TALL("tall"),
    ;

    private final String name;

    PaleMossCarpetSideEastState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
