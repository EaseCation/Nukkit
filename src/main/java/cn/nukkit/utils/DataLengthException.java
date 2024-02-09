package cn.nukkit.utils;

import java.io.IOException;

public class DataLengthException extends IOException {
    public DataLengthException() {
        super();
    }

    public DataLengthException(String message) {
        super(message);
    }

    public DataLengthException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataLengthException(Throwable cause) {
        super(cause);
    }
}
