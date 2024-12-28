package cn.nukkit.block.state.enumeration;

public enum StoneSlabTypeState {
    SMOOTH_STONE("smooth_stone"),
    SANDSTONE("sandstone"),
    WOOD("wood"),
    COBBLESTONE("cobblestone"),
    BRICK("brick"),
    STONE_BRICK("stone_brick"),
    QUARTZ("quartz"),
    NETHER_BRICK("nether_brick"),
    ;

    private final String name;

    StoneSlabTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
