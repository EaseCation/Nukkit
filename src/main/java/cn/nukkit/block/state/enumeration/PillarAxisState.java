package cn.nukkit.block.state.enumeration;

public enum PillarAxisState {
    Y("y"),
    X("x"),
    Z("z"),
    ;

    private final String name;

    PillarAxisState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
