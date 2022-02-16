package cn.nukkit;

public final class NukkitSharedConstants {

    public static final boolean ENABLE_COMMAND_PARAMETER_NAME_WARNING = false;

    //FIXME: broken in memory chunk cache
    public static final boolean ENABLE_SUB_CHUNK_NETWORK_OPTIMIZATION = false;

    private NukkitSharedConstants() {
        throw new IllegalStateException();
    }
}
