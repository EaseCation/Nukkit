package cn.nukkit.block.state.enumeration;

public enum ChiselTypeState {
    DEFAULT("default"),
    CHISELED("chiseled"),
    LINES("lines"),
    SMOOTH("smooth"),
    ;

    private final String name;

    ChiselTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
