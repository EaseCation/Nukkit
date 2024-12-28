package cn.nukkit.block.state.enumeration;

public enum PortalAxisState {
    UNKNOWN("unknown"),
    X("x"),
    Z("z"),
    ;

    private final String name;

    PortalAxisState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
