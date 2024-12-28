package cn.nukkit.block.state.enumeration;

public enum StructureBlockTypeState {
    DATA("data"),
    SAVE("save"),
    LOAD("load"),
    CORNER("corner"),
    INVALID("invalid"),
    EXPORT("export"),
    ;

    private final String name;

    StructureBlockTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
