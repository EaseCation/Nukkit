package cn.nukkit.utils;

import java.io.IOException;

public class DataDepthException extends IOException {
    public DataDepthException() {
        super();
    }

    public DataDepthException(String message) {
        super(message);
    }

    public DataDepthException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataDepthException(Throwable cause) {
        super(cause);
    }
}
