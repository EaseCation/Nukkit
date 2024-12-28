package cn.nukkit.block.state.enumeration;

public enum OldLogTypeState {
    OAK("oak"),
    SPRUCE("spruce"),
    BIRCH("birch"),
    JUNGLE("jungle"),
    ;

    private final String name;

    OldLogTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
