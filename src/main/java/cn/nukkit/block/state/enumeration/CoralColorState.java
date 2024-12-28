package cn.nukkit.block.state.enumeration;

public enum CoralColorState {
    BLUE("blue"),
    PINK("pink"),
    PURPLE("purple"),
    RED("red"),
    YELLOW("yellow"),
    ;

    private final String name;

    CoralColorState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
