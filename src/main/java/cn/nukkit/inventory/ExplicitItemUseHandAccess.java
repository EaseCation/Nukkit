package cn.nukkit.inventory;

/**
 * Provides the runtime capability for explicit main-hand/offhand item use.
 */
public interface ExplicitItemUseHandAccess {

    default boolean isExplicitItemUseHandClient() {
        return false;
    }

    boolean isExplicitItemUseHandAllowed();
}
