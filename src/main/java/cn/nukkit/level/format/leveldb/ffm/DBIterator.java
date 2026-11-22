package cn.nukkit.level.format.leveldb.ffm;

import io.netty.buffer.ByteBuf;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import static cn.nukkit.level.format.leveldb.ffm.NativeBindings.*;
import static cn.nukkit.level.format.leveldb.ffm.PlatformUtil.*;
import static java.lang.foreign.Arena.*;
import static java.lang.foreign.MemorySegment.*;
import static java.lang.foreign.ValueLayout.*;
import static java.util.Objects.*;

/**
 * An iterator over the contents of a LevelDB database.
 * <p>
 * Multiple threads can invoke const methods on an Iterator without external synchronization,
 * but if any of the threads may call a non-const method,
 * all threads accessing the same Iterator must use external synchronization.
 */
public final class DBIterator implements Iterator<Map.Entry<byte[], byte[]>>, AutoCloseable {

    private final Arena arena;
    private final MemorySegment handle;
    private final MemorySegment keyLengthPtr;
    private final MemorySegment valueLengthPtr;
    private final MemorySegment errorPtr;
    private final NativeBuffer seekBuffer = new NativeBuffer();
    private final Object keyAccessLock = new Object();
    private final Object valueAccessLock = new Object();
    private final Object errorAccessLock = new Object();
    private boolean validityKnown;
    private boolean valid;
    private boolean closed;

    DBIterator(MemorySegment handle) {
        this(handle, false);
    }

    DBIterator(MemorySegment handle, boolean confined) {
        Arena newArena = null;
        try {
            newArena = confined ? ofConfined() : ofShared();
            this.keyLengthPtr = newArena.allocate(JAVA_LONG);
            this.valueLengthPtr = newArena.allocate(JAVA_LONG);
            this.errorPtr = newArena.allocate(ADDRESS);
            this.arena = newArena;
            this.handle = handle;
        } catch (Throwable e) {
            if (newArena != null) {
                try {
                    newArena.close();
                } catch (Throwable cleanupFailure) {
                    e.addSuppressed(cleanupFailure);
                }
            }
            try {
                leveldb_iter_destroy.invokeExact(handle);
            } catch (Throwable cleanupFailure) {
                e.addSuppressed(cleanupFailure);
            }
            throw new DBException("Failed to initialize iterator", e);
        }
    }

    /**
     * Returns true if the iterator is positioned at a valid entry.
     */
    public synchronized boolean isValid() {
        ensureOpen();
        if (validityKnown) {
            return valid;
        }
        try {
            valid = ((byte) leveldb_iter_valid.invokeExact(handle)) != 0;
            validityKnown = true;
            return valid;
        } catch (Throwable e) {
            throw new DBException("Failed to check iterator validity", e);
        }
    }

    /**
     * Position at the first key in the source.
     */
    public void seekToFirst() {
        ensureOpen();
        invalidateValidity();
        try {
            leveldb_iter_seek_to_first.invokeExact(handle);
        } catch (Throwable e) {
            throw new DBException("Failed to seek to first", e);
        }
    }

    /**
     * Position at the last key in the source.
     */
    public void seekToLast() {
        ensureOpen();
        invalidateValidity();
        try {
            leveldb_iter_seek_to_last.invokeExact(handle);
        } catch (Throwable e) {
            throw new DBException("Failed to seek to last", e);
        }
    }

    /**
     * Position at the first key in the source that is at or past target.
     */
    public void seek(byte[] target) {
        ensureOpen();
        invalidateValidity();
        try {
            MemorySegment targetSegment = seekBuffer.copyFrom(target, arena);
            invokeSeek(targetSegment, target.length);
        } catch (Throwable e) {
            throw new DBException("Failed to seek", e);
        }
    }

    /**
     * Positions at the first key at or past a target in native memory without copying it.
     */
    public void seek(MemorySegment target) {
        ensureOpen();
        invalidateValidity();
        MemorySegment targetSegment = requireNative(target, "target");
        try {
            invokeSeek(targetSegment, targetSegment.byteSize());
        } catch (Throwable e) {
            throw new DBException("Failed to seek", e);
        }
    }

