package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.ChunkException;
import lombok.extern.log4j.Log4j2;

/**
 * @author MagicDroidX
 */
@Log4j2
public abstract class BlockEntity extends Position implements BlockEntityID {

    public static long count = 1;

    public FullChunk chunk;
    private String name;
    public final long id;
    private int repairCost;
    protected boolean movable;

    private boolean initialized;
    private boolean closed = false;
    public CompoundTag namedTag;

    protected int lastUpdate;
    protected Server server;

    public BlockEntity(FullChunk chunk, CompoundTag nbt) {
        if (chunk == null || chunk.getProvider() == null) {
            throw new ChunkException("Invalid garbage Chunk given to Block Entity");
        }

        this.server = chunk.getProvider().getLevel().getServer();
        this.chunk = chunk;
        this.setLevel(chunk.getProvider().getLevel());
        this.namedTag = nbt;
        this.lastUpdate = server.getTick();
        this.id = BlockEntity.count++;
        this.x = this.namedTag.getInt("x");
        this.y = this.namedTag.getInt("y");
        this.z = this.namedTag.getInt("z");

        if (namedTag.contains("isMovable")) {
            this.movable = this.namedTag.getBoolean("isMovable");
        } else {
            this.movable = true;
            namedTag.putBoolean("isMovable", true);
        }

        if (namedTag.contains("CustomName")) {
            this.name = this.namedTag.getString("CustomName");
        } else {
            this.name = "";
        }

        if (namedTag.contains("RepairCost")) {
            this.repairCost = this.namedTag.getInt("RepairCost");
        } else {
            this.repairCost = 0;
        }

        this.initBlockEntity();
        initialized = true;

        this.chunk.addBlockEntity(this);
        this.getLevel().addBlockEntity(this);
    }

    protected void initBlockEntity() {

    }

    public abstract int getTypeId();

    public final String getSaveId() {
        return BlockEntities.getIdByType(getTypeId());
    }

    public long getId() {
        return id;
    }

    public void saveNBT() {
        this.namedTag.setName(null);
        this.namedTag.putString("id", this.getSaveId());
        this.namedTag.putInt("x", (int) this.getX());
        this.namedTag.putInt("y", (int) this.getY());
        this.namedTag.putInt("z", (int) this.getZ());
        this.namedTag.putBoolean("isMovable", this.movable);

        if (!name.isEmpty()) {
            namedTag.putString("CustomName", name);
        } else {
            namedTag.remove("CustomName");
        }
        if (repairCost != 0) {
            namedTag.putInt("RepairCost", repairCost);
        } else {
            namedTag.remove("RepairCost");
        }
    }

    public CompoundTag getCleanedNBT() {
        this.saveNBT();
        CompoundTag tag = this.namedTag.clone();
        tag.remove("x").remove("y").remove("z").remove("id")
                .remove("isMovable")
                .remove("CustomName")
                .remove("RepairCost");
        if (!tag.isEmpty()) {
            return tag;
        } else {
            return null;
        }
    }

    public Block getBlock() {
        return this.getLevelBlock();
    }

    public abstract boolean isValidBlock(int blockId);

    public final boolean isBlockEntityValid() {
        return this.isValidBlock(this.getBlock().getId());
    }

    public boolean onUpdate() {
        return false;
    }

    public final void scheduleUpdate() {
        if (level == null) {
            return;
        }

        this.level.scheduleBlockEntityUpdate(this);
    }

    public void close() {
        if (!this.closed) {
            this.closed = true;
            if (this.chunk != null) {
                this.chunk.removeBlockEntity(this);
            }
            if (this.level != null) {
                this.level.removeBlockEntity(this);
            }
            this.level = null;
        }
    }

    public void onBreak() {

    }

    public final void setDirty() {
        if (level == null) {
            return;
        }

        chunk.setChanged();

        if (level.isInitialized() && !this.getLevelBlock().isAir()) {
            this.level.updateComparatorOutputLevel(this);
        }
    }

    /**
     * Get custom name.
     * @return custom name
     */
    public final String getName() {
        return name;
    }

    /**
     * Set custom name.
     * @param name custom name
     */
    public final void setName(String name) {
        this.name = name != null ? name : "";
    }

    /**
     * Has custom name.
     * @return has custom name
     */
    public final boolean hasName() {
        return !name.isEmpty();
    }

    public final int getRepairCost() {
        return repairCost;
    }

    public final void setRepairCost(int repairCost) {
        this.repairCost = Math.max(repairCost, 0);
    }

    public final boolean isMovable() {
        return movable;
    }

    public final void setMovable(boolean movable) {
        this.movable = movable;
    }

    public final boolean isClosed() {
        return closed;
    }

    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BlockEntity that = (BlockEntity) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    public static CompoundTag getDefaultCompound(Vector3 pos, String id) {
        return getDefaultCompound(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), id);
    }

    public static CompoundTag getDefaultCompound(BlockVector3 pos, String id) {
        return getDefaultCompound(pos.getX(), pos.getY(), pos.getZ(), id);
    }

    public static CompoundTag getDefaultCompound(int x, int y, int z, String id) {
        return new CompoundTag()
                .putString("id", id)
                .putInt("x", x)
                .putInt("y", y)
                .putInt("z", z);
    }
}
