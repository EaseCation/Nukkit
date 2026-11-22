package cn.nukkit.level.format.leveldb;

import lombok.extern.log4j.Log4j2;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.Range;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.Snapshot;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.WriteOptions;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import static java.util.Objects.*;

@Log4j2
public final class FfmDB implements DB {
    public static boolean USE_NULL_LOGGER = true;
    private static final int DEFAULT_ZLIB_COMPRESSION_LEVEL = 1; // EC优化

    private static final AtomicBoolean LEGACY_COMPRESSION_LEVEL_WARNED = new AtomicBoolean();
    private static final AtomicBoolean LEGACY_NULL_LOGGER_WARNED = new AtomicBoolean();

    private final cn.nukkit.level.format.leveldb.ffm.DB delegate;
    private final ReentrantReadWriteLock lifecycle = new ReentrantReadWriteLock();
    private final ReadWriteLock writes = new ReentrantReadWriteLock();
    private final Set<FfmIterator> iterators = ConcurrentHashMap.newKeySet();
    private final Set<FfmSnapshot> snapshots = ConcurrentHashMap.newKeySet();
    private final cn.nukkit.level.format.leveldb.ffm.ReadOptions[] readOptions = new cn.nukkit.level.format.leveldb.ffm.ReadOptions[4];
    private final cn.nukkit.level.format.leveldb.ffm.WriteOptions syncWriteOptions;
    private final int defaultReadIndex;
    private boolean closed;

    private FfmDB(cn.nukkit.level.format.leveldb.ffm.DB delegate, boolean verifyChecksums) {
        this.delegate = delegate;
        this.defaultReadIndex = 2 | (verifyChecksums ? 1 : 0);
        cn.nukkit.level.format.leveldb.ffm.WriteOptions syncOptions = null;
        try {
            for (int index = 0; index < readOptions.length; index++) {
                readOptions[index] = new cn.nukkit.level.format.leveldb.ffm.ReadOptions();
                readOptions[index].setVerifyChecksums((index & 1) != 0).setFillCache((index & 2) != 0);
            }
            syncOptions = new cn.nukkit.level.format.leveldb.ffm.WriteOptions();
            syncOptions.setSync(true);
        } catch (RuntimeException | Error failure) {
            if (syncOptions != null) {
                closeOnFailure(syncOptions, failure);
            }
            for (cn.nukkit.level.format.leveldb.ffm.ReadOptions options : readOptions) {
                if (options != null) {
                    closeOnFailure(options, failure);
                }
            }
            closeOnFailure(delegate, failure);
            throw failure;
        }
        this.syncWriteOptions = syncOptions;
    }

