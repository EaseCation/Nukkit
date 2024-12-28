package cn.nukkit.block.state.enumeration;

public enum VaultState {
    INACTIVE("inactive"),
    ACTIVE("active"),
    UNLOCKING("unlocking"),
    EJECTING("ejecting"),
    ;

    private final String name;

    VaultState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
