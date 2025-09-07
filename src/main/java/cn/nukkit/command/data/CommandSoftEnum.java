package cn.nukkit.command.data;

public final class CommandSoftEnum {
    public static final CommandEnum PLAYER = new CommandEnum("Player", true);
    public static final CommandEnum WORLD = new CommandEnum("World", true);

    private CommandSoftEnum() {
    }
}
