package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

import javax.annotation.Nullable;

public class BlockIgniteEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    @Nullable
    private final Block source;
    @Nullable
    private final Entity entity;
    private final BlockIgniteCause cause;

    public BlockIgniteEvent(Block block, @Nullable Block source, @Nullable Entity entity, BlockIgniteCause cause) {
        super(block);
        this.source = source;
        this.entity = entity;
        this.cause = cause;
    }

    @Nullable
    public Block getSource() {
        return source;
    }

    @Nullable
    public Entity getEntity() {
        return entity;
    }

    public BlockIgniteCause getCause() {
        return cause;
    }

    public enum BlockIgniteCause {
        EXPLOSION,
        FIREBALL,
        FLINT_AND_STEEL,
        LAVA,
        LIGHTNING,
        SPREAD
    }
}
