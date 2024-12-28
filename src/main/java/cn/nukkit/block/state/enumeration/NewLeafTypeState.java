package cn.nukkit.block.state.enumeration;

public enum NewLeafTypeState {
    ACACIA("acacia"),
    DARK_OAK("dark_oak"),
    ;

    private final String name;

    NewLeafTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
