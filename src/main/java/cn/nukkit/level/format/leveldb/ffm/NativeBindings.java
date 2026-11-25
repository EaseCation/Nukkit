package cn.nukkit.level.format.leveldb.ffm;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.foreign.FunctionDescriptor.*;
import static java.lang.foreign.ValueLayout.*;

/**
 * Low-level FFM bindings for LevelDB C API.
 */
final class NativeBindings {

    static final Linker LINKER = Linker.nativeLinker();
    private static final SymbolLookup LOOKUP = loadLibrary();

    // Function handles
    public static final MethodHandle leveldb_open = downcallHandle("leveldb_open",
            of(ADDRESS, ADDRESS, ADDRESS, ADDRESS));
    public static final MethodHandle leveldb_close = downcallHandle("leveldb_close",
            ofVoid(ADDRESS));
    public static final MethodHandle leveldb_put = downcallHandle("leveldb_put",
            ofVoid(ADDRESS, ADDRESS, ADDRESS, JAVA_LONG, ADDRESS, JAVA_LONG, ADDRESS));
    public static final MethodHandle leveldb_get = downcallHandle("leveldb_get",
            of(ADDRESS, ADDRESS, ADDRESS, ADDRESS, JAVA_LONG, ADDRESS, ADDRESS));
    public static final MethodHandle leveldb_delete = downcallHandle("leveldb_delete",
            ofVoid(ADDRESS, ADDRESS, ADDRESS, JAVA_LONG, ADDRESS));
    public static final MethodHandle leveldb_free = downcallHandle("leveldb_free",
            ofVoid(ADDRESS));
    @Nullable
    static final OwnedValueBindings OWNED_VALUES = findOwnedValueBindings(LOOKUP);

    // Options
    public static final MethodHandle leveldb_options_create = downcallHandle("leveldb_options_create",
            of(ADDRESS));
    public static final MethodHandle leveldb_options_destroy = downcallHandle("leveldb_options_destroy",
            ofVoid(ADDRESS));
    public static final MethodHandle leveldb_options_set_create_if_missing = downcallHandle("leveldb_options_set_create_if_missing",
            ofVoid(ADDRESS, JAVA_BYTE));
    public static final MethodHandle leveldb_options_set_error_if_exists = downcallHandle("leveldb_options_set_error_if_exists",
            ofVoid(ADDRESS, JAVA_BYTE));
    public static final MethodHandle leveldb_options_set_paranoid_checks = downcallHandle("leveldb_options_set_paranoid_checks",
            ofVoid(ADDRESS, JAVA_BYTE));
    public static final MethodHandle leveldb_options_set_write_buffer_size = downcallHandle("leveldb_options_set_write_buffer_size",
            ofVoid(ADDRESS, JAVA_LONG));
    public static final MethodHandle leveldb_options_set_max_open_files = downcallHandle("leveldb_options_set_max_open_files",
            ofVoid(ADDRESS, JAVA_INT));
    public static final MethodHandle leveldb_options_set_block_size = downcallHandle("leveldb_options_set_block_size",
            ofVoid(ADDRESS, JAVA_LONG));
    public static final MethodHandle leveldb_options_set_compression = downcallHandle("leveldb_options_set_compression",
            ofVoid(ADDRESS, JAVA_INT));
    @Nullable
    static final ZlibCompressionLevelBindings ZLIB_COMPRESSION_LEVEL = findZlibCompressionLevelBindings(LOOKUP);
    public static final MethodHandle leveldb_options_set_max_file_size = downcallHandle("leveldb_options_set_max_file_size",
            ofVoid(ADDRESS, JAVA_LONG));
    public static final MethodHandle leveldb_options_set_block_restart_interval = downcallHandle("leveldb_options_set_block_restart_interval",
            ofVoid(ADDRESS, JAVA_INT));
    public static final MethodHandle leveldb_options_set_disable_seek_autocompaction = downcallHandle("leveldb_options_set_disable_seek_autocompaction",
            ofVoid(ADDRESS, JAVA_BYTE));
    public static final MethodHandle leveldb_options_set_comparator = downcallHandle("leveldb_options_set_comparator",
            ofVoid(ADDRESS, ADDRESS));
    public static final MethodHandle leveldb_options_set_env = downcallHandle("leveldb_options_set_env",
            ofVoid(ADDRESS, ADDRESS));
    public static final MethodHandle leveldb_options_set_info_log = downcallHandle("leveldb_options_set_info_log",
            ofVoid(ADDRESS, ADDRESS));
    @Nullable
    static final MethodHandle NULL_LOGGER = findNullLoggerBinding(LOOKUP);
    public static final MethodHandle leveldb_options_set_cache = downcallHandle("leveldb_options_set_cache",
            ofVoid(ADDRESS, ADDRESS));
    public static final MethodHandle leveldb_options_set_filter_policy = downcallHandle("leveldb_options_set_filter_policy",
            ofVoid(ADDRESS, ADDRESS));

