package cn.nukkit.level.format.leveldb.ffm;

import java.lang.foreign.MemorySegment;

import static cn.nukkit.level.format.leveldb.ffm.NativeBindings.*;

/**
 * Options that control write operations.
 */
public final class WriteOptions implements AutoCloseable {

    private final MemorySegment handle;
    private boolean closed;

    /**
     * Creates a new WriteOptions instance with default settings.
     */
    public WriteOptions() {
        try {
            this.handle = (MemorySegment) leveldb_writeoptions_create.invokeExact();
        } catch (Throwable e) {
            throw new DBException("Failed to create write options", e);
        }
    }

    /**
     * If true, the write will be flushed from the operating system buffer cache (by calling WritableFile::Sync())
     * before the write is considered complete.
     * If this flag is true, writes will be slower.
     * <p>
     * If this flag is false, and the machine crashes, some recent writes may be lost.
     * Note that if it is just the process that crashes (i.e., the machine does not reboot),
     * no writes will be lost even if sync==false.
     * <p>
     * Default: false
     */
    public WriteOptions setSync(boolean value) {
        ensureOpen();
        try {
            leveldb_writeoptions_set_sync.invokeExact(handle, (byte) (value ? 1 : 0));
        } catch (Throwable e) {
            throw new DBException("Failed to set sync", e);
        }
        return this;
    }

    MemorySegment getHandle() {
        ensureOpen();
        return handle;
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("WriteOptions has been closed");
        }
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        try {
            leveldb_writeoptions_destroy.invokeExact(handle);
        } catch (Throwable e) {
            throw new DBException("Failed to destroy write options", e);
        }
    }
}
