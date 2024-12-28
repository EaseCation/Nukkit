package cn.nukkit.block.state.enumeration;

public enum StructureVoidTypeState {
    VOID("void"),
    AIR("air"),
    ;

    private final String name;

    StructureVoidTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
