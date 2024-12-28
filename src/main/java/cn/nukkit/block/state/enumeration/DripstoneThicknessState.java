package cn.nukkit.block.state.enumeration;

public enum DripstoneThicknessState {
    TIP("tip"),
    FRUSTUM("frustum"),
    MIDDLE("middle"),
    BASE("base"),
    MERGE("merge"),
    ;

    private final String name;

    DripstoneThicknessState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
