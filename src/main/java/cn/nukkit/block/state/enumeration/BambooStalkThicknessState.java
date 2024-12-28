package cn.nukkit.block.state.enumeration;

public enum BambooStalkThicknessState {
    THIN("thin"),
    THICK("thick"),
    ;

    private final String name;

    BambooStalkThicknessState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