    public static FfmDB open(File directory, Options options) throws IOException {
        requireNonNull(directory, "directory");
        requireNonNull(options, "options");
//        if (options.comparator() != null) {
//            throw new IllegalArgumentException("FFM LevelDB does not support iq80 custom comparators");
//        }
        cn.nukkit.level.format.leveldb.ffm.Options.CompressionType compression = switch (options.compressionType()) {
            case NONE -> cn.nukkit.level.format.leveldb.ffm.Options.CompressionType.NO_COMPRESSION;
            case SNAPPY -> cn.nukkit.level.format.leveldb.ffm.Options.CompressionType.SNAPPY_COMPRESSION;
            case ZLIB -> cn.nukkit.level.format.leveldb.ffm.Options.CompressionType.ZLIB_COMPRESSION;
            case ZLIB_RAW -> cn.nukkit.level.format.leveldb.ffm.Options.CompressionType.ZLIB_RAW_COMPRESSION;
        };
        try (cn.nukkit.level.format.leveldb.ffm.Options nativeOptions = new cn.nukkit.level.format.leveldb.ffm.Options()) {
            if (USE_NULL_LOGGER) {
                if (cn.nukkit.level.format.leveldb.ffm.Options.supportsNullLogger()) {
                    nativeOptions.setNullLogger();
                } else if (LEGACY_NULL_LOGGER_WARNED.compareAndSet(false, true)) {
                    log.warn("Native FFM LevelDB does not support a null logger; retaining native diagnostic logging");
                }
            }
            nativeOptions.setCreateIfMissing(options.createIfMissing())
                    .setErrorIfExists(options.errorIfExists())
                    .setParanoidChecks(options.paranoidChecks())
                    .setWriteBufferSize(options.writeBufferSize())
                    .setMaxOpenFiles(options.maxOpenFiles())
                    .setBlockRestartInterval(options.blockRestartInterval())
                    .setBlockSize(options.blockSize())
                    .setCompression(compression);
            if (compression == cn.nukkit.level.format.leveldb.ffm.Options.CompressionType.ZLIB_RAW_COMPRESSION) {
                if (cn.nukkit.level.format.leveldb.ffm.Options.supportsZlibCompressionLevel()) {
                    nativeOptions.setZlibCompressionLevel(DEFAULT_ZLIB_COMPRESSION_LEVEL);
                } else if (LEGACY_COMPRESSION_LEVEL_WARNED.compareAndSet(false, true)) {
                    log.warn("Native FFM LevelDB does not support selecting RAW compression level {}; retaining the native default", DEFAULT_ZLIB_COMPRESSION_LEVEL);
                }
            }
            nativeOptions.setBloomFilterPolicy(10);
            nativeOptions.setCacheSize(options.cacheSize() > 0 ? options.cacheSize() : 40L * 1024 * 1024);
            return new FfmDB(cn.nukkit.level.format.leveldb.ffm.DB.open(directory.toPath(), nativeOptions), options.verifyChecksums());
        } catch (cn.nukkit.level.format.leveldb.ffm.DBException failure) {
            throw new IOException("Unable to open FFM LevelDB: " + directory, failure);
        }
    }

    @Override
    @Nullable
    public byte[] get(byte[] key) {
        return access(() -> delegate.get(key, readOptions[defaultReadIndex]));
    }

    @Override
    @Nullable
    public byte[] get(byte[] key, ReadOptions options) {
        requireNonNull(options, "options");
        return access(() -> {
            if (options.snapshot() == null) {
                return delegate.get(key, cachedReadOptions(options));
            }
            try (cn.nukkit.level.format.leveldb.ffm.ReadOptions nativeOptions = snapshotReadOptions(options)) {
                return delegate.get(key, nativeOptions);
            }
        });
    }

    /**
     * 为一个区块保留数据库读锁，直到全部原生视图不再被解码器使用。
     */
    public ReadSession openReadSession() {
        Lock lock = lifecycle.readLock();
        lock.lock();
        cn.nukkit.level.format.leveldb.ffm.DB.ReadSession nativeSession = null;
        try {
            if (closed) {
                throw new IllegalStateException("Database has been closed");
            }
            nativeSession = delegate.openReadSession(readOptions[defaultReadIndex]);
            return new ReadSession(nativeSession, lock);
        } catch (RuntimeException | Error failure) {
            if (nativeSession != null) {
                closeOnFailure(nativeSession, failure);
            }
            lock.unlock();
            if (failure instanceof cn.nukkit.level.format.leveldb.ffm.DBException nativeFailure) {
                throw new DBException(nativeFailure.getMessage(), nativeFailure);
            }
            throw failure;
        }
    }

    @Override
    public FfmIterator iterator() {
        return access(() -> newIterator(readOptions[defaultReadIndex]));
    }

    @Override
    public FfmIterator iterator(ReadOptions options) {
        requireNonNull(options, "options");
        return access(() -> {
            if (options.snapshot() == null) {
                return newIterator(cachedReadOptions(options));
            }
            try (cn.nukkit.level.format.leveldb.ffm.ReadOptions nativeOptions = snapshotReadOptions(options)) {
                return newIterator(nativeOptions);
            }
        });
    }

