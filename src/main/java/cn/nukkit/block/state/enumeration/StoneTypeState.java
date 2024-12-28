package cn.nukkit.block.state.enumeration;

public enum StoneTypeState {
    STONE("stone"),
    GRANITE("granite"),
    GRANITE_SMOOTH("granite_smooth"),
    DIORITE("diorite"),
    DIORITE_SMOOTH("diorite_smooth"),
    ANDESITE("andesite"),
    ANDESITE_SMOOTH("andesite_smooth"),
    ;

    private final String name;

    StoneTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
