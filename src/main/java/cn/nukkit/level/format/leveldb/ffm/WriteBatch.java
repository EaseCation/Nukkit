package cn.nukkit.level.format.leveldb.ffm;

import io.netty.buffer.ByteBuf;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.ByteBuffer;

import static cn.nukkit.level.format.leveldb.ffm.NativeBindings.*;
import static cn.nukkit.level.format.leveldb.ffm.PlatformUtil.*;
import static java.lang.foreign.Arena.*;
import static java.lang.foreign.FunctionDescriptor.*;
import static java.lang.foreign.MemorySegment.*;
import static java.lang.foreign.ValueLayout.*;
import static java.lang.invoke.MethodType.*;
import static java.util.Objects.*;

/**
 * WriteBatch holds a collection of updates to apply atomically to a DB.
 * <p>
 * Multiple threads can invoke const methods on a WriteBatch without external synchronization,
 * but if any of the threads may call a non-const method,
 * all threads accessing the same WriteBatch must use external synchronization.
 */
public final class WriteBatch implements AutoCloseable {

    private static final FunctionDescriptor PUT_CALLBACK_DESCRIPTOR = ofVoid(ADDRESS, ADDRESS, JAVA_LONG, ADDRESS, JAVA_LONG);
    private static final MethodHandle PUT_CALLBACK_HANDLE = findCallbackHandle("putCallback",
            methodType(void.class, PutHandler.class, MemorySegment.class, MemorySegment.class, long.class, MemorySegment.class, long.class));
    private static final MethodHandle SEGMENT_PUT_CALLBACK_HANDLE = findCallbackHandle("segmentPutCallback",
            methodType(void.class, SegmentPutHandler.class, MemorySegment.class, MemorySegment.class, long.class, MemorySegment.class, long.class));

    private static final FunctionDescriptor DELETE_CALLBACK_DESCRIPTOR = ofVoid(ADDRESS, ADDRESS, JAVA_LONG);
    private static final MethodHandle DELETE_CALLBACK_HANDLE = findCallbackHandle("deleteCallback",
            methodType(void.class, DeleteHandler.class, MemorySegment.class, MemorySegment.class, long.class));
    private static final MethodHandle SEGMENT_DELETE_CALLBACK_HANDLE = findCallbackHandle("segmentDeleteCallback",
            methodType(void.class, SegmentDeleteHandler.class, MemorySegment.class, MemorySegment.class, long.class));

    private final Arena arena;
    private final boolean ownsArena;
    private final MemorySegment arenaAccess;
    private final MemorySegment handle;
    private final NativeBuffer keyBuffer = new NativeBuffer();
    private final NativeBuffer valueBuffer = new NativeBuffer();
    private boolean closed;

    /**
     * Creates an empty WriteBatch.
     */
    public WriteBatch() {
        this(ofShared(), true);
    }

    /**
     * 借用调用方的临时内存；调用方应先关闭批次，再关闭 arena。
     * 批次操作遵守 arena 的线程限制，关闭批次不会关闭借用的 arena。
     */
    public WriteBatch(Arena arena) {
        this(arena, false);
    }

    private WriteBatch(Arena arena, boolean ownsArena) {
        this.arena = requireNonNull(arena, "arena");
        this.ownsArena = ownsArena;
        try {
            this.arenaAccess = ownsArena ? NULL : arena.allocate(0);
            this.handle = (MemorySegment) leveldb_writebatch_create.invokeExact();
        } catch (Throwable e) {
            if (ownsArena) {
                try {
                    arena.close();
                } catch (Throwable cleanupFailure) {
                    e.addSuppressed(cleanupFailure);
                }
            }
            if (e instanceof RuntimeException runtimeFailure) {
                throw runtimeFailure;
            }
            if (e instanceof Error error) {
                throw error;
            }
            throw new DBException("Failed to create write batch", e);
        }
    }

    /**
     * Clears all updates buffered in this batch.
     */
    public WriteBatch clear() {
        ensureOpen();
        try {
            leveldb_writebatch_clear.invokeExact(handle);
        } catch (Throwable e) {
            throw new DBException("Failed to clear write batch", e);
        }
        return this;
    }

    /**
     * Store the mapping "key->value" in the database.
     */
    public WriteBatch put(byte[] key, byte[] value) {
        ensureOpen();
        try {
            MemorySegment keySegment = keyBuffer.copyFrom(key, arena);
            MemorySegment valueSegment = valueBuffer.copyFrom(value, arena);
            invokePut(keySegment, key.length, valueSegment, value.length);
        } catch (Throwable e) {
            throw new DBException("Failed to put to write batch", e);
        }
        return this;
    }

    /**
     * 仅复制有效数据范围，避免序列化缓冲区先创建裁剪后的堆数组。
     */
    public WriteBatch put(byte[] key, byte[] value, int offset, int length) {
        ensureOpen();
        requireNonNull(key, "key");
        checkFromIndexSize(offset, length, value.length);
        try {
            MemorySegment keySegment = keyBuffer.copyFrom(key, arena);
            MemorySegment valueSegment = valueBuffer.copyFrom(value, offset, length, arena);
            invokePut(keySegment, key.length, valueSegment, length);
        } catch (Throwable failure) {
            throw new DBException("Failed to put value range to write batch", failure);
        }
        return this;
    }

