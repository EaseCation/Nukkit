package cn.nukkit.level.format.leveldb.ffm;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import static cn.nukkit.level.format.leveldb.ffm.NativeBindings.*;
import static cn.nukkit.level.format.leveldb.ffm.PlatformUtil.*;
import static java.lang.foreign.Arena.*;
import static java.lang.foreign.MemorySegment.*;
import static java.lang.foreign.ValueLayout.*;
import static java.util.Objects.*;

/**
 * A LevelDB database.
 * <p>
 * Example usage:
 * <pre>{@code
 * try (Options options = new Options().setCreateIfMissing(true);
 *      DB db = DB.open("path/to/db", options)) {
 *
 *     byte[] key = {'k', 'e', 'y'};
 *     db.put(key, new byte[]{'v', 'a', 'l', 'u', 'e'});
 *     byte[] value = db.get(key);
 *     db.delete(key);
 * }
 * }</pre>
 */
public final class DB implements AutoCloseable {

    private static final ValueReader<byte[]> BYTE_ARRAY_READER = value -> value.toArray(JAVA_BYTE);

    private final MemorySegment handle;
    private final Options options;
    private final MemorySegment defaultReadOptions;
    private final MemorySegment defaultWriteOptions;
    private boolean closed;

    private DB(MemorySegment handle, Options options) {
        this.handle = handle;
        this.options = options;

        MemorySegment readOptions = NULL;
        MemorySegment writeOptions = NULL;
        try {
            readOptions = (MemorySegment) leveldb_readoptions_create.invokeExact();
            if (readOptions.equals(NULL)) {
                throw new DBException("Failed to create default read options: returned null");
            }

            writeOptions = (MemorySegment) leveldb_writeoptions_create.invokeExact();
            if (writeOptions.equals(NULL)) {
                throw new DBException("Failed to create default write options: returned null");
            }
        } catch (Throwable e) {
            Throwable failure = e;
            if (writeOptions != null && !writeOptions.equals(NULL)) {
                try {
                    leveldb_writeoptions_destroy.invokeExact(writeOptions);
                } catch (Throwable cleanupFailure) {
                    failure = addFailure(failure, cleanupFailure);
                }
            }
            if (readOptions != null && !readOptions.equals(NULL)) {
                try {
                    leveldb_readoptions_destroy.invokeExact(readOptions);
                } catch (Throwable cleanupFailure) {
                    failure = addFailure(failure, cleanupFailure);
                }
            }
            try {
                leveldb_close.invokeExact(handle);
            } catch (Throwable cleanupFailure) {
                failure = addFailure(failure, cleanupFailure);
            }
            throw new DBException("Failed to initialize database", failure);
        }

        this.defaultReadOptions = readOptions;
        this.defaultWriteOptions = writeOptions;
    }

    /**
     * Opens a LevelDB database at the given path with the specified options.
     *
     * @param path    the path to the database directory
     * @param options the options for opening the database
     * @return a new LevelDB instance
     * @throws DBException if the database cannot be opened
     */
    public static DB open(String path, Options options) {
        MemorySegment optionsHandle = options.retainForDatabase();
        try (Arena localArena = ofConfined()) {
            MemorySegment pathSegment = localArena.allocateFrom(path);
            MemorySegment errorPtr = localArena.allocate(ADDRESS);

            MemorySegment db = (MemorySegment) leveldb_open.invokeExact(optionsHandle, pathSegment, errorPtr);

            checkError(errorPtr);

            if (db.equals(NULL)) {
                throw new DBException("Failed to open database: returned null");
            }

            return new DB(db, options);
        } catch (DBException | IllegalStateException e) {
            releaseOptionsAfterFailedOpen(options, e);
            throw e;
        } catch (Throwable e) {
            DBException failure = new DBException("Failed to open database", e);
            releaseOptionsAfterFailedOpen(options, failure);
            throw failure;
        }
    }

    private static void releaseOptionsAfterFailedOpen(Options options, Throwable failure) {
        try {
            options.releaseFromDatabase();
        } catch (Throwable releaseFailure) {
            failure.addSuppressed(releaseFailure);
        }
    }

    /**
     * Opens a LevelDB database at the given path with the specified options.
     *
     * @param path    the path to the database directory
     * @param options the options for opening the database
     * @return a new LevelDB instance
     * @throws DBException if the database cannot be opened
     */
    public static DB open(Path path, Options options) {
        return open(path.toString(), options);
    }

    /**
     * Sets the database entry for "key" to "value".
     *
     * @param key   the key
     * @param value the value
     * @throws DBException if the operation fails
     */
    public void put(byte[] key, byte[] value) {
        put(key, value, null);
    }

