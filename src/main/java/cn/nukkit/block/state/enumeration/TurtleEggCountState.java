package cn.nukkit.block.state.enumeration;

public enum TurtleEggCountState {
    ONE_EGG("one_egg"),
    TWO_EGG("two_egg"),
    THREE_EGG("three_egg"),
    FOUR_EGG("four_egg"),
    ;

    private final String name;

    TurtleEggCountState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
