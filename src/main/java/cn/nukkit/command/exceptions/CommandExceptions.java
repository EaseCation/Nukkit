package cn.nukkit.command.exceptions;

public final class CommandExceptions {
    public static final CommandSyntaxException COMMAND_SYNTAX_EXCEPTION = new CommandSyntaxException();
    public static final SelectorSyntaxException SELECTOR_SYNTAX_EXCEPTION = new SelectorSyntaxException();

    public static final CommandSyntaxException END_OF_COMMAND = new CommandSyntaxException("End of Command");
    public static final CommandSyntaxException NUMBER_TOO_SMALL = new CommandSyntaxException("Number too small");
    public static final CommandSyntaxException NUMBER_TOO_BIG = new CommandSyntaxException("Number too big");
    public static final CommandSyntaxException NOT_FLOAT = new CommandSyntaxException("expected float");
    public static final CommandSyntaxException NOT_INT = new CommandSyntaxException("expected int");

    public static final SelectorSyntaxException NO_TARGET = new SelectorSyntaxException("No target");
    public static final SelectorSyntaxException TOO_MANY_TARGETS = new SelectorSyntaxException("Too many targets");

    //TODO: more stuff

    public static void init() {
    }

    private CommandExceptions() {
        throw new IllegalStateException();
    }
}