    /**
     * Sets the database entry for "key" to "value".
     *
     * @param key     the key
     * @param value   the value
     * @param options the write options (may be null)
     * @throws DBException if the operation fails
     */
    public void put(byte[] key, byte[] value, WriteOptions options) {
        ensureOpen();
        try (Arena localArena = ofConfined()) {
            MemorySegment keySegment = localArena.allocateFrom(JAVA_BYTE, key);
            MemorySegment valueSegment = localArena.allocateFrom(JAVA_BYTE, value);
            MemorySegment errorPtr = localArena.allocate(ADDRESS);

            invokePut(keySegment, key.length, valueSegment, value.length, options, errorPtr);
        } catch (DBException e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to put value", e);
        }
    }

    /**
     * Sets a database entry from native memory without copying the key or value.
     */
    public void put(MemorySegment key, MemorySegment value) {
        put(key, value, null);
    }

    /**
     * Sets a database entry from native memory without copying the key or value.
     */
    public void put(MemorySegment key, MemorySegment value, WriteOptions options) {
        ensureOpen();
        MemorySegment keySegment = requireNative(key, "key");
        MemorySegment valueSegment = requireNative(value, "value");
        try (Arena localArena = ofConfined()) {
            MemorySegment errorPtr = localArena.allocate(ADDRESS);
            invokePut(keySegment, keySegment.byteSize(), valueSegment, valueSegment.byteSize(), options, errorPtr);
        } catch (DBException e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to put value", e);
        }
    }

    /**
     * Sets a database entry from the remaining regions of two NIO byte buffers.
     * Positions are not modified. Direct buffers are passed without copying.
     */
    public void put(ByteBuffer key, ByteBuffer value) {
        put(key, value, null);
    }

    /**
     * Sets a database entry from the remaining regions of two NIO byte buffers.
     * Positions are not modified. Direct buffers are passed without copying.
     */
    public void put(ByteBuffer key, ByteBuffer value, WriteOptions options) {
        ensureOpen();
        try (Arena localArena = ofConfined()) {
            MemorySegment keySegment = fromByteBuffer(key, localArena);
            MemorySegment valueSegment = fromByteBuffer(value, localArena);
            MemorySegment errorPtr = localArena.allocate(ADDRESS);
            invokePut(keySegment, keySegment.byteSize(), valueSegment, valueSegment.byteSize(), options, errorPtr);
        } catch (DBException e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to put value", e);
        }
    }

    /**
     * Sets a database entry from the readable regions of two Netty buffers.
     * Reader indexes are not modified. Contiguous direct buffers are passed without copying.
     */
    public void put(ByteBuf key, ByteBuf value) {
        put(key, value, null);
    }

    /**
     * Sets a database entry from the readable regions of two Netty buffers.
     * Reader indexes are not modified. Contiguous direct buffers are passed without copying.
     */
    public void put(ByteBuf key, ByteBuf value, WriteOptions options) {
        ensureOpen();
        try (Arena localArena = ofConfined()) {
            MemorySegment keySegment = fromByteBuf(key, localArena);
            MemorySegment valueSegment = fromByteBuf(value, localArena);
            MemorySegment errorPtr = localArena.allocate(ADDRESS);
            invokePut(keySegment, keySegment.byteSize(), valueSegment, valueSegment.byteSize(), options, errorPtr);
        } catch (DBException e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to put value", e);
        }
    }

    private void invokePut(MemorySegment key, long keyLength, MemorySegment value, long valueLength, WriteOptions options, MemorySegment errorPtr) throws Throwable {
        MemorySegment writeOptionsHandle = options != null ? options.getHandle() : defaultWriteOptions;
        leveldb_put.invokeExact(handle, writeOptionsHandle, key, keyLength, value, valueLength, errorPtr);
        checkError(errorPtr);
    }

    /**
     * 为一组相关读取复用临时原生内存，返回值在会话关闭前有效。
     * 会话只能由创建它的线程使用；调用者必须保证数据库在会话期间保持打开。
     */
    public ReadSession openReadSession() {
        return openReadSession(null);
    }

    /**
     * 借用指定读取选项；选项必须保持有效直到会话关闭。
     * 每个会话独立持有内存，允许区块加载在同一线程重入。
     */
    public ReadSession openReadSession(@Nullable ReadOptions options) {
        ensureOpen();
        return new ReadSession(options);
    }

