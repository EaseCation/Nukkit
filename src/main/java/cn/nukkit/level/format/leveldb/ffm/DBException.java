package cn.nukkit.level.format.leveldb.ffm;

/**
 * Exception thrown when a LevelDB operation fails.
 */
public class DBException extends RuntimeException {

    public DBException(String message) {
        super(message);
    }

    public DBException(String message, Throwable cause) {
        super(message, cause);
    }
}
