package cn.nukkit.level.format.leveldb.ffm;

import javax.annotation.Nullable;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

import static cn.nukkit.level.format.leveldb.ffm.NativeBindings.*;

/**
 * Options to control the behavior of a database.
 */
public final class Options implements AutoCloseable {

    private final MemorySegment handle;
    @Nullable
    private MemorySegment cacheHandle;
    @Nullable
    private MemorySegment filterPolicyHandle;
    private int openDatabases;
    private boolean closed;
    private boolean nativeResourcesReleased;

    /**
     * Creates a new Options instance with default settings.
     */
    public Options() {
        try {
            this.handle = (MemorySegment) leveldb_options_create.invokeExact();
        } catch (Throwable e) {
            throw new DBException("Failed to create options", e);
        }
    }

    /**
     * If true, the database will be created if it is missing.
     * Default: false
     */
    public Options setCreateIfMissing(boolean value) {
        ensureOpen();
        try {
            leveldb_options_set_create_if_missing.invokeExact(handle, (byte) (value ? 1 : 0));
        } catch (Throwable e) {
            throw new DBException("Failed to set create_if_missing", e);
        }
        return this;
    }

    /**
     * If true, an error is raised if the database already exists.
     * Default: false
     */
    public Options setErrorIfExists(boolean value) {
        ensureOpen();
        try {
            leveldb_options_set_error_if_exists.invokeExact(handle, (byte) (value ? 1 : 0));
        } catch (Throwable e) {
            throw new DBException("Failed to set error_if_exists", e);
        }
        return this;
    }

    /**
     * If true, the implementation will do aggressive checking of the data it is processing and will stop early if it detects any errors.
     * Default: false
     */
    public Options setParanoidChecks(boolean value) {
        ensureOpen();
        try {
            leveldb_options_set_paranoid_checks.invokeExact(handle, (byte) (value ? 1 : 0));
        } catch (Throwable e) {
            throw new DBException("Failed to set paranoid_checks", e);
        }
        return this;
    }

    /**
     * Amount of data to build up in memory (backed by an unsorted log on disk) before converting to a sorted on-disk file.
     * Default: 4MB
     */
    public Options setWriteBufferSize(long size) {
        ensureOpen();
        try {
            leveldb_options_set_write_buffer_size.invokeExact(handle, size);
        } catch (Throwable e) {
            throw new DBException("Failed to set write_buffer_size", e);
        }
        return this;
    }

    /**
     * Number of open files that can be used by the DB.
     * Default: 1000
     */
    public Options setMaxOpenFiles(int num) {
        ensureOpen();
        try {
            leveldb_options_set_max_open_files.invokeExact(handle, num);
        } catch (Throwable e) {
            throw new DBException("Failed to set max_open_files", e);
        }
        return this;
    }

    /**
     * Approximate size of user data packed per block.
     * Default: 4K
     */
    public Options setBlockSize(long size) {
        ensureOpen();
        try {
            leveldb_options_set_block_size.invokeExact(handle, size);
        } catch (Throwable e) {
            throw new DBException("Failed to set block_size", e);
        }
        return this;
    }

    /**
     * Compress blocks using the specified compression algorithm.
     * Default: SNAPPY_COMPRESSION
     */
    public Options setCompression(CompressionType compression) {
        ensureOpen();
        try {
            leveldb_options_set_compression.invokeExact(handle, compression.getValue());
        } catch (Throwable e) {
            throw new DBException("Failed to set compression", e);
        }
        return this;
    }

    /**
     * 当前原生库是否提供成对的 zlib 压缩等级设置和回读扩展。
     */
    public static boolean supportsZlibCompressionLevel() {
        return ZLIB_COMPRESSION_LEVEL != null;
    }

    /**
     * 设置后续写入使用的 zlib 等级；-1 保留原生默认，0 到 9 为显式等级。
     * 此选项不会重写已有 SST，也不会改变其压缩类型编号。
     */
    public Options setZlibCompressionLevel(int level) {
        ensureOpen();
        if (level < -1 || level > 9) {
            throw new IllegalArgumentException("Zlib compression level must be between -1 and 9");
        }
        NativeBindings.ZlibCompressionLevelBindings bindings = ZLIB_COMPRESSION_LEVEL;
        if (bindings == null) {
            throw new UnsupportedOperationException("Native LevelDB does not support zlib compression-level options");
        }
        try {
            bindings.set().invokeExact(handle, level);
        } catch (Throwable failure) {
            throw new DBException("Failed to set zlib compression level", failure);
        }
        return this;
    }

