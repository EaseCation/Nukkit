package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.format.FullChunk;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkLoadExceptionEvent extends ChunkEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Exception exception;

    public ChunkLoadExceptionEvent(FullChunk chunk, Exception exception) {
        super(chunk);
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
}