    /**
     * 同线程读取会话，持有的所有原生值在关闭时统一释放并失效。
     */
    public final class ReadSession implements AutoCloseable {
        private final Thread owner = Thread.currentThread();
        private final Arena arena;
        private final NativeBuffer keyBuffer = new NativeBuffer();
        private final MemorySegment lengthPtr;
        private final MemorySegment dataPtr;
        private final MemorySegment errorPtr;
        private final MemorySegment readOptionsHandle;
        @Nullable
        private final ReadOptions.HandleLease readOptionsLease;
        @Nullable
        private DBIterator cursor;
        private boolean closed;

        private ReadSession(@Nullable ReadOptions options) {
            Arena allocatedArena = ofConfined();
            ReadOptions.HandleLease lease = null;
            try {
                this.lengthPtr = allocatedArena.allocate(JAVA_LONG);
                this.dataPtr = allocatedArena.allocate(ADDRESS);
                this.errorPtr = allocatedArena.allocate(ADDRESS);
                lease = options == null ? null : options.acquireHandle();
                this.readOptionsHandle = lease == null ? defaultReadOptions : lease.handle();
                this.readOptionsLease = lease;
                this.arena = allocatedArena;
            } catch (RuntimeException | Error failure) {
                try {
                    allocatedArena.close();
                } catch (RuntimeException | Error cleanupFailure) {
                    failure.addSuppressed(cleanupFailure);
                }
                if (lease != null) {
                    lease.close();
                }
                throw failure;
            }
        }

        /**
         * 分配可重复改写的键缓冲区，与本会话共享生命周期。
         */
        public MemorySegment allocateKey(int length) {
            ensureUsable();
            if (length < 0) {
                throw new IllegalArgumentException("Key length must not be negative");
            }
            return arena.allocate(Math.max(length, 1), 1).asSlice(0, length);
        }

        /**
         * 懒创建本会话独占的同线程游标；会话关闭时先关闭游标，再释放普通读取值。
         */
        public DBIterator openCursor() {
            ensureUsable();
            if (cursor == null) {
                try {
                    MemorySegment iterator = (MemorySegment) leveldb_create_iterator.invokeExact(handle, readOptionsHandle);
                    cursor = new DBIterator(iterator, true);
                } catch (RuntimeException | Error failure) {
                    throw failure;
                } catch (Throwable failure) {
                    throw new DBException("Failed to create session cursor", failure);
                }
            }
            return cursor;
        }

        @Nullable
        public MemorySegment get(byte[] key) {
            ensureUsable();
            return read(keyBuffer.copyFrom(key, arena));
        }

        @Nullable
        public MemorySegment get(MemorySegment key) {
            ensureUsable();
            return read(requireNative(key, "key"));
        }

        @Nullable
        private MemorySegment read(MemorySegment key) {
            errorPtr.set(ADDRESS, 0, NULL);
            lengthPtr.set(JAVA_LONG, 0, 0L);
            NativeBindings.OwnedValueBindings ownedValues = OWNED_VALUES;
            if (ownedValues != null) {
                return readOwned(key, ownedValues);
            }
            MemorySegment valuePtr = NULL;
            boolean ownedByArena = false;
            try {
                valuePtr = (MemorySegment) leveldb_get.invokeExact(handle, readOptionsHandle, key, key.byteSize(), lengthPtr, errorPtr);
                checkError(errorPtr);
                if (valuePtr.equals(NULL)) {
                    return null;
                }
                long length = lengthPtr.get(JAVA_LONG, 0);
                MemorySegment value = valuePtr.reinterpret(length, arena, DB::freeValue);
                ownedByArena = true;
                return value.asReadOnly();
            } catch (RuntimeException | Error failure) {
                throw failure;
            } catch (Throwable failure) {
                throw new DBException("Failed to read value in session", failure);
            } finally {
                if (!ownedByArena && !valuePtr.equals(NULL)) {
                    freeValue(valuePtr);
                }
            }
        }

        @Nullable
        private MemorySegment readOwned(MemorySegment key, NativeBindings.OwnedValueBindings bindings) {
            dataPtr.set(ADDRESS, 0, NULL);
            MemorySegment valueOwner = NULL;
            boolean ownedByArena = false;
            try {
                valueOwner = (MemorySegment) bindings.get().invokeExact(handle, readOptionsHandle, key, key.byteSize(), dataPtr, lengthPtr, errorPtr);
                checkError(errorPtr);
                if (valueOwner.equals(NULL)) {
                    return null;
                }
                MemorySegment valuePtr = dataPtr.get(ADDRESS, 0);
                if (valuePtr.equals(NULL)) {
                    throw new DBException("Owned value returned a null data pointer");
                }
                long length = lengthPtr.get(JAVA_LONG, 0);
                MemorySegment retainedOwner = valueOwner;
                // 释放的是字符串所有者，不是其数据地址；短字符串也适用。
                MemorySegment value = valuePtr.reinterpret(length, arena, _ -> freeOwnedValue(bindings, retainedOwner));
                ownedByArena = true;
                return value.asReadOnly();
            } catch (RuntimeException | Error failure) {
                throw failure;
            } catch (Throwable failure) {
                throw new DBException("Failed to read owned value in session", failure);
            } finally {
                if (!ownedByArena && !valueOwner.equals(NULL)) {
                    freeOwnedValue(bindings, valueOwner);
                }
            }
        }

