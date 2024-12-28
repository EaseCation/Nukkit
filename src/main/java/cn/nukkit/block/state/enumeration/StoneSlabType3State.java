package cn.nukkit.block.state.enumeration;

public enum StoneSlabType3State {
    END_STONE_BRICK("end_stone_brick"),
    SMOOTH_RED_SANDSTONE("smooth_red_sandstone"),
    POLISHED_ANDESITE("polished_andesite"),
    ANDESITE("andesite"),
    DIORITE("diorite"),
    POLISHED_DIORITE("polished_diorite"),
    GRANITE("granite"),
    POLISHED_GRANITE("polished_granite"),
    ;

    private final String name;

    StoneSlabType3State(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