    /**
     * Positions at the first key at or past the remaining region of an NIO byte buffer.
     * The position is not modified. A direct buffer is passed without copying.
     */
    public void seek(ByteBuffer target) {
        ensureOpen();
        invalidateValidity();
        try {
            MemorySegment targetSegment = fromByteBuffer(target, seekBuffer, arena);
            invokeSeek(targetSegment, targetSegment.byteSize());
        } catch (Throwable e) {
            throw new DBException("Failed to seek", e);
        }
    }

    /**
     * Positions at the first key at or past the readable region of a Netty buffer.
     * The reader index is not modified. A contiguous direct buffer is passed without copying.
     */
    public void seek(ByteBuf target) {
        ensureOpen();
        invalidateValidity();
        try {
            MemorySegment targetSegment = fromByteBuf(target, seekBuffer, arena);
            invokeSeek(targetSegment, targetSegment.byteSize());
        } catch (Throwable e) {
            throw new DBException("Failed to seek", e);
        }
    }

    private void invokeSeek(MemorySegment target, long targetLength) throws Throwable {
        leveldb_iter_seek.invokeExact(handle, target, targetLength);
    }

    /**
     * Moves to the next entry in the source.
     */
    public void moveNext() {
        ensureOpen();
        invalidateValidity();
        try {
            leveldb_iter_next.invokeExact(handle);
        } catch (Throwable e) {
            throw new DBException("Failed to move to next", e);
        }
    }

    /**
     * Moves to the previous entry in the source.
     */
    public void movePrev() {
        ensureOpen();
        invalidateValidity();
        try {
            leveldb_iter_prev.invokeExact(handle);
        } catch (Throwable e) {
            throw new DBException("Failed to move to previous", e);
        }
    }

    /**
     * Returns the key for the current entry.
     * REQUIRES: isValid()
     */
    public byte[] key() {
        ensureOpen();
        try {
            synchronized (keyAccessLock) {
                MemorySegment keyPtr = (MemorySegment) leveldb_iter_key.invokeExact(handle, keyLengthPtr);

                long length = keyLengthPtr.get(JAVA_LONG, 0);
                return keyPtr.reinterpret(length).toArray(JAVA_BYTE);
            }
        } catch (Throwable e) {
            throw new DBException("Failed to get key", e);
        }
    }

    /**
     * Returns the value for the current entry.
     * REQUIRES: isValid()
     */
    public byte[] value() {
        ensureOpen();
        try {
            synchronized (valueAccessLock) {
                MemorySegment valuePtr = (MemorySegment) leveldb_iter_value.invokeExact(handle, valueLengthPtr);

                long length = valueLengthPtr.get(JAVA_LONG, 0);
                return valuePtr.reinterpret(length).toArray(JAVA_BYTE);
            }
        } catch (Throwable e) {
            throw new DBException("Failed to get value", e);
        }
    }

    /**
     * 借用当前位置的只读键；移动游标前必须消费完，关闭游标后视图失效。
     */
    public MemorySegment keySegment() {
        ensureOpen();
        if (!isValid()) {
            checkError();
            throw new NoSuchElementException();
        }
        try {
            synchronized (keyAccessLock) {
                MemorySegment pointer = (MemorySegment) leveldb_iter_key.invokeExact(handle, keyLengthPtr);
                return pointer.reinterpret(keyLengthPtr.get(JAVA_LONG, 0), arena, null).asReadOnly();
            }
        } catch (RuntimeException | Error failure) {
            throw failure;
        } catch (Throwable failure) {
            throw new DBException("Failed to borrow iterator key", failure);
        }
    }

