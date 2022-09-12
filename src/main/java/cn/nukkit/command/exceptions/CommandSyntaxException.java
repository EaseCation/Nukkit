package cn.nukkit.command.exceptions;

public class CommandSyntaxException extends Exception {
    CommandSyntaxException() {
        this(null);
    }

    CommandSyntaxException(String message) {
        super(message, null, false, false);
    }
}
