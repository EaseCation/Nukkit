package cn.nukkit.block.state.enumeration;

public enum StoneBrickTypeState {
    DEFAULT("default"),
    MOSSY("mossy"),
    CRACKED("cracked"),
    CHISELED("chiseled"),
    SMOOTH("smooth"),
    ;

    private final String name;

    StoneBrickTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
