package cn.nukkit.level.generator.task;

import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.Task;

import static cn.nukkit.level.generator.task.PlaceEntityTask.getLevel;

public class PlaceBlockEntityTask extends Task {
    protected final Level level;
    protected final CompoundTag nbt;

    public PlaceBlockEntityTask(Level level, CompoundTag nbt) {
        this.level = level;
        this.nbt = nbt;
    }

    public PlaceBlockEntityTask(FullChunk chunk, CompoundTag nbt) {
        this(getLevel(chunk), nbt);
    }

    @Override
    public void onRun(int currentTick) {
        FullChunk chunk = level.getChunk(nbt.getInt("x") >> 4, nbt.getInt("z") >> 4, true);
        if (chunk == null || chunk.getProvider() == null) {
            return;
        }
        BlockEntities.createBlockEntity(nbt.getString("id"), chunk, nbt);
    }
}
