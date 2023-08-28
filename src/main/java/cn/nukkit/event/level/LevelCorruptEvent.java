package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.ChunkPosition;
import cn.nukkit.level.Level;

import javax.annotation.Nullable;

public class LevelCorruptEvent extends LevelEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    @Nullable
    private final ChunkPosition chunkPos;

    public LevelCorruptEvent(Level level) {
        this(level, null);
    }

    public LevelCorruptEvent(Level level, @Nullable ChunkPosition chunkPos) {
        super(level);
        this.chunkPos = chunkPos;
    }

    @Nullable
    public ChunkPosition getChunkPos() {
        return chunkPos;
    }
}
