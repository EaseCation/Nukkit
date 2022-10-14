package cn.nukkit;

@SuppressWarnings("all")
public final class SharedConstants {

    public static final boolean PRODUCTION_ENVIRONMENT = false;

    // general

    public static final boolean BREAKPOINT_DEBUGGING = !PRODUCTION_ENVIRONMENT && true;

    public static final boolean ENABLE_TEST_COMMAND = !PRODUCTION_ENVIRONMENT && true;

    public static final boolean THREAD_SAFETY_CHECK = !PRODUCTION_ENVIRONMENT && false;

    public static final boolean NAN_INF_CHECK = !PRODUCTION_ENVIRONMENT && false;

    public static final boolean LOG_INVALID_BLOCK_AUX_ACCESS = !PRODUCTION_ENVIRONMENT && true;

    public static final boolean EVENT_CREATE_STACK_TRACE = !PRODUCTION_ENVIRONMENT && false;
    public static final boolean EVENT_CANCEL_STACK_TRACE = !PRODUCTION_ENVIRONMENT && false;

    // chunk

    /**
     * Use bitwise operations.
     */
    public static final boolean DISABLE_SUB_CHUNK_STORAGE_PADDING = true;
    public static final boolean ENABLE_SUB_CHUNK_NETWORK_OPTIMIZATION = true;
    public static final boolean ENABLE_EMPTY_SUB_CHUNK_NETWORK_OPTIMIZATION = true;

    public static final boolean DUMP_NETWORK_SUB_CHUNK = !PRODUCTION_ENVIRONMENT && false;

    // storage

    public static final boolean USE_NATIVE_LEVELDB = true;

    public static final boolean ENABLE_STORAGE_AUTO_COMPACTION = !USE_NATIVE_LEVELDB && false; //TODO: native db_iter

    private SharedConstants() {
        throw new IllegalStateException();
    }
}