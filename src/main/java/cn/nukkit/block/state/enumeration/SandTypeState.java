package cn.nukkit.block.state.enumeration;

public enum SandTypeState {
    NORMAL("normal"),
    RED("red"),
    ;

    private final String name;

    SandTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