    /**
     * 回读原生选项中的等级，供配置验证使用；旧库不支持时明确拒绝。
     */
    public int getZlibCompressionLevel() {
        ensureOpen();
        NativeBindings.ZlibCompressionLevelBindings bindings = ZLIB_COMPRESSION_LEVEL;
        if (bindings == null) {
            throw new UnsupportedOperationException("Native LevelDB does not support zlib compression-level options");
        }
        try {
            return (int) bindings.get().invokeExact(handle);
        } catch (Throwable failure) {
            throw new DBException("Failed to get zlib compression level", failure);
        }
    }

    /**
     * 当前原生库是否提供库自身持有的无输出日志器。
     */
    public static boolean supportsNullLogger() {
        return NULL_LOGGER != null;
    }

    /**
     * 禁止创建诊断 LOG/LOG.old 文件。
     */
    public Options setNullLogger() {
        ensureOpen();
        MethodHandle setter = NULL_LOGGER;
        if (setter == null) {
            throw new UnsupportedOperationException("Native LevelDB does not support a library-owned NullLogger");
        }
        try {
            setter.invokeExact(handle);
        } catch (Throwable failure) {
            throw new DBException("Failed to set native NullLogger", failure);
        }
        return this;
    }

    /**
     * Maximum size of each file.
     * Default: 2MB
     */
    public Options setMaxFileSize(long size) {
        ensureOpen();
        try {
            leveldb_options_set_max_file_size.invokeExact(handle, size);
        } catch (Throwable e) {
            throw new DBException("Failed to set max_file_size", e);
        }
        return this;
    }

    /**
     * Sets the size of the LRU cache for blocks.
     * Default: 8MB (internal cache)
     *
     * @param size cache size in bytes
     */
    public synchronized Options setCacheSize(long size) {
        ensureOpen();
        ensureNoOpenDatabases("cache");
        MemorySegment newCache = null;
        boolean installed = false;
        try {
            newCache = (MemorySegment) leveldb_cache_create_lru.invokeExact(size);
            leveldb_options_set_cache.invokeExact(handle, newCache);

            MemorySegment oldCache = cacheHandle;
            cacheHandle = newCache;
            installed = true;
            if (oldCache != null) {
                leveldb_cache_destroy.invokeExact(oldCache);
            }
        } catch (Throwable e) {
            if (!installed && newCache != null) {
                try {
                    leveldb_cache_destroy.invokeExact(newCache);
                } catch (Throwable cleanupFailure) {
                    e.addSuppressed(cleanupFailure);
                }
            }
            throw new DBException("Failed to set cache_size", e);
        }
        return this;
    }

    /**
     * Number of keys between restart points for delta encoding of keys.
     * Default: 16
     *
     * @param interval the block restart interval
     */
    public Options setBlockRestartInterval(int interval) {
        ensureOpen();
        try {
            leveldb_options_set_block_restart_interval.invokeExact(handle, interval);
        } catch (Throwable e) {
            throw new DBException("Failed to set block_restart_interval", e);
        }
        return this;
    }

    /**
     * Disables automatic compactions triggered by seeks.
     * This is a Mojang-specific extension.
     * Default: false
     *
     * @param value true to disable seek-based auto compaction
     */
    public Options setDisableSeekAutoCompaction(boolean value) {
        ensureOpen();
        try {
            leveldb_options_set_disable_seek_autocompaction.invokeExact(handle, (byte) (value ? 1 : 0));
        } catch (Throwable e) {
            throw new DBException("Failed to set disable_seek_autocompaction", e);
        }
        return this;
    }

    /**
     * Sets a custom comparator for key ordering.
     * The comparator must remain alive while the database is open.
     *
     * @param comparator the comparator to use
     */
    public Options setComparator(DBComparator comparator) {
        ensureOpen();
        try {
            leveldb_options_set_comparator.invokeExact(handle, comparator.getHandle());
        } catch (Throwable e) {
            throw new DBException("Failed to set comparator", e);
        }
        return this;
    }

    /**
     * Sets a custom comparator for key ordering (low-level).
     *
     * @param comparator the native comparator handle
     */
    public Options setComparator(MemorySegment comparator) {
        ensureOpen();
        try {
            leveldb_options_set_comparator.invokeExact(handle, comparator);
        } catch (Throwable e) {
            throw new DBException("Failed to set comparator", e);
        }
        return this;
    }

