package cn.nukkit.block.state.enumeration;

public enum CrackedState {
    NO_CRACKS("no_cracks"),
    CRACKED("cracked"),
    MAX_CRACKED("max_cracked"),
    ;

    private final String name;

    CrackedState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
