package cn.nukkit.blockentity;

import cn.nukkit.event.block.ModBlockEntityTickEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Builder;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class BlockEntityModBlock extends BlockEntitySpawnable {
    protected final ModBlockEntityDefinition definition;
    protected CompoundTag data;

    private final ModBlockEntityTickEvent tickEvent;

    public BlockEntityModBlock(FullChunk chunk, CompoundTag nbt, ModBlockEntityDefinition definition) {
        super(chunk, nbt);
        this.definition = definition;

        movable = definition.movable;

        if (definition.serverTick != null) {
            tickEvent = new ModBlockEntityTickEvent(this);
            scheduleUpdate();
        } else {
            tickEvent = null;
        }

        spawnToAll();
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();

        if (namedTag.contains("exData")) {
            data = namedTag.getCompound("exData");
        } else {
            data = new CompoundTag();
            namedTag.putCompound("exData", data);
        }
    }

    @Override
    protected void initSpawn() {
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        namedTag.putLong("_uniqueId", ThreadLocalRandom.current().nextLong());
        namedTag.putString("_blockName", definition.blockName);
        namedTag.putBoolean("_tick", definition.serverTick != null);
        namedTag.putBoolean("_tickClient", definition.clientTick);
        namedTag.putBoolean("_movable", definition.movable);
        namedTag.putCompound("exData", data);
    }

    @Override
    public CompoundTag getSpawnCompound(boolean chunkData) {
        return getDefaultCompound(this, MOD_BLOCK)
                .putLong("_uniqueId", -id)
                .putString("_blockName", definition.blockName)
                .putBoolean("_tick", definition.serverTick != null)
                .putBoolean("_tickClient", definition.clientTick)
                .putBoolean("_movable", definition.movable)
                .putBoolean("isMovable", movable)
                .putCompound("exData", data.clone());
    }

    @Override
    public boolean onUpdate() {
        if (isClosed()) {
            return false;
        }

        int currentTick = server.getTick();
        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0) {
            return true;
        }
        lastUpdate = currentTick;

        Consumer<BlockEntityModBlock> serverTick = definition.serverTick;
        if (serverTick == null) {
            return false;
        }
        serverTick.accept(this);
        return true;
    }

    @Override
    public int getTypeId() {
        return definition.blockEntityType;
    }

    @Override
    public String getSaveId() {
        return MOD_BLOCK;
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == definition.blockId;
    }

    public CompoundTag getData() {
        return data;
    }

    public int getBlockId() {
        return definition.blockId;
    }

    public String getBlockName() {
        return definition.blockName;
    }

    void tickEvent() {
        ModBlockEntityTickEvent tickEvent = this.tickEvent;
        if (tickEvent != null) {
            tickEvent.call();
        }
    }

    @Builder
    public record ModBlockEntityDefinition(
            int blockEntityType,
            int blockId,
            String blockName,
            boolean movable,
            @Nullable
            Consumer<BlockEntityModBlock> serverTick,
            boolean clientTick
    ) {
    }
}
