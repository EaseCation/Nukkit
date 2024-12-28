package cn.nukkit.block.state.enumeration;

public enum PrismarineBlockTypeState {
    DEFAULT("default"),
    DARK("dark"),
    BRICKS("bricks"),
    ;

    private final String name;

    PrismarineBlockTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
