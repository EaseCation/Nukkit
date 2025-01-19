package cn.nukkit.block.state.enumeration;

public enum CreakingHeartState {
    UPROOTED("uprooted"),
    DORMANT("dormant"),
    AWAKE("awake"),
    ;

    private final String name;

    CreakingHeartState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