    // Cache
    public static final MethodHandle leveldb_cache_create_lru = downcallHandle("leveldb_cache_create_lru",
            of(ADDRESS, JAVA_LONG));
    public static final MethodHandle leveldb_cache_destroy = downcallHandle("leveldb_cache_destroy",
            ofVoid(ADDRESS));

    // Filter Policy
    public static final MethodHandle leveldb_filterpolicy_create = downcallHandle("leveldb_filterpolicy_create",
            of(ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS));
    public static final MethodHandle leveldb_filterpolicy_create_bloom = downcallHandle("leveldb_filterpolicy_create_bloom",
            of(ADDRESS, JAVA_INT));
    public static final MethodHandle leveldb_filterpolicy_destroy = downcallHandle("leveldb_filterpolicy_destroy",
            ofVoid(ADDRESS));

    // Read options
    public static final MethodHandle leveldb_readoptions_create = downcallHandle("leveldb_readoptions_create",
            of(ADDRESS));
    public static final MethodHandle leveldb_readoptions_destroy = downcallHandle("leveldb_readoptions_destroy",
            ofVoid(ADDRESS));
    public static final MethodHandle leveldb_readoptions_set_verify_checksums = downcallHandle("leveldb_readoptions_set_verify_checksums",
            ofVoid(ADDRESS, JAVA_BYTE));
    public static final MethodHandle leveldb_readoptions_set_fill_cache = downcallHandle("leveldb_readoptions_set_fill_cache",
            ofVoid(ADDRESS, JAVA_BYTE));
    public static final MethodHandle leveldb_readoptions_set_snapshot = downcallHandle("leveldb_readoptions_set_snapshot",
            ofVoid(ADDRESS, ADDRESS));

    // Write options
    public static final MethodHandle leveldb_writeoptions_create = downcallHandle("leveldb_writeoptions_create",
            of(ADDRESS));
    public static final MethodHandle leveldb_writeoptions_destroy = downcallHandle("leveldb_writeoptions_destroy",
            ofVoid(ADDRESS));
    public static final MethodHandle leveldb_writeoptions_set_sync = downcallHandle("leveldb_writeoptions_set_sync",
            ofVoid(ADDRESS, JAVA_BYTE));

    // WriteBatch
    public static final MethodHandle leveldb_writebatch_create = downcallHandle("leveldb_writebatch_create",
            of(ADDRESS));
    public static final MethodHandle leveldb_writebatch_destroy = downcallHandle("leveldb_writebatch_destroy",
            ofVoid(ADDRESS));
    public static final MethodHandle leveldb_writebatch_clear = downcallHandle("leveldb_writebatch_clear",
            ofVoid(ADDRESS));
    public static final MethodHandle leveldb_writebatch_put = downcallHandle("leveldb_writebatch_put",
            ofVoid(ADDRESS, ADDRESS, JAVA_LONG, ADDRESS, JAVA_LONG));
    public static final MethodHandle leveldb_writebatch_delete = downcallHandle("leveldb_writebatch_delete",
            ofVoid(ADDRESS, ADDRESS, JAVA_LONG));
    public static final MethodHandle leveldb_writebatch_append = downcallHandle("leveldb_writebatch_append",
            ofVoid(ADDRESS, ADDRESS));
    public static final MethodHandle leveldb_writebatch_iterate = downcallHandle("leveldb_writebatch_iterate",
            ofVoid(ADDRESS, ADDRESS, ADDRESS, ADDRESS));
    public static final MethodHandle leveldb_write = downcallHandle("leveldb_write",
            ofVoid(ADDRESS, ADDRESS, ADDRESS, ADDRESS));

