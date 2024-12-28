package cn.nukkit.block.state.enumeration;

public enum WallBlockTypeState {
    COBBLESTONE("cobblestone"),
    MOSSY_COBBLESTONE("mossy_cobblestone"),
    GRANITE("granite"),
    DIORITE("diorite"),
    ANDESITE("andesite"),
    SANDSTONE("sandstone"),
    BRICK("brick"),
    STONE_BRICK("stone_brick"),
    MOSSY_STONE_BRICK("mossy_stone_brick"),
    NETHER_BRICK("nether_brick"),
    END_BRICK("end_brick"),
    PRISMARINE("prismarine"),
    RED_SANDSTONE("red_sandstone"),
    RED_NETHER_BRICK("red_nether_brick"),
    ;

    private final String name;

    WallBlockTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
