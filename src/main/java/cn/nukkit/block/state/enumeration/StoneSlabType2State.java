package cn.nukkit.block.state.enumeration;

public enum StoneSlabType2State {
    RED_SANDSTONE("red_sandstone"),
    PURPUR("purpur"),
    PRISMARINE_ROUGH("prismarine_rough"),
    PRISMARINE_DARK("prismarine_dark"),
    PRISMARINE_BRICK("prismarine_brick"),
    MOSSY_COBBLESTONE("mossy_cobblestone"),
    SMOOTH_SANDSTONE("smooth_sandstone"),
    RED_NETHER_BRICK("red_nether_brick"),
    ;

    private final String name;

    StoneSlabType2State(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
