package cn.nukkit.molang.compiler;

public class MolangCompileException extends RuntimeException {
    public MolangCompileException(String message) {
        super(message);
    }

    public MolangCompileException(String message, Throwable cause) {
        super(message, cause);
    }
}