        private void ensureUsable() {
            ensureOwner();
            if (closed) {
                throw new IllegalStateException("Read session has been closed");
            }
            DB.this.ensureOpen();
        }

        private void ensureOwner() {
            if (Thread.currentThread() != owner) {
                throw new IllegalStateException("Read session belongs to another thread");
            }
        }

        @Override
        public void close() {
            ensureOwner();
            if (closed) {
                return;
            }
            closed = true;
            try (ReadOptions.HandleLease ignoredLease = readOptionsLease;
                 Arena ignoredArena = arena;
                 DBIterator ignoredCursor = cursor) {
                // 反向关闭资源，保留清理失败的原始异常及 suppressed 信息。
            }
        }
    }

    /**
     * 当前原生库是否提供成对的所有权读取扩展，供诊断与兼容性验收使用。
     */
    public static boolean supportsOwnedValues() {
        return OWNED_VALUES != null;
    }

    private static void freeOwnedValue(NativeBindings.OwnedValueBindings bindings, MemorySegment owner) {
        try {
            bindings.destroy().invokeExact(owner);
        } catch (Throwable failure) {
            throw new DBException("Failed to free owned value", failure);
        }
    }

    /**
     * Gets the value for the given key.
     *
     * @param key the key
     * @return the value, or null if the key is not found
     * @throws DBException if the operation fails
     */
    public byte[] get(byte[] key) {
        return get(key, null);
    }

    /**
     * Gets the value for the given key.
     *
     * @param key     the key
     * @param options the read options (may be null)
     * @return the value, or null if the key is not found
     * @throws DBException if the operation fails
     */
    public byte[] get(byte[] key, ReadOptions options) {
        return withValue(key, options, BYTE_ARRAY_READER);
    }

    /**
     * Gets a value using a key in native memory without copying the key.
     */
    public byte[] get(MemorySegment key) {
        return get(key, null);
    }

    /**
     * Gets a value using a key in native memory without copying the key.
     */
    public byte[] get(MemorySegment key, ReadOptions options) {
        return withValue(key, options, BYTE_ARRAY_READER);
    }

    /**
     * Gets a value using the remaining region of an NIO byte buffer as the key.
     * The buffer position is not modified.
     */
    public byte[] get(ByteBuffer key) {
        return get(key, (ReadOptions) null);
    }

    /**
     * Gets a value using the remaining region of an NIO byte buffer as the key.
     * The buffer position is not modified.
     */
    public byte[] get(ByteBuffer key, ReadOptions options) {
        return withValue(key, options, BYTE_ARRAY_READER);
    }

    /**
     * Gets a value using the readable region of a Netty buffer as the key.
     */
    public byte[] get(ByteBuf key) {
        return get(key, (ReadOptions) null);
    }

    /**
     * Gets a value using the readable region of a Netty buffer as the key.
     */
    public byte[] get(ByteBuf key, ReadOptions options) {
        return withValue(key, options, BYTE_ARRAY_READER);
    }

    private MemorySegment invokeGet(ReadOptions options, MemorySegment key, long keyLength, MemorySegment lengthPtr, MemorySegment errorPtr) throws Throwable {
        if (options == null) {
            return (MemorySegment) leveldb_get.invokeExact(handle, defaultReadOptions, key, keyLength, lengthPtr, errorPtr);
        }
        try (ReadOptions.HandleLease readOptionsLease = options.acquireHandle()) {
            return (MemorySegment) leveldb_get.invokeExact(handle, readOptionsLease.handle(), key, keyLength, lengthPtr, errorPtr);
        }
    }

    /**
     * Copies a value into the remaining region of an NIO byte buffer without creating
     * an intermediate byte array. The destination position advances when the key exists.
     *
     * @return true if the key exists
     */
    public boolean get(ByteBuffer key, ByteBuffer destination) {
        return get(key, destination, null);
    }

