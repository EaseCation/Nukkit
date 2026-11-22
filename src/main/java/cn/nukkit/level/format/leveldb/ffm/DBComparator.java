package cn.nukkit.level.format.leveldb.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static cn.nukkit.level.format.leveldb.ffm.NativeBindings.*;
import static java.lang.foreign.Arena.*;
import static java.lang.foreign.FunctionDescriptor.*;
import static java.lang.foreign.MemorySegment.*;
import static java.lang.foreign.ValueLayout.*;
import static java.lang.invoke.MethodType.*;
import static java.util.Objects.*;

/**
 * A Comparator provides a total order across slices used as keys in a LevelDB table.
 * <p>
 * The comparator must remain alive while the database is open, and must not be closed before the database.
 */
public final class DBComparator implements AutoCloseable {

    private static final FunctionDescriptor DESTRUCTOR_CALLBACK_DESCRIPTOR = ofVoid(ADDRESS);
    private static final MethodHandle DESTRUCTOR_CALLBACK_HANDLE = findCallbackHandle("destructorCallback",
            methodType(void.class, MemorySegment.class));

    private static final FunctionDescriptor COMPARE_CALLBACK_DESCRIPTOR = of(JAVA_INT, ADDRESS, ADDRESS, JAVA_LONG, ADDRESS, JAVA_LONG);
    private static final MethodHandle COMPARE_CALLBACK_HANDLE = findCallbackHandle("compareCallback",
            methodType(int.class, SegmentCompareFunction.class, MemorySegment.class, MemorySegment.class, long.class, MemorySegment.class, long.class));

    private static final FunctionDescriptor NAME_CALLBACK_DESCRIPTOR = of(ADDRESS, ADDRESS);
    private static final MethodHandle NAME_CALLBACK_HANDLE = findCallbackHandle("nameCallback",
            methodType(MemorySegment.class, MemorySegment.class, MemorySegment.class));

    private final Arena arena;
    private final MemorySegment handle;
    private boolean closed;

    /**
     * Creates a new comparator with the given name and comparison function.
     *
     * @param name    the name of the comparator (used for sanity checks)
     * @param compare the comparison function
     */
    public DBComparator(String name, CompareFunction compare) {
        this(name, adapt(compare));
    }

    /**
     * Creates a comparator whose callback reads directly from native key segments.
     * The read-only segments are only valid for the duration of the callback and must not be retained.
     *
     * @param name    the name of the comparator (used for sanity checks)
     * @param compare the zero-copy comparison function
     * @return a new comparator
     */
    public static DBComparator fromMemorySegments(String name, SegmentCompareFunction compare) {
        return new DBComparator(name, requireNonNull(compare, "compare"));
    }

    private DBComparator(String name, SegmentCompareFunction compare) {
        this.arena = ofShared();
        try {
            this.handle = (MemorySegment) leveldb_comparator_create.invokeExact(NULL,
                    LINKER.upcallStub(DESTRUCTOR_CALLBACK_HANDLE, DESTRUCTOR_CALLBACK_DESCRIPTOR, arena),
                    LINKER.upcallStub(COMPARE_CALLBACK_HANDLE.bindTo(compare), COMPARE_CALLBACK_DESCRIPTOR, arena),
                    LINKER.upcallStub(NAME_CALLBACK_HANDLE.bindTo(arena.allocateFrom(name)), NAME_CALLBACK_DESCRIPTOR, arena));
        } catch (Throwable e) {
            arena.close();
            throw new DBException("Failed to create comparator", e);
        }
    }

    private static MethodHandle findCallbackHandle(String name, MethodType type) {
        try {
            return MethodHandles.lookup().findStatic(DBComparator.class, name, type);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static SegmentCompareFunction adapt(CompareFunction compare) {
        requireNonNull(compare, "compare");
        return (a, b) -> compare.compare(a.toArray(JAVA_BYTE), b.toArray(JAVA_BYTE));
    }

    @SuppressWarnings("unused")
    private static void destructorCallback(MemorySegment state) {
        // NOOP
    }

    @SuppressWarnings("unused")
    private static int compareCallback(SegmentCompareFunction compare, MemorySegment state, MemorySegment a, long aLen, MemorySegment b, long bLen) {
        return compare.compare(a.reinterpret(aLen).asReadOnly(), b.reinterpret(bLen).asReadOnly());
    }

    @SuppressWarnings("unused")
    private static MemorySegment nameCallback(MemorySegment nameSegment, MemorySegment state) {
        return nameSegment;
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
            throw new IllegalStateException("Comparator has been closed");
        }
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        try {
            leveldb_comparator_destroy.invokeExact(handle);
        } catch (Throwable e) {
            throw new DBException("Failed to destroy comparator", e);
        } finally {
            arena.close();
        }
    }

    /**
     * Functional interface for comparing two byte arrays.
     * Returns negative, zero, or positive if a is less than, equal to, or greater than b.
     */
    @FunctionalInterface
    public interface CompareFunction {
        int compare(byte[] a, byte[] b);
    }

    /**
     * Compares two temporary read-only native key segments without copying them to Java arrays.
     */
    @FunctionalInterface
    public interface SegmentCompareFunction {
        int compare(MemorySegment a, MemorySegment b);
    }
}
