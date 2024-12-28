package cn.nukkit.block.state.enumeration;

public enum SpongeTypeState {
    DRY("dry"),
    WET("wet"),
    ;

    private final String name;

    SpongeTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
