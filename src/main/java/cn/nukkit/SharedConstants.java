package cn.nukkit;

public final class SharedConstants {

    // general

    public static final boolean BREAKPOINT_DEBUGGING = true;

    public static final boolean ENABLE_TEST_COMMAND = true;

    public static final boolean THREAD_SAFETY_CHECK = false;

    public static final boolean NAN_INF_CHECK = false;

    public static final boolean EVENT_CREATE_STACK_TRACE = false;
    public static final boolean EVENT_CANCEL_STACK_TRACE = false;

    // chunk

    /**
     * Use bitwise operations.
     */
    public static final boolean DISABLE_SUB_CHUNK_STORAGE_PADDING = true;
    public static final boolean ENABLE_SUB_CHUNK_NETWORK_OPTIMIZATION = true;
    public static final boolean ENABLE_EMPTY_SUB_CHUNK_NETWORK_OPTIMIZATION = true;

    public static final boolean DUMP_NETWORK_SUB_CHUNK = false;

    private SharedConstants() {
        throw new IllegalStateException();
    }
}
