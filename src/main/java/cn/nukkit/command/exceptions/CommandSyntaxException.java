package cn.nukkit.command.exceptions;

public class CommandSyntaxException extends Exception {
    CommandSyntaxException() {
    }

    CommandSyntaxException(String message) {
        super(message);
    }
}