    /**
     * 原生序列化结果直接交给批次，只为堆上的键复用临时缓冲区。
     */
    public WriteBatch put(byte[] key, MemorySegment value) {
        ensureOpen();
        MemorySegment nativeValue = requireNative(value, "value");
        try {
            MemorySegment nativeKey = keyBuffer.copyFrom(key, arena);
            invokePut(nativeKey, key.length, nativeValue, nativeValue.byteSize());
        } catch (Throwable failure) {
            throw new DBException("Failed to put native value to write batch", failure);
        }
        return this;
    }

    /**
     * Stores a mapping from native memory without copying it on the Java side.
     */
    public WriteBatch put(MemorySegment key, MemorySegment value) {
        ensureOpen();
        MemorySegment keySegment = requireNative(key, "key");
        MemorySegment valueSegment = requireNative(value, "value");
        try {
            invokePut(keySegment, keySegment.byteSize(), valueSegment, valueSegment.byteSize());
        } catch (Throwable e) {
            throw new DBException("Failed to put to write batch", e);
        }
        return this;
    }

    /**
     * Stores a mapping from the remaining regions of two NIO byte buffers.
     * Positions are not modified. Direct buffers are passed without copying.
     */
    public WriteBatch put(ByteBuffer key, ByteBuffer value) {
        ensureOpen();
        try {
            MemorySegment keySegment = fromByteBuffer(key, keyBuffer, arena);
            MemorySegment valueSegment = fromByteBuffer(value, valueBuffer, arena);
            invokePut(keySegment, keySegment.byteSize(), valueSegment, valueSegment.byteSize());
        } catch (Throwable e) {
            throw new DBException("Failed to put to write batch", e);
        }
        return this;
    }

    /**
     * Stores a mapping from the readable regions of two Netty buffers.
     * Reader indexes are not modified. Contiguous direct buffers are passed without copying.
     */
    public WriteBatch put(ByteBuf key, ByteBuf value) {
        ensureOpen();
        try {
            MemorySegment keySegment = fromByteBuf(key, keyBuffer, arena);
            MemorySegment valueSegment = fromByteBuf(value, valueBuffer, arena);
            invokePut(keySegment, keySegment.byteSize(), valueSegment, valueSegment.byteSize());
        } catch (Throwable e) {
            throw new DBException("Failed to put to write batch", e);
        }
        return this;
    }

    private void invokePut(MemorySegment key, long keyLength, MemorySegment value, long valueLength) throws Throwable {
        leveldb_writebatch_put.invokeExact(handle, key, keyLength, value, valueLength);
    }

    /**
     * If the database contains a mapping for "key", erase it.
     */
    public WriteBatch delete(byte[] key) {
        ensureOpen();
        try {
            MemorySegment keySegment = keyBuffer.copyFrom(key, arena);
            invokeDelete(keySegment, key.length);
        } catch (Throwable e) {
            throw new DBException("Failed to delete from write batch", e);
        }
        return this;
    }

    /**
     * Deletes a key from native memory without copying it on the Java side.
     */
    public WriteBatch delete(MemorySegment key) {
        ensureOpen();
        MemorySegment keySegment = requireNative(key, "key");
        try {
            invokeDelete(keySegment, keySegment.byteSize());
        } catch (Throwable e) {
            throw new DBException("Failed to delete from write batch", e);
        }
        return this;
    }

    /**
     * Deletes a key from the remaining region of an NIO byte buffer.
     * The position is not modified. A direct buffer is passed without copying.
     */
    public WriteBatch delete(ByteBuffer key) {
        ensureOpen();
        try {
            MemorySegment keySegment = fromByteBuffer(key, keyBuffer, arena);
            invokeDelete(keySegment, keySegment.byteSize());
        } catch (Throwable e) {
            throw new DBException("Failed to delete from write batch", e);
        }
        return this;
    }

    /**
     * Deletes a key from the readable region of a Netty buffer.
     * The reader index is not modified. A contiguous direct buffer is passed without copying.
     */
    public WriteBatch delete(ByteBuf key) {
        ensureOpen();
        try {
            MemorySegment keySegment = fromByteBuf(key, keyBuffer, arena);
            invokeDelete(keySegment, keySegment.byteSize());
        } catch (Throwable e) {
            throw new DBException("Failed to delete from write batch", e);
        }
        return this;
    }

    private void invokeDelete(MemorySegment key, long keyLength) throws Throwable {
        leveldb_writebatch_delete.invokeExact(handle, key, keyLength);
    }

    /**
     * Append all operations from the source batch to this batch.
     *
     * @param source the source batch whose operations will be appended
     * @return this WriteBatch for chaining
     */
    public WriteBatch append(WriteBatch source) {
        ensureOpen();
        source.ensureOpen();
        try {
            leveldb_writebatch_append.invokeExact(handle, source.handle);
        } catch (Throwable e) {
            throw new DBException("Failed to append write batch", e);
        }
        return this;
    }