    /**
     * Copies a value into the remaining region of an NIO byte buffer without creating
     * an intermediate byte array. The destination position advances when the key exists.
     *
     * @return true if the key exists
     */
    public boolean get(ByteBuffer key, ByteBuffer destination, ReadOptions options) {
        requireNonNull(destination, "destination");
        Boolean found = withValue(key, options, value -> {
            destination.put(value.asByteBuffer());
            return Boolean.TRUE;
        });
        return Boolean.TRUE == found;
    }

    /**
     * Copies a value directly into a Netty buffer without creating an intermediate byte array.
     * The destination writer index is advanced when the key exists.
     *
     * @return true if the key exists
     */
    public boolean get(ByteBuf key, ByteBuf destination) {
        return get(key, destination, null);
    }

    /**
     * Copies a value directly into a Netty buffer without creating an intermediate byte array.
     * The destination writer index is advanced when the key exists.
     *
     * @return true if the key exists
     */
    public boolean get(ByteBuf key, ByteBuf destination, ReadOptions options) {
        requireNonNull(destination, "destination");
        Boolean found = withValue(key, options, value -> {
            destination.writeBytes(value.asByteBuffer());
            return Boolean.TRUE;
        });
        return Boolean.TRUE == found;
    }

    /**
     * Reads a value through a temporary read-only native segment.
     * The segment is only valid during the callback and must not be retained.
     *
     * @return the callback result, or null if the key does not exist
     */
    public <T> T withValue(byte[] key, ValueReader<T> reader) {
        return withValue(key, null, reader);
    }

    /**
     * Reads a value through a temporary read-only native segment.
     * The segment is only valid during the callback and must not be retained.
     *
     * @return the callback result, or null if the key does not exist
     */
    public <T> T withValue(byte[] key, ReadOptions options, ValueReader<T> reader) {
        ensureOpen();
        requireNonNull(reader, "reader");
        try (Arena localArena = ofConfined()) {
            MemorySegment keySegment = localArena.allocateFrom(JAVA_BYTE, key);
            return readValue(keySegment, key.length, options, reader, localArena);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to get value", e);
        }
    }

    /**
     * Reads a value through a temporary read-only native segment without copying the native key.
     * The value segment is only valid during the callback and must not be retained.
     *
     * @return the callback result, or null if the key does not exist
     */
    public <T> T withValue(MemorySegment key, ValueReader<T> reader) {
        return withValue(key, null, reader);
    }

    /**
     * Reads a value through a temporary read-only native segment without copying the native key.
     * The value segment is only valid during the callback and must not be retained.
     *
     * @return the callback result, or null if the key does not exist
     */
    public <T> T withValue(MemorySegment key, ReadOptions options, ValueReader<T> reader) {
        ensureOpen();
        requireNonNull(reader, "reader");
        MemorySegment keySegment = requireNative(key, "key");
        try (Arena localArena = ofConfined()) {
            return readValue(keySegment, keySegment.byteSize(), options, reader, localArena);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to get value", e);
        }
    }

    /**
     * Reads a value through a temporary read-only native segment using an NIO byte buffer key.
     * The key position is not modified. The value segment is only valid during the callback.
     *
     * @return the callback result, or null if the key does not exist
     */
    public <T> T withValue(ByteBuffer key, ValueReader<T> reader) {
        return withValue(key, null, reader);
    }

    /**
     * Reads a value through a temporary read-only native segment using an NIO byte buffer key.
     * The key position is not modified. The value segment is only valid during the callback.
     *
     * @return the callback result, or null if the key does not exist
     */
    public <T> T withValue(ByteBuffer key, ReadOptions options, ValueReader<T> reader) {
        ensureOpen();
        requireNonNull(reader, "reader");
        try (Arena localArena = ofConfined()) {
            MemorySegment keySegment = fromByteBuffer(key, localArena);
            return readValue(keySegment, keySegment.byteSize(), options, reader, localArena);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to get value", e);
        }
    }

    /**
     * Reads a value through a temporary read-only native segment using a Netty buffer key.
     * The value segment is only valid during the callback and must not be retained.
     *
     * @return the callback result, or null if the key does not exist
     */
    public <T> T withValue(ByteBuf key, ValueReader<T> reader) {
        return withValue(key, null, reader);
    }

    /**
     * Reads a value through a temporary read-only native segment using a Netty buffer key.
     * The value segment is only valid during the callback and must not be retained.
     *
     * @return the callback result, or null if the key does not exist
     */
    public <T> T withValue(ByteBuf key, ReadOptions options, ValueReader<T> reader) {
        ensureOpen();
        requireNonNull(reader, "reader");
        try (Arena localArena = ofConfined()) {
            MemorySegment keySegment = fromByteBuf(key, localArena);
            return readValue(keySegment, keySegment.byteSize(), options, reader, localArena);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to get value", e);
        }
    }

