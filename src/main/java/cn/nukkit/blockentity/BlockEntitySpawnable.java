package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BlockEntityDataPacket;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.ByteOrder;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockEntitySpawnable extends BlockEntity {

    public BlockEntitySpawnable(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();

        this.initSpawn();
    }

    protected void initSpawn() {
        this.spawnToAll();
    }

    public final CompoundTag getSpawnCompound() {
        return getSpawnCompound(false);
    }

    public CompoundTag getSpawnCompound(boolean chunkData) {
        return this.namedTag;
    }

    public void spawnTo(Player player) {
        spawnTo(player, false);
    }

    public void spawnTo(Player player, boolean empty) {
        if (this.isClosed()) {
            return;
        }

        BlockEntityDataPacket packet = this.getSpawnPacket(empty);
        if (packet == null) {
            return;
        }
        player.dataPacket(packet);
    }

    @Nullable
    public BlockEntityDataPacket getSpawnPacket() {
        return getSpawnPacket(null);
    }

    @Nullable
    public BlockEntityDataPacket getSpawnPacket(CompoundTag nbt) {
        if (nbt == null) {
            nbt = this.getSpawnCompound();
        }

        if (nbt.isEmpty()) {
            return null;
        }

        BlockEntityDataPacket pk = new BlockEntityDataPacket();
        pk.x = (int) this.x;
        pk.y = (int) this.y;
        pk.z = (int) this.z;
        try {
            pk.namedTag = NBTIO.write(nbt, ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return pk;
    }

    public BlockEntityDataPacket getSpawnPacket(boolean empty) {
        if (empty) {
            BlockEntityDataPacket pk = new BlockEntityDataPacket();
            pk.x = (int) this.x;
            pk.y = (int) this.y;
            pk.z = (int) this.z;
            pk.namedTag = CompoundTag.EMPTY;
            return pk;
        }

        return getSpawnPacket();
    }

    public void spawnToAll() {
        spawnToAll(false);
    }

    public void spawnToAll(boolean empty) {
        if (this.isClosed()) {
            return;
        }

        for (Player player : this.getLevel().getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values()) {
            if (player.spawned) {
                this.spawnTo(player, empty);
            }
        }
    }

    /**
     * Called when a player updates a block entity's NBT data
     * for example when writing on a sign.
     *
     * @param nbt tag
     * @param player player
     * @return bool indication of success, will respawn the tile to the player if false.
     */
    public boolean updateCompoundTag(CompoundTag nbt, Player player) {
        return false;
    }
}
