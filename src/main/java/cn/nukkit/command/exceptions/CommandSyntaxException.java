package cn.nukkit.command.exceptions;

public class CommandSyntaxException extends RuntimeException {
    CommandSyntaxException() {
        this(null);
    }

    CommandSyntaxException(String message) {
        super(message, null, false, false);
    }
}
