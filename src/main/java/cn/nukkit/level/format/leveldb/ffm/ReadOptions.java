package cn.nukkit.level.format.leveldb.ffm;

import javax.annotation.Nullable;
import java.lang.foreign.MemorySegment;

import static cn.nukkit.level.format.leveldb.ffm.NativeBindings.*;
import static java.lang.foreign.MemorySegment.*;

/**
 * Options that control read operations.
 */
public final class ReadOptions implements AutoCloseable {

    private final MemorySegment handle;
    @Nullable
    private Snapshot snapshot;
    private boolean closed;

    /**
     * Creates a new ReadOptions instance with default settings.
     */
    public ReadOptions() {
        try {
            this.handle = (MemorySegment) leveldb_readoptions_create.invokeExact();
        } catch (Throwable e) {
            throw new DBException("Failed to create read options", e);
        }
    }

    /**
     * If true, all data read from underlying storage will be verified against corresponding checksums.
     * Default: false
     */
    public ReadOptions setVerifyChecksums(boolean value) {
        ensureOpen();
        try {
            leveldb_readoptions_set_verify_checksums.invokeExact(handle, (byte) (value ? 1 : 0));
        } catch (Throwable e) {
            throw new DBException("Failed to set verify_checksums", e);
        }
        return this;
    }

    /**
     * Should the data read for this iteration be cached in memory.
     * Default: true
     */
    public ReadOptions setFillCache(boolean value) {
        ensureOpen();
        try {
            leveldb_readoptions_set_fill_cache.invokeExact(handle, (byte) (value ? 1 : 0));
        } catch (Throwable e) {
            throw new DBException("Failed to set fill_cache", e);
        }
        return this;
    }

    /**
     * Sets the snapshot to read from.
     * If null, an implicit snapshot of the state at the beginning of this read operation will be used.
     */
    public ReadOptions setSnapshot(Snapshot snapshot) {
        ensureOpen();
        try (Snapshot.HandleLease snapshotLease = snapshot != null ? snapshot.acquireHandle() : null) {
            MemorySegment snapshotHandle = snapshotLease != null ? snapshotLease.handle() : NULL;
            leveldb_readoptions_set_snapshot.invokeExact(handle, snapshotHandle);
            this.snapshot = snapshot;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to set snapshot", e);
        }
        return this;
    }

    HandleLease acquireHandle() {
        ensureOpen();
        Snapshot.HandleLease snapshotLease = snapshot != null ? snapshot.acquireHandle() : null;
        return new HandleLease(handle, snapshotLease);
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("ReadOptions has been closed");
        }
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        try {
            leveldb_readoptions_destroy.invokeExact(handle);
        } catch (Throwable e) {
            throw new DBException("Failed to destroy read options", e);
        } finally {
            snapshot = null;
        }
    }

    static final class HandleLease implements AutoCloseable {

        private final MemorySegment handle;
        @Nullable
        private Snapshot.HandleLease snapshotLease;

        private HandleLease(MemorySegment handle, @Nullable Snapshot.HandleLease snapshotLease) {
            this.handle = handle;
            this.snapshotLease = snapshotLease;
        }

        MemorySegment handle() {
            return handle;
        }

        @Override
        public void close() {
            if (snapshotLease != null) {
                snapshotLease.close();
                snapshotLease = null;
            }
        }
    }
}
