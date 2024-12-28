package cn.nukkit.block.state.enumeration;

public enum ColorState {
    WHITE("white"),
    ORANGE("orange"),
    MAGENTA("magenta"),
    LIGHT_BLUE("light_blue"),
    YELLOW("yellow"),
    LIME("lime"),
    PINK("pink"),
    GRAY("gray"),
    SILVER("silver"),
    CYAN("cyan"),
    PURPLE("purple"),
    BLUE("blue"),
    BROWN("brown"),
    GREEN("green"),
    RED("red"),
    BLACK("black"),
    ;

    private final String name;

    ColorState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