    private <T> T readValue(MemorySegment key, long keyLength, ReadOptions options, ValueReader<T> reader, Arena allocator) throws Throwable {
        MemorySegment lengthPtr = allocator.allocate(JAVA_LONG);
        MemorySegment errorPtr = allocator.allocate(ADDRESS);
        MemorySegment valuePtr = invokeGet(options, key, keyLength, lengthPtr, errorPtr);

        checkError(errorPtr);
        if (valuePtr.equals(NULL)) {
            return null;
        }

        long length = lengthPtr.get(JAVA_LONG, 0);
        MemorySegment value = valuePtr.reinterpret(length, allocator, DB::freeValue).asReadOnly();
        return reader.read(value);
    }

    private static void freeValue(MemorySegment value) {
        try {
            leveldb_free.invokeExact(value);
        } catch (Throwable e) {
            throw new DBException("Failed to free value", e);
        }
    }

    /**
     * Removes the database entry (if any) for "key".
     *
     * @param key the key
     * @throws DBException if the operation fails
     */
    public void delete(byte[] key) {
        delete(key, null);
    }

    /**
     * Removes the database entry (if any) for "key".
     *
     * @param key     the key
     * @param options the write options (may be null)
     * @throws DBException if the operation fails
     */
    public void delete(byte[] key, WriteOptions options) {
        ensureOpen();
        try (Arena localArena = ofConfined()) {
            MemorySegment keySegment = localArena.allocateFrom(JAVA_BYTE, key);
            MemorySegment errorPtr = localArena.allocate(ADDRESS);
            invokeDelete(keySegment, key.length, options, errorPtr);
        } catch (DBException e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to delete value", e);
        }
    }

    /**
     * Removes an entry using a key in native memory without copying the key.
     */
    public void delete(MemorySegment key) {
        delete(key, null);
    }

    /**
     * Removes an entry using a key in native memory without copying the key.
     */
    public void delete(MemorySegment key, WriteOptions options) {
        ensureOpen();
        MemorySegment keySegment = requireNative(key, "key");
        try (Arena localArena = ofConfined()) {
            MemorySegment errorPtr = localArena.allocate(ADDRESS);
            invokeDelete(keySegment, keySegment.byteSize(), options, errorPtr);
        } catch (DBException e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to delete value", e);
        }
    }

    /**
     * Removes an entry using the remaining region of an NIO byte buffer as the key.
     * The buffer position is not modified. A direct buffer is passed without copying.
     */
    public void delete(ByteBuffer key) {
        delete(key, null);
    }

    /**
     * Removes an entry using the remaining region of an NIO byte buffer as the key.
     * The buffer position is not modified. A direct buffer is passed without copying.
     */
    public void delete(ByteBuffer key, WriteOptions options) {
        ensureOpen();
        try (Arena localArena = ofConfined()) {
            MemorySegment keySegment = fromByteBuffer(key, localArena);
            MemorySegment errorPtr = localArena.allocate(ADDRESS);
            invokeDelete(keySegment, keySegment.byteSize(), options, errorPtr);
        } catch (DBException e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to delete value", e);
        }
    }

    /**
     * Removes an entry using the readable region of a Netty buffer as the key.
     * The reader index is not modified. A contiguous direct buffer is passed without copying.
     */
    public void delete(ByteBuf key) {
        delete(key, null);
    }

    /**
     * Removes an entry using the readable region of a Netty buffer as the key.
     * The reader index is not modified. A contiguous direct buffer is passed without copying.
     */
    public void delete(ByteBuf key, WriteOptions options) {
        ensureOpen();
        try (Arena localArena = ofConfined()) {
            MemorySegment keySegment = fromByteBuf(key, localArena);
            MemorySegment errorPtr = localArena.allocate(ADDRESS);
            invokeDelete(keySegment, keySegment.byteSize(), options, errorPtr);
        } catch (DBException e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to delete value", e);
        }
    }

    private void invokeDelete(MemorySegment key, long keyLength, WriteOptions options, MemorySegment errorPtr) throws Throwable {
        MemorySegment writeOptionsHandle = options != null ? options.getHandle() : defaultWriteOptions;
        leveldb_delete.invokeExact(handle, writeOptionsHandle, key, keyLength, errorPtr);
        checkError(errorPtr);
    }

    /**
     * Apply the specified updates to the database.
     *
     * @param batch the write batch
     * @throws DBException if the operation fails
     */
    public void write(WriteBatch batch) {
        write(batch, null);
    }

