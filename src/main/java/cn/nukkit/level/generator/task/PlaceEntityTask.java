package cn.nukkit.level.generator.task;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.math.Mth;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.scheduler.Task;

import javax.annotation.Nullable;

public class PlaceEntityTask extends Task {
    protected final Level level;
    protected final CompoundTag nbt;

    public PlaceEntityTask(Level level, CompoundTag nbt) {
        this.level = level;
        this.nbt = nbt;
    }

    public PlaceEntityTask(FullChunk chunk, CompoundTag nbt) {
        this(getLevel(chunk), nbt);
    }

    @Override
    public void onRun(int currentTick) {
        ListTag<DoubleTag> pos = nbt.getList("Pos", DoubleTag.class);
        FullChunk chunk = level.getChunk(Mth.floor(pos.get(0).data) >> 4, Mth.floor(pos.get(2).data) >> 4, true);
        if (chunk == null || chunk.getProvider() == null) {
            return;
        }

        Entity entity = Entity.createEntity(nbt.getString("id"), chunk, nbt);
        if (entity != null) {
            entity.spawnToAll();
        }
    }

    @Nullable
    static Level getLevel(FullChunk chunk) {
        LevelProvider provider = chunk.getProvider();
        if (provider == null) {
            return null;
        }
        return provider.getLevel();
    }
}
