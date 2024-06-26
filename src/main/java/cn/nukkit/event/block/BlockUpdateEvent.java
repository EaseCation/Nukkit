package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockUpdateEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final int layer;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public BlockUpdateEvent(Block block, int layer) {
        super(block);
        this.layer = layer;
    }

    public int getLayer() {
        return layer;
    }
}
