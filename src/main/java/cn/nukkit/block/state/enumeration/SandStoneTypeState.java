package cn.nukkit.block.state.enumeration;

public enum SandStoneTypeState {
    DEFAULT("default"),
    HEIROGLYPHS("heiroglyphs"),
    CUT("cut"),
    SMOOTH("smooth"),
    ;

    private final String name;

    SandStoneTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
