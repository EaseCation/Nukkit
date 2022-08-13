package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.MainLogger;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.lang.reflect.Constructor;

/**
 * @author MagicDroidX
 */
public abstract class BlockEntity extends Position implements BlockEntityID {

    public static long count = 1;

    private static final BiMap<String, Class<? extends BlockEntity>> knownBlockEntities = HashBiMap.create(BlockEntityType.UNDEFINED);

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
    protected Timing timing;

    public BlockEntity(FullChunk chunk, CompoundTag nbt) {
        if (chunk == null || chunk.getProvider() == null) {
            throw new ChunkException("Invalid garbage Chunk given to Block Entity");
        }

        this.timing = Timings.getBlockEntityTiming(this);
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

    public static BlockEntity createBlockEntity(String type, FullChunk chunk, CompoundTag nbt, Object... args) {
        type = type.replaceFirst("BlockEntity", ""); //TODO: Remove this after the first release
        BlockEntity blockEntity = null;

        if (knownBlockEntities.containsKey(type)) {
            Class<? extends BlockEntity> clazz = knownBlockEntities.get(type);

            if (clazz == null) {
                return null;
            }

            for (Constructor<?> constructor : clazz.getConstructors()) {
                if (blockEntity != null) {
                    break;
                }

                if (constructor.getParameterCount() != (args == null ? 2 : args.length + 2)) {
                    continue;
                }

                try {
                    if (args == null || args.length == 0) {
                        blockEntity = (BlockEntity) constructor.newInstance(chunk, nbt);
                    } else {
                        Object[] objects = new Object[args.length + 2];

                        objects[0] = chunk;
                        objects[1] = nbt;
                        System.arraycopy(args, 0, objects, 2, args.length);
                        blockEntity = (BlockEntity) constructor.newInstance(objects);

                    }
                } catch (Exception e) {
                    MainLogger.getLogger().logException(e);
                }

            }
        }

        return blockEntity;
    }

    public static boolean registerBlockEntity(String name, Class<? extends BlockEntity> c) {
        if (c == null) {
            return false;
        }

        knownBlockEntities.put(name, c);
        return true;
    }

    public final String getSaveId() {
        return knownBlockEntities.inverse().getOrDefault(getClass(), "");
    }

    public long getId() {
        return id;
    }

    public void saveNBT() {
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
        if (tag.getTags().size() > 0) {
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
