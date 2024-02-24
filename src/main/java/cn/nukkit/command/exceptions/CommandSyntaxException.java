package cn.nukkit.command.exceptions;

public class CommandSyntaxException extends RuntimeException {
    CommandSyntaxException() {
        this(null);
    }

    CommandSyntaxException(String message) {
        super(message, null, false, false);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public Throwable initCause(Throwable cause) {
        return this;
    }
}