    private FfmIterator newIterator(cn.nukkit.level.format.leveldb.ffm.ReadOptions options) {
        cn.nukkit.level.format.leveldb.ffm.DBIterator nativeIterator = delegate.iterator(options);
        try {
            nativeIterator.seekToFirst();
            nativeIterator.checkError();
            FfmIterator iterator = new FfmIterator(nativeIterator);
            iterators.add(iterator);
            return iterator;
        } catch (RuntimeException | Error failure) {
            closeOnFailure(nativeIterator, failure);
            throw failure;
        }
    }

    private cn.nukkit.level.format.leveldb.ffm.ReadOptions cachedReadOptions(ReadOptions options) {
        return readOptions[(options.verifyChecksums() ? 1 : 0) | (options.fillCache() ? 2 : 0)];
    }

    private cn.nukkit.level.format.leveldb.ffm.ReadOptions snapshotReadOptions(ReadOptions options) {
        if (!(options.snapshot() instanceof FfmSnapshot snapshot) || snapshot.owner() != this) {
            throw new IllegalArgumentException("Snapshot belongs to another database");
        }
        cn.nukkit.level.format.leveldb.ffm.ReadOptions nativeOptions = new cn.nukkit.level.format.leveldb.ffm.ReadOptions();
        try {
            return nativeOptions.setVerifyChecksums(options.verifyChecksums())
                    .setFillCache(options.fillCache())
                    .setSnapshot(snapshot.delegate);
        } catch (RuntimeException | Error failure) {
            closeOnFailure(nativeOptions, failure);
            throw failure;
        }
    }

    @Override
    public void put(byte[] key, byte[] value) {
        mutate(options -> delegate.put(key, value, options), null);
    }

    @Override
    public void delete(byte[] key) {
        mutate(options -> delegate.delete(key, options), null);
    }

    @Override
    public void write(WriteBatch updates) {
        write(updates, null);
    }

    @Override
    public FfmWriteBatch createWriteBatch() {
        return access(FfmWriteBatch::new);
    }

    /**
     * 借用调用方的 arena，避免单次区块保存额外创建和关闭共享作用域。
     * 必须先关闭批次，再关闭 arena；批次沿用该 arena 的线程限制。
     */
    public FfmWriteBatch createWriteBatch(Arena arena) {
        requireNonNull(arena, "arena");
        return access(() -> new FfmWriteBatch(arena));
    }

    @Override
    @Nullable
    public Snapshot put(byte[] key, byte[] value, @Nullable WriteOptions options) {
        return mutate(nativeOptions -> delegate.put(key, value, nativeOptions), options);
    }

    @Override
    @Nullable
    public Snapshot delete(byte[] key, @Nullable WriteOptions options) {
        return mutate(nativeOptions -> delegate.delete(key, nativeOptions), options);
    }

    @Override
    @Nullable
    public Snapshot write(WriteBatch updates, @Nullable WriteOptions options) {
        if (!(updates instanceof FfmWriteBatch batch)) {
            throw new IllegalArgumentException("Write batch was not created by FFM LevelDB");
        }
        return mutate(nativeOptions -> {
            synchronized (batch) {
                batch.ensureOpen();
                delegate.write(batch.delegate, nativeOptions);
            }
        }, options);
    }

    @Nullable
    private Snapshot mutate(NativeWrite operation, @Nullable WriteOptions options) {
        return access(() -> {
            boolean snapshotRequested = options != null && options.snapshot();
            // 普通写入仍可并行，仅写后快照需要排除其他写入，避免快照混入后续更新。
            Lock lock = snapshotRequested ? writes.writeLock() : writes.readLock();
            lock.lock();
            try {
                operation.run(options != null && options.sync() ? syncWriteOptions : null);
                return snapshotRequested ? newSnapshot() : null;
            } finally {
                lock.unlock();
            }
        });
    }

    @Override
    public Snapshot getSnapshot() {
        return access(this::newSnapshot);
    }

    private FfmSnapshot newSnapshot() {
        FfmSnapshot snapshot = new FfmSnapshot(delegate.createSnapshot());
        snapshots.add(snapshot);
        return snapshot;
    }

