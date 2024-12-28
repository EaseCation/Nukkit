package cn.nukkit.block.state.enumeration;

public enum BigDripleafTiltState {
    NONE("none"),
    UNSTABLE("unstable"),
    PARTIAL_TILT("partial_tilt"),
    FULL_TILT("full_tilt"),
    ;

    private final String name;

    BigDripleafTiltState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