    // Iterator
    public static final MethodHandle leveldb_create_iterator = downcallHandle("leveldb_create_iterator",
            of(ADDRESS, ADDRESS, ADDRESS));
    public static final MethodHandle leveldb_iter_destroy = downcallHandle("leveldb_iter_destroy",
            ofVoid(ADDRESS));
    public static final MethodHandle leveldb_iter_valid = downcallHandle("leveldb_iter_valid",
            of(JAVA_BYTE, ADDRESS));
    public static final MethodHandle leveldb_iter_seek_to_first = downcallHandle("leveldb_iter_seek_to_first",
            ofVoid(ADDRESS));
    public static final MethodHandle leveldb_iter_seek_to_last = downcallHandle("leveldb_iter_seek_to_last",
            ofVoid(ADDRESS));
    public static final MethodHandle leveldb_iter_seek = downcallHandle("leveldb_iter_seek",
            ofVoid(ADDRESS, ADDRESS, JAVA_LONG));
    public static final MethodHandle leveldb_iter_next = downcallHandle("leveldb_iter_next",
            ofVoid(ADDRESS));
    public static final MethodHandle leveldb_iter_prev = downcallHandle("leveldb_iter_prev",
            ofVoid(ADDRESS));
    public static final MethodHandle leveldb_iter_key = downcallHandle("leveldb_iter_key",
            of(ADDRESS, ADDRESS, ADDRESS));
    public static final MethodHandle leveldb_iter_value = downcallHandle("leveldb_iter_value",
            of(ADDRESS, ADDRESS, ADDRESS));
    public static final MethodHandle leveldb_iter_get_error = downcallHandle("leveldb_iter_get_error",
            ofVoid(ADDRESS, ADDRESS));

    // Snapshot
    public static final MethodHandle leveldb_create_snapshot = downcallHandle("leveldb_create_snapshot",
            of(ADDRESS, ADDRESS));
    public static final MethodHandle leveldb_release_snapshot = downcallHandle("leveldb_release_snapshot",
            ofVoid(ADDRESS, ADDRESS));

    // Utility
    public static final MethodHandle leveldb_property_value = downcallHandle("leveldb_property_value",
            of(ADDRESS, ADDRESS, ADDRESS));
    public static final MethodHandle leveldb_approximate_sizes = downcallHandle("leveldb_approximate_sizes",
            ofVoid(ADDRESS, JAVA_INT, ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS));
    public static final MethodHandle leveldb_compact_range = downcallHandle("leveldb_compact_range",
            ofVoid(ADDRESS, ADDRESS, JAVA_LONG, ADDRESS, JAVA_LONG));
    public static final MethodHandle leveldb_destroy_db = downcallHandle("leveldb_destroy_db",
            ofVoid(ADDRESS, ADDRESS, ADDRESS));
    public static final MethodHandle leveldb_repair_db = downcallHandle("leveldb_repair_db",
            ofVoid(ADDRESS, ADDRESS, ADDRESS));

    // Comparator
    public static final MethodHandle leveldb_comparator_create = downcallHandle("leveldb_comparator_create",
            of(ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS));
    public static final MethodHandle leveldb_comparator_destroy = downcallHandle("leveldb_comparator_destroy",
            ofVoid(ADDRESS));

    // Env
    public static final MethodHandle leveldb_create_default_env = downcallHandle("leveldb_create_default_env",
            of(ADDRESS));
    public static final MethodHandle leveldb_env_destroy = downcallHandle("leveldb_env_destroy",
            ofVoid(ADDRESS));
    public static final MethodHandle leveldb_env_get_test_directory = downcallHandle("leveldb_env_get_test_directory",
            of(ADDRESS, ADDRESS));

    // Version
    public static final MethodHandle leveldb_major_version = downcallHandle("leveldb_major_version",
            of(JAVA_INT));
    public static final MethodHandle leveldb_minor_version = downcallHandle("leveldb_minor_version",
            of(JAVA_INT));

