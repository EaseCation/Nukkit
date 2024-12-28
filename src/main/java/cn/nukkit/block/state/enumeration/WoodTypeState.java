package cn.nukkit.block.state.enumeration;

public enum WoodTypeState {
    OAK("oak"),
    SPRUCE("spruce"),
    BIRCH("birch"),
    JUNGLE("jungle"),
    ACACIA("acacia"),
    DARK_OAK("dark_oak"),
    ;

    private final String name;

    WoodTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
