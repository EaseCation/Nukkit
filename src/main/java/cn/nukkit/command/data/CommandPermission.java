package cn.nukkit.command.data;

public enum CommandPermission {
    ALL,
    GAME_DIRECTORS,
    ADMIN,
    HOST,
    OWNER,
    INTERNAL,
    ;

    private static final CommandPermission[] VALUES = values();

    public static CommandPermission[] getValues() {
        return VALUES;
    }
}
