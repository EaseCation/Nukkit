package cn.nukkit.network.protocol.types;

public enum CommandBlockMode {
    NORMAL,
    REPEATING,
    CHAIN,
    ;

    private static final CommandBlockMode[] VALUES = values();

    public static CommandBlockMode[] getValues() {
        return VALUES;
    }
}
