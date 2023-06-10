package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityConduit extends BlockEntitySpawnable {

    protected boolean active;

    protected Entity target;

    protected long tickCount = 0;

    public BlockEntityConduit(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        this.active = this.namedTag.getBoolean("Active");

        if (this.namedTag.contains("Target")) {
            long id = this.namedTag.getLong("Target");
            if (id != -1) {
                this.target = this.level.getEntity(id);
            } else {
                this.target = null;
            }
        } else {
            this.target = null;
        }

        super.initBlockEntity();

//        this.scheduleUpdate();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putBoolean("Active", this.active);
        this.namedTag.putLong("Target", this.target != null ? this.target.getId() : -1);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.CONDUIT;
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

        if (!this.active || tickCount++ % 40 != 0) {
            return true;
        }

        //TODO
        return true;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, CONDUIT)
                .putBoolean("Active", this.active)
                .putLong("Target", this.target != null ? this.target.getId() : -1);
    }
}
