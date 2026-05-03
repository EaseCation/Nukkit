package cn.nukkit.block.state.enumeration;

public enum PotentSulfurState {
    DRY("dry"),
    WET("wet"),
    DORMANT("dormant"),
    ERUPTING("erupting"),
    ;

    private final String name;

    PotentSulfurState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
