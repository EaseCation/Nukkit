package cn.nukkit.block.state.enumeration;

public enum CauldronLiquidState {
    WATER("water"),
    LAVA("lava"),
    POWDER_SNOW("powder_snow"),
    ;

    private final String name;

    CauldronLiquidState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
