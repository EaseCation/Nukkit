package cn.nukkit.level.format.leveldb.ffm;

import java.lang.foreign.MemorySegment;

import static cn.nukkit.level.format.leveldb.ffm.NativeBindings.*;
import static java.lang.foreign.MemorySegment.*;

/**
 * An Env is an interface used by the LevelDB implementation to access operating system functionality like the filesystem etc.
 * <p>
 * This class wraps the default environment provided by LevelDB.
 */
public final class Env implements AutoCloseable {

    private final MemorySegment handle;
    private boolean closed;

    /**
     * Creates a default environment for the current operating system.
     */
    public Env() {
        try {
            this.handle = (MemorySegment) leveldb_create_default_env.invokeExact();
        } catch (Throwable e) {
            throw new DBException("Failed to create default env", e);
        }
    }

    /**
     * Returns the path to a temporary directory for testing.
     * The returned path must be freed by the caller using {@link NativeBindings#leveldb_free}.
     *
     * @return the path to a test directory, or null if not available
     */
    public String getTestDirectory() {
        ensureOpen();
        try {
            MemorySegment result = (MemorySegment) leveldb_env_get_test_directory.invokeExact(handle);
            if (result.equals(NULL)) {
                return null;
            }
            String path = result.reinterpret(Long.MAX_VALUE).getString(0);
            leveldb_free.invokeExact(result);
            return path;
        } catch (Throwable e) {
            throw new DBException("Failed to get test directory", e);
        }
    }

    /**
     * Returns the native handle for use with options.
     */
    MemorySegment getHandle() {
        ensureOpen();
        return handle;
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("Env has been closed");
        }
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        try {
            leveldb_env_destroy.invokeExact(handle);
        } catch (Throwable e) {
            throw new DBException("Failed to destroy env", e);
        }
    }
}
