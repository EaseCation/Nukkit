package cn.nukkit.command.exceptions;

public final class CommandExceptions {
    public static final CommandSyntaxException COMMAND_SYNTAX_EXCEPTION = new CommandSyntaxException();
    public static final SelectorSyntaxException SELECTOR_SYNTAX_EXCEPTION = new SelectorSyntaxException();

    public static final CommandSyntaxException END_OF_COMMAND = new CommandSyntaxException("End of Command");
    //TODO: more stuff

    public static void init() {
    }

    private CommandExceptions() {
        throw new IllegalStateException();
    }
}
