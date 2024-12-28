package cn.nukkit.block.state.enumeration;

public enum StoneSlabType4State {
    MOSSY_STONE_BRICK("mossy_stone_brick"),
    SMOOTH_QUARTZ("smooth_quartz"),
    STONE("stone"),
    CUT_SANDSTONE("cut_sandstone"),
    CUT_RED_SANDSTONE("cut_red_sandstone"),
    ;

    private final String name;

    StoneSlabType4State(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