    @Override
    public long[] getApproximateSizes(Range... ranges) {
        requireNonNull(ranges, "ranges");
        byte[][] starts = new byte[ranges.length][];
        byte[][] limits = new byte[ranges.length][];
        for (int index = 0; index < ranges.length; index++) {
            starts[index] = ranges[index].start();
            limits[index] = ranges[index].limit();
        }
        return access(() -> delegate.getApproximateSizes(starts, limits));
    }

    @Override
    @Nullable
    public String getProperty(String name) {
        return access(() -> delegate.getProperty(name));
    }

    @Override
    public void suspendCompactions() {
        throw new UnsupportedOperationException("FFM LevelDB does not expose compaction suspension");
    }

    @Override
    public void resumeCompactions() {
        throw new UnsupportedOperationException("FFM LevelDB does not expose compaction suspension");
    }

    @Override
    public void compactRange(@Nullable byte[] begin, @Nullable byte[] end) {
        access(() -> {
            delegate.compactRange(begin, end);
            return null;
        });
    }

    private <T> T access(Supplier<T> operation) {
        Lock lock = lifecycle.readLock();
        lock.lock();
        try {
            if (closed) {
                throw new IllegalStateException("Database has been closed");
            }
            return operation.get();
        } catch (cn.nukkit.level.format.leveldb.ffm.DBException failure) {
            throw new DBException(failure.getMessage(), failure);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        if (lifecycle.getReadHoldCount() != 0) {
            throw new IllegalStateException("Cannot close database while this thread holds a read session");
        }
        Lock lock = lifecycle.writeLock();
        lock.lock();
        try {
            if (closed) {
                return;
            }
            closed = true;
            // 数据库先等待进行中的操作，再销毁依赖其句柄的游标和快照。
            IOException failure = null;
            for (FfmIterator iterator : iterators) {
                failure = closeResource(iterator::closeNative, failure);
            }
            iterators.clear();
            for (FfmSnapshot snapshot : snapshots) {
                failure = closeResource(snapshot::closeNative, failure);
            }
            snapshots.clear();
            for (cn.nukkit.level.format.leveldb.ffm.ReadOptions options : readOptions) {
                failure = closeResource(options, failure);
            }
            failure = closeResource(syncWriteOptions, failure);
            failure = closeResource(delegate, failure);
            if (failure != null) {
                throw failure;
            }
        } finally {
            lock.unlock();
        }
    }

    @Nullable
    private static IOException closeResource(AutoCloseable resource, @Nullable IOException failure) {
        try {
            resource.close();
        } catch (Exception cleanupFailure) {
            if (failure == null) {
                return new IOException("Failed to close FFM LevelDB resources", cleanupFailure);
            }
            failure.addSuppressed(cleanupFailure);
        }
        return failure;
    }

    private static void closeOnFailure(AutoCloseable resource, Throwable failure) {
        try {
            resource.close();
        } catch (Exception cleanupFailure) {
            failure.addSuppressed(cleanupFailure);
        }
    }

    @FunctionalInterface
    private interface NativeWrite {
        void run(@Nullable cn.nukkit.level.format.leveldb.ffm.WriteOptions options);
    }

    /**
     * 只允许创建线程使用和关闭；关闭会使所有键和值视图失效，并释放数据库读锁。
     */
    public static final class ReadSession implements AutoCloseable {
        private final Thread owner = Thread.currentThread();
        private final cn.nukkit.level.format.leveldb.ffm.DB.ReadSession delegate;
        private final Lock lock;
        private boolean closed;

        private ReadSession(cn.nukkit.level.format.leveldb.ffm.DB.ReadSession delegate, Lock lock) {
            this.delegate = delegate;
            this.lock = lock;
        }

        public MemorySegment allocateKey(int length) {
            return delegate.allocateKey(length);
        }

        /**
         * 返回会话持有的同线程原生游标；其借用值只允许在游标移动前使用。
         */
        public cn.nukkit.level.format.leveldb.ffm.DBIterator openCursor() {
            try {
                return delegate.openCursor();
            } catch (cn.nukkit.level.format.leveldb.ffm.DBException failure) {
                throw new DBException(failure.getMessage(), failure);
            }
        }

        @Nullable
        public MemorySegment get(byte[] key) {
            try {
                return delegate.get(key);
            } catch (cn.nukkit.level.format.leveldb.ffm.DBException failure) {
                throw new DBException(failure.getMessage(), failure);
            }
        }

        @Nullable
        public MemorySegment get(MemorySegment key) {
            try {
                return delegate.get(key);
            } catch (cn.nukkit.level.format.leveldb.ffm.DBException failure) {
                throw new DBException(failure.getMessage(), failure);
            }
        }

        @Override
        public void close() {
            if (Thread.currentThread() != owner) {
                throw new IllegalStateException("Read session belongs to another thread");
            }
            if (closed) {
                return;
            }
            closed = true;
            try {
                delegate.close();
            } catch (cn.nukkit.level.format.leveldb.ffm.DBException failure) {
                throw new DBException(failure.getMessage(), failure);
            } finally {
                lock.unlock();
            }
        }
    }

    public static final class FfmWriteBatch implements WriteBatch {
        private final cn.nukkit.level.format.leveldb.ffm.WriteBatch delegate;
        private boolean closed;

        private FfmWriteBatch() {
            this.delegate = new cn.nukkit.level.format.leveldb.ffm.WriteBatch();
        }

        private FfmWriteBatch(Arena arena) {
            this.delegate = new cn.nukkit.level.format.leveldb.ffm.WriteBatch(arena);
        }

        @Override
        public synchronized WriteBatch put(byte[] key, byte[] value) {
            ensureOpen();
            try {
                delegate.put(key, value);
                return this;
            } catch (cn.nukkit.level.format.leveldb.ffm.DBException failure) {
                throw new DBException(failure.getMessage(), failure);
            }
        }

        public synchronized FfmWriteBatch put(byte[] key, byte[] value, int offset, int length) {
            ensureOpen();
            try {
                delegate.put(key, value, offset, length);
                return this;
            } catch (cn.nukkit.level.format.leveldb.ffm.DBException failure) {
                throw new DBException(failure.getMessage(), failure);
            }
        }

        public synchronized FfmWriteBatch put(byte[] key, MemorySegment value) {
            ensureOpen();
            try {
                delegate.put(key, value);
                return this;
            } catch (cn.nukkit.level.format.leveldb.ffm.DBException failure) {
                throw new DBException(failure.getMessage(), failure);
            }
        }

        public synchronized FfmWriteBatch put(MemorySegment key, MemorySegment value) {
            ensureOpen();
            try {
                delegate.put(key, value);
                return this;
            } catch (cn.nukkit.level.format.leveldb.ffm.DBException failure) {
                throw new DBException(failure.getMessage(), failure);
            }
        }

        public synchronized FfmWriteBatch delete(MemorySegment key) {
            ensureOpen();
            try {
                delegate.delete(key);
                return this;
            } catch (cn.nukkit.level.format.leveldb.ffm.DBException failure) {
                throw new DBException(failure.getMessage(), failure);
            }
        }

        @Override
        public synchronized WriteBatch delete(byte[] key) {
            ensureOpen();
            try {
                delegate.delete(key);
                return this;
            } catch (cn.nukkit.level.format.leveldb.ffm.DBException failure) {
                throw new DBException(failure.getMessage(), failure);
            }
        }

        private void ensureOpen() {
            if (closed) {
                throw new IllegalStateException("Write batch has been closed");
            }
        }

        @Override
        public synchronized void close() {
            if (!closed) {
                delegate.close();
                closed = true;
            }
        }
    }

    private final class FfmSnapshot implements Snapshot {
        private final cn.nukkit.level.format.leveldb.ffm.Snapshot delegate;
        private boolean closed;

        private FfmSnapshot(cn.nukkit.level.format.leveldb.ffm.Snapshot delegate) {
            this.delegate = delegate;
        }

        private FfmDB owner() {
            return FfmDB.this;
        }

        @Override
        public void close() {
            Lock lock = lifecycle.readLock();
            lock.lock();
            try {
                closeNative();
                snapshots.remove(this);
            } finally {
                lock.unlock();
            }
        }

        private synchronized void closeNative() {
            if (!closed) {
                closed = true;
                delegate.close();
            }
        }
    }

    /**
     * 使用双向游标语义；只扫描区块键时可跳过 value 的堆内复制。
     */
    public final class FfmIterator implements DBIterator {
        private final cn.nukkit.level.format.leveldb.ffm.DBIterator delegate;
        private boolean closed;

        private FfmIterator(cn.nukkit.level.format.leveldb.ffm.DBIterator delegate) {
            this.delegate = delegate;
        }

        private <T> T accessIterator(Supplier<T> operation) {
            return access(() -> {
                synchronized (this) {
                    if (closed) {
                        throw new IllegalStateException("Iterator has been closed");
                    }
                    return operation.get();
                }
            });
        }

        private boolean valid() {
            if (delegate.isValid()) {
                return true;
            }
            delegate.checkError();
            return false;
        }

        @Override
        public boolean hasNext() {
            return accessIterator(this::valid);
        }

        @Override
        public Map.Entry<byte[], byte[]> next() {
            return accessIterator(() -> {
                Map.Entry<byte[], byte[]> entry = current();
                delegate.moveNext();
                return entry;
            });
        }

        public byte[] nextKey() {
            return accessIterator(() -> {
                if (!valid()) {
                    throw new NoSuchElementException();
                }
                byte[] key = delegate.key();
                delegate.moveNext();
                return key;
            });
        }

        @Override
        public Map.Entry<byte[], byte[]> peekNext() {
            return accessIterator(this::current);
        }

        private Map.Entry<byte[], byte[]> current() {
            if (!valid()) {
                throw new NoSuchElementException();
            }
            return Map.entry(delegate.key(), delegate.value());
        }

        @Override
        public void seek(byte[] key) {
            accessIterator(() -> {
                delegate.seek(key);
                delegate.checkError();
                return null;
            });
        }

        @Override
        public void seekToFirst() {
            accessIterator(() -> {
                delegate.seekToFirst();
                delegate.checkError();
                return null;
            });
        }

        @Override
        public void seekToLast() {
            accessIterator(() -> {
                delegate.seekToLast();
                if (valid()) {
                    delegate.moveNext();
                }
                return null;
            });
        }

        private boolean moveBefore() {
            if (valid()) {
                delegate.movePrev();
            } else {
                delegate.seekToLast();
            }
            return valid();
        }

        private void restoreNext(boolean hasPrevious) {
            if (hasPrevious) {
                delegate.moveNext();
            } else {
                delegate.seekToFirst();
            }
        }

        @Override
        public boolean hasPrev() {
            return accessIterator(() -> {
                boolean hasPrevious = moveBefore();
                restoreNext(hasPrevious);
                return hasPrevious;
            });
        }

        @Override
        public Map.Entry<byte[], byte[]> prev() {
            return accessIterator(() -> {
                if (!moveBefore()) {
                    delegate.seekToFirst();
                    throw new NoSuchElementException();
                }
                return current();
            });
        }

        @Override
        public Map.Entry<byte[], byte[]> peekPrev() {
            return accessIterator(() -> {
                boolean hasPrevious = moveBefore();
                try {
                    if (!hasPrevious) {
                        throw new NoSuchElementException();
                    }
                    return current();
                } finally {
                    restoreNext(hasPrevious);
                }
            });
        }

        @Override
        public void close() {
            Lock lock = lifecycle.readLock();
            lock.lock();
            try {
                closeNative();
                iterators.remove(this);
            } finally {
                lock.unlock();
            }
        }

        private synchronized void closeNative() {
            if (!closed) {
                closed = true;
                delegate.close();
            }
        }
    }
}