    /**
     * Apply the specified updates to the database.
     *
     * @param batch   the write batch
     * @param options the write options (may be null)
     * @throws DBException if the operation fails
     */
    public void write(WriteBatch batch, WriteOptions options) {
        ensureOpen();
        try (Arena localArena = ofConfined()) {
            MemorySegment writeOptionsHandle = options != null ? options.getHandle() : defaultWriteOptions;
            MemorySegment errorPtr = localArena.allocate(ADDRESS);

            leveldb_write.invokeExact(handle, writeOptionsHandle, batch.getHandle(), errorPtr);

            checkError(errorPtr);
        } catch (DBException e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to write batch", e);
        }
    }

    /**
     * Creates a new iterator over the database contents.
     *
     * @return a new iterator
     * @throws DBException if the operation fails
     */
    public DBIterator iterator() {
        return iterator(null);
    }

    /**
     * Creates a new iterator over the database contents.
     *
     * @param options the read options (may be null)
     * @return a new iterator
     * @throws DBException if the operation fails
     */
    public DBIterator iterator(ReadOptions options) {
        ensureOpen();
        try {
            MemorySegment iter = invokeCreateIterator(options);
            return new DBIterator(iter);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to create iterator", e);
        }
    }

    private MemorySegment invokeCreateIterator(ReadOptions options) throws Throwable {
        if (options == null) {
            return (MemorySegment) leveldb_create_iterator.invokeExact(handle, defaultReadOptions);
        }
        try (ReadOptions.HandleLease readOptionsLease = options.acquireHandle()) {
            return (MemorySegment) leveldb_create_iterator.invokeExact(handle, readOptionsLease.handle());
        }
    }

    /**
     * Creates a snapshot of the current state of the database.
     *
     * @return a new snapshot
     * @throws DBException if the operation fails
     */
    public Snapshot createSnapshot() {
        ensureOpen();
        try {
            MemorySegment snapshot = (MemorySegment) leveldb_create_snapshot.invokeExact(handle);
            return new Snapshot(snapshot, this);
        } catch (Throwable e) {
            throw new DBException("Failed to create snapshot", e);
        }
    }

    /**
     * Releases a previously acquired snapshot.
     *
     * @param snapshotHandle the snapshot to release
     */
    void releaseSnapshot(MemorySegment snapshotHandle) {
        ensureOpen();
        try {
            leveldb_release_snapshot.invokeExact(handle, snapshotHandle);
        } catch (Throwable e) {
            throw new DBException("Failed to release snapshot", e);
        }
    }

    /**
     * Gets a database property value.
     *
     * @param property the property name
     * @return the property value, or null if not found
     */
    public String getProperty(String property) {
        ensureOpen();
        try (Arena localArena = ofConfined()) {
            MemorySegment propertySegment = localArena.allocateFrom(property);
            MemorySegment valuePtr = (MemorySegment) leveldb_property_value.invokeExact(handle, propertySegment);

            if (valuePtr.equals(NULL)) {
                return null;
            }

            String result = valuePtr.reinterpret(Long.MAX_VALUE).getString(0);
            leveldb_free.invokeExact(valuePtr);
            return result;
        } catch (Throwable e) {
            throw new DBException("Failed to get property", e);
        }
    }

    /**
     * 返回各个左闭右开键区间在磁盘中的估算大小，不包含尚未落盘的内存表。
     */
    public long[] getApproximateSizes(byte[][] starts, byte[][] limits) {
        ensureOpen();
        requireNonNull(starts, "starts");
        requireNonNull(limits, "limits");
        if (starts.length != limits.length) {
            throw new IllegalArgumentException("Range start and limit counts differ");
        }
        if (starts.length == 0) {
            return new long[0];
        }
        try (Arena localArena = ofConfined()) {
            MemorySegment startPointers = localArena.allocate(ADDRESS, starts.length);
            MemorySegment startLengths = localArena.allocate(JAVA_LONG, starts.length);
            MemorySegment limitPointers = localArena.allocate(ADDRESS, starts.length);
            MemorySegment limitLengths = localArena.allocate(JAVA_LONG, starts.length);
            MemorySegment sizes = localArena.allocate(JAVA_LONG, starts.length);
            for (int index = 0; index < starts.length; index++) {
                byte[] start = requireNonNull(starts[index], "start");
                byte[] limit = requireNonNull(limits[index], "limit");
                startPointers.setAtIndex(ADDRESS, index, localArena.allocateFrom(JAVA_BYTE, start));
                startLengths.setAtIndex(JAVA_LONG, index, start.length);
                limitPointers.setAtIndex(ADDRESS, index, localArena.allocateFrom(JAVA_BYTE, limit));
                limitLengths.setAtIndex(JAVA_LONG, index, limit.length);
            }
            leveldb_approximate_sizes.invokeExact(handle, starts.length, startPointers, startLengths, limitPointers, limitLengths, sizes);
            return sizes.toArray(JAVA_LONG);
        } catch (Throwable failure) {
            throw new DBException("Failed to estimate range sizes", failure);
        }
    }

