package cn.nukkit.block.state.enumeration;

public enum SeaGrassTypeState {
    DEFAULT("default"),
    DOUBLE_TOP("double_top"),
    DOUBLE_BOT("double_bot"),
    ;

    private final String name;

    SeaGrassTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