    /**
     * Sets the environment for the database to use.
     * The env must remain alive while the database is open.
     *
     * @param env the environment to use
     */
    public Options setEnv(Env env) {
        ensureOpen();
        try {
            leveldb_options_set_env.invokeExact(handle, env.getHandle());
        } catch (Throwable e) {
            throw new DBException("Failed to set env", e);
        }
        return this;
    }

    /**
     * Sets the environment for the database to use (low-level).
     *
     * @param env the native env handle
     */
    public Options setEnv(MemorySegment env) {
        ensureOpen();
        try {
            leveldb_options_set_env.invokeExact(handle, env);
        } catch (Throwable e) {
            throw new DBException("Failed to set env", e);
        }
        return this;
    }

    /**
     * Sets the info log for the database (low-level).
     *
     * @param logger the native logger handle
     */
    public Options setInfoLog(MemorySegment logger) {
        ensureOpen();
        try {
            leveldb_options_set_info_log.invokeExact(handle, logger);
        } catch (Throwable e) {
            throw new DBException("Failed to set info_log", e);
        }
        return this;
    }

    /**
     * Sets a Bloom filter policy to reduce disk reads.
     *
     * @param bitsPerKey bits per key for the bloom filter (typically 10)
     */
    public synchronized Options setBloomFilterPolicy(int bitsPerKey) {
        ensureOpen();
        ensureNoOpenDatabases("filter policy");
        MemorySegment newFilterPolicy = null;
        boolean installed = false;
        try {
            newFilterPolicy = (MemorySegment) leveldb_filterpolicy_create_bloom.invokeExact(bitsPerKey);
            leveldb_options_set_filter_policy.invokeExact(handle, newFilterPolicy);

            MemorySegment oldFilterPolicy = filterPolicyHandle;
            filterPolicyHandle = newFilterPolicy;
            installed = true;
            if (oldFilterPolicy != null) {
                leveldb_filterpolicy_destroy.invokeExact(oldFilterPolicy);
            }
        } catch (Throwable e) {
            if (!installed && newFilterPolicy != null) {
                try {
                    leveldb_filterpolicy_destroy.invokeExact(newFilterPolicy);
                } catch (Throwable cleanupFailure) {
                    e.addSuppressed(cleanupFailure);
                }
            }
            throw new DBException("Failed to set filter_policy", e);
        }
        return this;
    }

    MemorySegment getHandle() {
        ensureOpen();
        return handle;
    }

    synchronized MemorySegment retainForDatabase() {
        ensureOpen();
        openDatabases++;
        return handle;
    }

    synchronized void releaseFromDatabase() {
        if (openDatabases == 0) {
            throw new IllegalStateException("Options has no open database reference");
        }
        openDatabases--;
        if (closed && openDatabases == 0) {
            releaseNativeResources();
        }
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("Options has been closed");
        }
    }

    private void ensureNoOpenDatabases(String resource) {
        if (openDatabases != 0) {
            throw new IllegalStateException("Cannot replace " + resource + " while a database is open");
        }
    }

    @Override
    public synchronized void close() {
        if (closed) {
            return;
        }
        closed = true;
        if (openDatabases == 0) {
            releaseNativeResources();
        }
    }

    private void releaseNativeResources() {
        if (nativeResourcesReleased) {
            return;
        }
        nativeResourcesReleased = true;

        Throwable failure = null;
        try {
            leveldb_options_destroy.invokeExact(handle);
        } catch (Throwable e) {
            failure = e;
        }
        if (cacheHandle != null) {
            try {
                leveldb_cache_destroy.invokeExact(cacheHandle);
            } catch (Throwable e) {
                failure = addFailure(failure, e);
            } finally {
                cacheHandle = null;
            }
        }
        if (filterPolicyHandle != null) {
            try {
                leveldb_filterpolicy_destroy.invokeExact(filterPolicyHandle);
            } catch (Throwable e) {
                failure = addFailure(failure, e);
            } finally {
                filterPolicyHandle = null;
            }
        }
        if (failure != null) {
            throw new DBException("Failed to destroy options", failure);
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
     * Compression types for LevelDB.
     */
    public enum CompressionType {
        NO_COMPRESSION(0),
        SNAPPY_COMPRESSION(1),
        ZLIB_COMPRESSION(2),
        ZLIB_RAW_COMPRESSION(4);

        private final int value;

        CompressionType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
