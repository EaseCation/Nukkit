package cn.nukkit.block.state.enumeration;

public enum DamageState {
    UNDAMAGED("undamaged"),
    SLIGHTLY_DAMAGED("slightly_damaged"),
    VERY_DAMAGED("very_damaged"),
    BROKEN("broken"),
    ;

    private final String name;

    DamageState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
