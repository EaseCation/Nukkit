package cn.nukkit.level.format.leveldb.ffm;

import java.lang.foreign.MemorySegment;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A Snapshot is an immutable object and can therefore be safely accessed from multiple threads without any external synchronization.
 */
public final class Snapshot implements AutoCloseable {

    private final MemorySegment handle;
    private final DB db;
    private final ReentrantReadWriteLock lifecycleLock = new ReentrantReadWriteLock();
    private boolean released;

    Snapshot(MemorySegment handle, DB db) {
        this.handle = handle;
        this.db = db;
    }

    HandleLease acquireHandle() {
        Lock readLock = lifecycleLock.readLock();
        readLock.lock();
        if (released) {
            readLock.unlock();
            throw new IllegalStateException("Snapshot has been released");
        }
        return new HandleLease(handle, readLock);
    }

    /**
     * Releases this snapshot.
     * After calling this method, the snapshot should not be used.
     */
    public void release() {
        close();
    }

    /**
     * Releases this snapshot. Repeated calls are safe.
     */
    @Override
    public void close() {
        if (lifecycleLock.getReadHoldCount() != 0) {
            throw new IllegalStateException("Cannot close snapshot while this thread holds a read session");
        }
        Lock writeLock = lifecycleLock.writeLock();
        writeLock.lock();
        try {
            if (released) {
                return;
            }
            db.releaseSnapshot(handle);
            released = true;
        } finally {
            writeLock.unlock();
        }
    }

    static final class HandleLease implements AutoCloseable {

        private final MemorySegment handle;
        private Lock lock;

        private HandleLease(MemorySegment handle, Lock lock) {
            this.handle = handle;
            this.lock = lock;
        }

        MemorySegment handle() {
            return handle;
        }

        @Override
        public void close() {
            if (lock != null) {
                lock.unlock();
                lock = null;
            }
        }
    }
}