    private static SymbolLookup loadLibrary() {
        String osName = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();

        String libraryFileName;

        if (osName.contains("win")) {
            libraryFileName = "leveldb.dll";
        } else if (osName.contains("mac")) {
            libraryFileName = "libleveldb.dylib";
        } else {
            libraryFileName = "libleveldb.so";
        }

        String archName;
        String archNameAlt = null;  // Alternative architecture name
        if (arch.contains("aarch64") || arch.contains("arm64")) {
            archName = "arm64";
            archNameAlt = "aarch64";
        } else if (arch.contains("amd64") || arch.contains("x86_64")) {
            archName = "x86_64";
            archNameAlt = "amd64";
        } else {
            archName = arch;
        }

        // Try bundled resources first. Libraries are packaged by CPU architecture.
        List<String> pathList = new ArrayList<>();
        pathList.add("/" + archName + "/" + libraryFileName);
        if (archNameAlt != null) {
            pathList.add("/" + archNameAlt + "/" + libraryFileName);
        }
        pathList.add("/" + libraryFileName);

        String[] resourcePaths = pathList.toArray(new String[0]);

        for (String resPath : resourcePaths) {
            try (InputStream is = NativeBindings.class.getResourceAsStream(resPath)) {
                if (is != null) {
                    Path tempDir = Files.createTempDirectory("leveldb-ffm");
                    Path tempLib = tempDir.resolve(libraryFileName);
                    Files.copy(is, tempLib, StandardCopyOption.REPLACE_EXISTING);
                    tempLib.toFile().deleteOnExit();
                    tempDir.toFile().deleteOnExit();

                    System.load(tempLib.toAbsolutePath().toString());
                    return SymbolLookup.loaderLookup();
                }
            } catch (IOException | UnsatisfiedLinkError ignored) {
                // Try next path
            }
        }

        // Fallback: try system library path
        try {
            System.loadLibrary("leveldb");
            return SymbolLookup.loaderLookup();
        } catch (UnsatisfiedLinkError e) {
            // Try loading from common file paths
            String[] filePaths = {
                    libraryFileName,
                    "./" + libraryFileName
            };

            for (String path : filePaths) {
                try {
                    System.load(new File(path).getAbsolutePath());
                    return SymbolLookup.loaderLookup();
                } catch (UnsatisfiedLinkError ignored) {
                }
            }

            throw new ExceptionInInitializerError(
                    "Failed to load LevelDB native library. " +
                            "Please ensure the bundled native library is present or leveldb is on the system path. " +
                            "Tried resource paths: " + String.join(", ", resourcePaths) + ". " +
                            "Original error: " + e.getMessage());
        }
    }

    private static MethodHandle downcallHandle(String name, FunctionDescriptor descriptor) {
        return LOOKUP.find(name)
                .map(addr -> LINKER.downcallHandle(addr, descriptor))
                .orElseThrow(() -> new UnsatisfiedLinkError("Function not found: " + name));
    }

    /**
     * 新旧原生包可以并存；只有读取和释放两个扩展同时存在才启用所有权读取。
     */
    @Nullable
    static OwnedValueBindings findOwnedValueBindings(SymbolLookup lookup) {
        Optional<MemorySegment> get = lookup.find("leveldb_get_owned");
        Optional<MemorySegment> destroy = lookup.find("leveldb_owned_value_destroy");
        if (get.isEmpty() || destroy.isEmpty()) {
            return null;
        }
        return new OwnedValueBindings(
                LINKER.downcallHandle(get.get(), of(ADDRESS, ADDRESS, ADDRESS, ADDRESS, JAVA_LONG, ADDRESS, ADDRESS, ADDRESS)),
                LINKER.downcallHandle(destroy.get(), ofVoid(ADDRESS)));
    }

    record OwnedValueBindings(MethodHandle get, MethodHandle destroy) {
    }

    /**
     * 只有设置和回读接口同时存在，才允许覆盖原生库的通用压缩等级默认值。
     */
    @Nullable
    static ZlibCompressionLevelBindings findZlibCompressionLevelBindings(SymbolLookup lookup) {
        Optional<MemorySegment> setter = lookup.find("leveldb_options_set_zlib_compression_level");
        Optional<MemorySegment> getter = lookup.find("leveldb_options_get_zlib_compression_level");
        if (setter.isEmpty() || getter.isEmpty()) {
            return null;
        }
        return new ZlibCompressionLevelBindings(
                LINKER.downcallHandle(setter.get(), ofVoid(ADDRESS, JAVA_INT)),
                LINKER.downcallHandle(getter.get(), of(JAVA_INT, ADDRESS)));
    }

    record ZlibCompressionLevelBindings(MethodHandle set, MethodHandle get) {
    }

    @Nullable
    static MethodHandle findNullLoggerBinding(SymbolLookup lookup) {
        return lookup.find("leveldb_options_set_null_logger")
                .map(address -> LINKER.downcallHandle(address, ofVoid(ADDRESS)))
                .orElse(null);
    }

    /**
     * Gets the major version of LevelDB.
     */
    public static int getMajorVersion() {
        try {
            return (int) leveldb_major_version.invokeExact();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get major version", e);
        }
    }

    /**
     * Gets the minor version of LevelDB.
     */
    public static int getMinorVersion() {
        try {
            return (int) leveldb_minor_version.invokeExact();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get minor version", e);
        }
    }

    private NativeBindings() {
        throw new IllegalStateException();
    }
}