    /**
     * Iterates over all operations in this batch, calling the appropriate handler for each put and delete operation.
     *
     * @param putHandler    handler called for each put operation
     * @param deleteHandler handler called for each delete operation
     */
    public void iterate(PutHandler putHandler, DeleteHandler deleteHandler) {
        ensureOpen();
        requireNonNull(putHandler, "putHandler");
        requireNonNull(deleteHandler, "deleteHandler");
        invokeIterate(PUT_CALLBACK_HANDLE.bindTo(putHandler), DELETE_CALLBACK_HANDLE.bindTo(deleteHandler));
    }

    /**
     * Iterates over all operations without copying keys or values to Java arrays.
     * Each non-owning read-only segment is valid only during its callback and must not be retained.
     *
     * @param putHandler    handler called for each put operation
     * @param deleteHandler handler called for each delete operation
     */
    public void iterateSegments(SegmentPutHandler putHandler, SegmentDeleteHandler deleteHandler) {
        ensureOpen();
        requireNonNull(putHandler, "putHandler");
        requireNonNull(deleteHandler, "deleteHandler");
        invokeIterate(SEGMENT_PUT_CALLBACK_HANDLE.bindTo(putHandler), SEGMENT_DELETE_CALLBACK_HANDLE.bindTo(deleteHandler));
    }

    private void invokeIterate(MethodHandle putTarget, MethodHandle deleteTarget) {
        try (Arena localArena = ofConfined()) {
            leveldb_writebatch_iterate.invokeExact(handle, NULL,
                    LINKER.upcallStub(putTarget, PUT_CALLBACK_DESCRIPTOR, localArena),
                    LINKER.upcallStub(deleteTarget, DELETE_CALLBACK_DESCRIPTOR, localArena));
        } catch (DBException e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to iterate write batch", e);
        }
    }

    private static MethodHandle findCallbackHandle(String name, MethodType type) {
        try {
            return MethodHandles.lookup().findStatic(WriteBatch.class, name, type);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @SuppressWarnings("unused")
    private static void putCallback(PutHandler handler, MemorySegment state, MemorySegment key, long keyLen, MemorySegment value, long valueLen) {
        byte[] keyBytes = key.reinterpret(keyLen).toArray(JAVA_BYTE);
        byte[] valueBytes = value.reinterpret(valueLen).toArray(JAVA_BYTE);
        handler.put(keyBytes, valueBytes);
    }

    @SuppressWarnings("unused")
    private static void segmentPutCallback(SegmentPutHandler handler, MemorySegment state, MemorySegment key, long keyLen, MemorySegment value, long valueLen) {
        handler.put(key.reinterpret(keyLen).asReadOnly(), value.reinterpret(valueLen).asReadOnly());
    }

    @SuppressWarnings("unused")
    private static void deleteCallback(DeleteHandler handler, MemorySegment state, MemorySegment key, long keyLen) {
        byte[] keyBytes = key.reinterpret(keyLen).toArray(JAVA_BYTE);
        handler.delete(keyBytes);
    }

    @SuppressWarnings("unused")
    private static void segmentDeleteCallback(SegmentDeleteHandler handler, MemorySegment state, MemorySegment key, long keyLen) {
        handler.delete(key.reinterpret(keyLen).asReadOnly());
    }

    MemorySegment getHandle() {
        ensureOpen();
        return handle;
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("WriteBatch has been closed");
        }
        if (!ownsArena) {
            ensureArenaThread();
            if (!arena.scope().isAlive()) {
                throw new IllegalStateException("Write batch arena has been closed");
            }
        }
    }

    private void ensureArenaThread() {
        if (!ownsArena && !arenaAccess.isAccessibleBy(Thread.currentThread())) {
            throw new WrongThreadException("Write batch arena belongs to another thread");
        }
    }

    @Override
    public void close() {
        ensureArenaThread();
        if (closed) {
            return;
        }
        closed = true;
        try {
            leveldb_writebatch_destroy.invokeExact(handle);
        } catch (Throwable e) {
            throw new DBException("Failed to destroy write batch", e);
        } finally {
            if (ownsArena) {
                arena.close();
            }
        }
    }

    /**
     * Handler for put operations during batch iteration.
     */
    @FunctionalInterface
    public interface PutHandler {
        void put(byte[] key, byte[] value);
    }

    /**
     * Handler for put operations using non-owning read-only native segments.
     */
    @FunctionalInterface
    public interface SegmentPutHandler {
        void put(MemorySegment key, MemorySegment value);
    }

    /**
     * Handler for delete operations during batch iteration.
     */
    @FunctionalInterface
    public interface DeleteHandler {
        void delete(byte[] key);
    }

    /**
     * Handler for delete operations using a non-owning read-only native segment.
     */
    @FunctionalInterface
    public interface SegmentDeleteHandler {
        void delete(MemorySegment key);
    }
}
