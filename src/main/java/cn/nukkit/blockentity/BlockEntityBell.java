package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityBell extends BlockEntitySpawnable {

    protected int ticks;
    protected boolean ringing;
    protected byte direction;

    public BlockEntityBell(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.BELL;
    }

    @Override
    protected void initBlockEntity() {
        this.ticks = this.namedTag.getInt("Ticks");
        this.ringing = this.namedTag.getBoolean("Ringing");
        if (this.namedTag.contains("Direction")) {
            this.direction = (byte) this.namedTag.getInt("Direction");
        } else {
            this.direction = -1;
        }

        super.initBlockEntity();

        if (this.ringing) {
            this.scheduleUpdate();
        } else {
            this.ticks = 0;
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putInt("Ticks", this.ticks);
        this.namedTag.putBoolean("Ringing", this.ringing);
        this.namedTag.putInt("Direction", this.direction & 0xff);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.BELL;
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

        if (!this.ringing) {
            return false;
        }
        if (++this.ticks >= 50) {
            this.ringing = false;
            this.ticks = 0;
        }
        return this.ringing;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, BELL)
                .putInt("Ticks", this.ticks)
                .putBoolean("Ringing", this.ringing)
                .putInt("Direction", this.direction & 0xff);
    }

    public void ring(BlockFace direction) {
        if (!this.ringing) {
            this.scheduleUpdate();
        }

        this.ringing = true;
        this.direction = (byte) direction.getHorizontalIndex();
        this.spawnToAll();

        //TODO: redstone
    }
}
