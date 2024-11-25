package cn.nukkit.network.protocol.types;

import cn.nukkit.network.protocol.UpdateBlockPacket;

public record BlockChangeEntry(
        int x,
        int y,
        int z,
        int block,
        int flags,
        // These two fields are useless 99.99% of the time; they are here to allow this packet to provide UpdateBlockSyncedPacket functionality.
        long syncedUpdateEntityUniqueId,
        int syncedUpdateType
) {
    public static BlockChangeEntry create(int x, int y, int z, int block) {
        return create(x, y, z, block, UpdateBlockPacket.FLAG_ALL_PRIORITY);
    }

    public static BlockChangeEntry create(int x, int y, int z, int block, int flags) {
        return create(x, y, z, block, flags,-1, 0);
    }

    public static BlockChangeEntry create(int x, int y, int z, int block, int flags, long syncedUpdateEntityUniqueId, int syncedUpdateType) {
        return new BlockChangeEntry(x, y, z, block, flags, syncedUpdateEntityUniqueId, syncedUpdateType);
    }
}
