package cn.nukkit.block;

public interface ClipFlag {
    int NONE = 0;
    /**
     * clamp to AABB(x, y, z, x + 1, x + 1, x + 1)
     */
    int CLAMP = 1 << 0;
    /**
     * include liquids
     */
    int LIQUID = 1 << 1;
    /**
     * exclude barrier
     */
    int IGNORE_BARRIER = 1 << 2;

    static boolean has(int flags, int flag) {
        return (flags & flag) != 0;
    }
}
