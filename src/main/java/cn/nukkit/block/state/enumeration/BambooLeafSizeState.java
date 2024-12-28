package cn.nukkit.block.state.enumeration;

public enum BambooLeafSizeState {
    NO_LEAVES("no_leaves"),
    SMALL_LEAVES("small_leaves"),
    LARGE_LEAVES("large_leaves"),
    ;

    private final String name;

    BambooLeafSizeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
