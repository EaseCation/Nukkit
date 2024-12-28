package cn.nukkit.block.state.enumeration;

public enum MinecraftVerticalHalfState {
    BOTTOM("bottom"),
    TOP("top"),
    ;

    private final String name;

    MinecraftVerticalHalfState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
