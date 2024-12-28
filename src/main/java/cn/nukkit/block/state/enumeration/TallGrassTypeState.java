package cn.nukkit.block.state.enumeration;

public enum TallGrassTypeState {
    DEFAULT("default"),
    TALL("tall"),
    FERN("fern"),
    SNOW("snow"),
    ;

    private final String name;

    TallGrassTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
