package cn.nukkit.block.state.enumeration;

public enum NewLogTypeState {
    ACACIA("acacia"),
    DARK_OAK("dark_oak"),
    ;

    private final String name;

    NewLogTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