    /**
     * Compact the underlying storage for the key range [begin, end].
     *
     * @param begin start of key range (inclusive), or null for the beginning
     * @param end   end of key range (exclusive), or null for the end
     */
    public void compactRange(byte[] begin, byte[] end) {
        ensureOpen();
        try (Arena localArena = ofConfined()) {
            MemorySegment beginSegment = begin != null ? localArena.allocateFrom(JAVA_BYTE, begin) : NULL;
            MemorySegment endSegment = end != null ? localArena.allocateFrom(JAVA_BYTE, end) : NULL;

            leveldb_compact_range.invokeExact(handle, beginSegment,
                    begin != null ? (long) begin.length : 0L,
                    endSegment, end != null ? (long) end.length : 0L);
        } catch (Throwable e) {
            throw new DBException("Failed to compact range", e);
        }
    }

    /**
     * Compact the entire database.
     */
    public void compactRange() {
        compactRange(null, null);
    }

    /**
     * Destroys the contents of the specified database.
     *
     * @param path    the path to the database
     * @param options the options
     * @throws DBException if the operation fails
     */
    public static void destroy(String path, Options options) {
        try (Arena localArena = ofConfined()) {
            MemorySegment pathSegment = localArena.allocateFrom(path);
            MemorySegment errorPtr = localArena.allocate(ADDRESS);

            leveldb_destroy_db.invokeExact(options.getHandle(), pathSegment, errorPtr);

            checkError(errorPtr);
        } catch (DBException e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to destroy database", e);
        }
    }

    /**
     * Repairs the specified database.
     *
     * @param path    the path to the database
     * @param options the options
     * @throws DBException if the operation fails
     */
    public static void repair(String path, Options options) {
        try (Arena localArena = ofConfined()) {
            MemorySegment pathSegment = localArena.allocateFrom(path);
            MemorySegment errorPtr = localArena.allocate(ADDRESS);

            leveldb_repair_db.invokeExact(options.getHandle(), pathSegment, errorPtr);

            checkError(errorPtr);
        } catch (DBException e) {
            throw e;
        } catch (Throwable e) {
            throw new DBException("Failed to repair database", e);
        }
    }

    /**
     * Gets the major version of the LevelDB library.
     */
    public static int getMajorVersion() {
        return NativeBindings.getMajorVersion();
    }

    /**
     * Gets the minor version of the LevelDB library.
     */
    public static int getMinorVersion() {
        return NativeBindings.getMinorVersion();
    }

    private static void checkError(MemorySegment errorPtr) {
        MemorySegment error = errorPtr.get(ADDRESS, 0);
        if (!error.equals(NULL)) {
            String errorMsg = error.reinterpret(Long.MAX_VALUE).getString(0);
            try {
                leveldb_free.invokeExact(error);
            } catch (Throwable ignored) {
            } finally {
                errorPtr.set(ADDRESS, 0, NULL);
            }
            throw new DBException(errorMsg);
        }
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("Database has been closed");
        }
    }

    @Override
    public synchronized void close() {
        if (closed) {
            return;
        }
        closed = true;

        Throwable failure = null;
        try {
            leveldb_close.invokeExact(handle);
        } catch (Throwable e) {
            failure = e;
        }
        try {
            leveldb_readoptions_destroy.invokeExact(defaultReadOptions);
        } catch (Throwable e) {
            failure = addFailure(failure, e);
        }
        try {
            leveldb_writeoptions_destroy.invokeExact(defaultWriteOptions);
        } catch (Throwable e) {
            failure = addFailure(failure, e);
        }
        try {
            options.releaseFromDatabase();
        } catch (Throwable e) {
            failure = addFailure(failure, e);
        }
        if (failure != null) {
            throw new DBException("Failed to close database", failure);
        }
    }

    private static Throwable addFailure(Throwable current, Throwable next) {
        if (current == null) {
            return next;
        }
        current.addSuppressed(next);
        return current;
    }

    /**
     * Reads a temporary value segment returned by LevelDB without copying it.
     */
    @FunctionalInterface
    public interface ValueReader<T> {
        T read(MemorySegment value);
    }
}