    /**
     * 借用当前位置的只读值，不分配和复制 value；下一次 seek 或移动前必须完成解码。
     */
    public MemorySegment valueSegment() {
        ensureOpen();
        if (!isValid()) {
            checkError();
            throw new NoSuchElementException();
        }
        try {
            synchronized (valueAccessLock) {
                MemorySegment pointer = (MemorySegment) leveldb_iter_value.invokeExact(handle, valueLengthPtr);
                return pointer.reinterpret(valueLengthPtr.get(JAVA_LONG, 0), arena, null).asReadOnly();
            }
        } catch (RuntimeException | Error failure) {
            throw failure;
        } catch (Throwable failure) {
            throw new DBException("Failed to borrow iterator value", failure);
        }
    }

    /**
     * Visits all entries from the current position without copying keys or values.
     * Each non-owning read-only segment is valid only during its callback and must not be retained.
     */
    public void forEachRemaining(SegmentEntryConsumer consumer) {
        ensureOpen();
        requireNonNull(consumer, "consumer");

        while (isValid()) {
            try {
                synchronized (keyAccessLock) {
                    synchronized (valueAccessLock) {
                        MemorySegment keyPtr = (MemorySegment) leveldb_iter_key.invokeExact(handle, keyLengthPtr);
                        MemorySegment valuePtr = (MemorySegment) leveldb_iter_value.invokeExact(handle, valueLengthPtr);
                        long keyLength = keyLengthPtr.get(JAVA_LONG, 0);
                        long valueLength = valueLengthPtr.get(JAVA_LONG, 0);

                        MemorySegment key = nonOwningReadOnlySegment(keyPtr, keyLength);
                        MemorySegment value = nonOwningReadOnlySegment(valuePtr, valueLength);
                        consumer.accept(key, value);
                    }
                }
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable e) {
                throw new DBException("Failed to access iterator entry", e);
            }
            moveNext();
        }
        checkError();
    }

    private static MemorySegment nonOwningReadOnlySegment(MemorySegment pointer, long length) {
        return pointer.reinterpret(length).asReadOnly();
    }

    /**
     * Checks for any errors. Throws LevelDBException if an error has occurred.
     */
    public void checkError() {
        ensureOpen();
        try {
            synchronized (errorAccessLock) {
                errorPtr.set(ADDRESS, 0, NULL);
                leveldb_iter_get_error.invokeExact(handle, errorPtr);

                MemorySegment error = errorPtr.get(ADDRESS, 0);
                if (!error.equals(NULL)) {
                    String errorMsg = error.reinterpret(Long.MAX_VALUE).getString(0);
                    leveldb_free.invokeExact(error);
                    errorPtr.set(ADDRESS, 0, NULL);
                    throw new DBException(errorMsg);
                }
            }
        } catch (DBException e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to check for errors", e);
        }
    }

    @Override
    public boolean hasNext() {
        return isValid();
    }

    @Override
    public Map.Entry<byte[], byte[]> next() {
        if (!isValid()) {
            throw new NoSuchElementException();
        }
        byte[] key = key();
        byte[] value = value();
        moveNext();
        return Map.entry(key, value);
    }

    private void ensureOpen() {
        ensureThread();
        if (closed) {
            throw new IllegalStateException("Iterator has been closed");
        }
    }

    private void ensureThread() {
        if (!keyLengthPtr.isAccessibleBy(Thread.currentThread())) {
            throw new WrongThreadException("Iterator belongs to another thread");
        }
    }

    private synchronized void invalidateValidity() {
        validityKnown = false;
    }

    @Override
    public synchronized void close() {
        ensureThread();
        if (closed) {
            return;
        }
        // 先使所有借用视图失效，再销毁其原生所有者。
        arena.close();
        closed = true;
        invalidateValidity();
        try {
            leveldb_iter_destroy.invokeExact(handle);
        } catch (Throwable e) {
            throw new DBException("Failed to destroy iterator", e);
        }
    }

    /**
     * Consumes non-owning read-only native segments for an iterator entry.
     */
    @FunctionalInterface
    public interface SegmentEntryConsumer {
        void accept(MemorySegment key, MemorySegment value);
    }
}
