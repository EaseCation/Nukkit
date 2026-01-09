package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityCreakingHeart extends BlockEntity {
    private int cooldown;

    public BlockEntityCreakingHeart(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.CREAKING_HEART;
    }

    @Override
    protected void initBlockEntity() {
        cooldown = namedTag.getInt("Cooldown", 20);

        super.initBlockEntity();

//        scheduleUpdate();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt("Cooldown", cooldown);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.CREAKING_HEART;
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

        //TODO
        return true;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int ticks) {
        this.cooldown = ticks;
    }
}
