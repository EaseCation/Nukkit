package cn.nukkit.block.state.enumeration;

public enum SaplingTypeState {
    OAK("oak"),
    SPRUCE("spruce"),
    BIRCH("birch"),
    JUNGLE("jungle"),
    ACACIA("acacia"),
    DARK_OAK("dark_oak"),
    ;

    private final String name;

    SaplingTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
