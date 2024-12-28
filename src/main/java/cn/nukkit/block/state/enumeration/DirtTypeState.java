package cn.nukkit.block.state.enumeration;

public enum DirtTypeState {
    NORMAL("normal"),
    COARSE("coarse"),
    ;

    private final String name;

    DirtTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
