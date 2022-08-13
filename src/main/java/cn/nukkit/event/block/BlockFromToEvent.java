package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class BlockFromToEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final int layer;
    private Block to;

    public BlockFromToEvent(Block block, int layer, Block to) {
        super(block);
        this.layer = layer;
        this.to = to;
    }

    public Block getFrom() {
        return getBlock();
    }

    public Block getTo() {
        return to;
    }

    public void setTo(Block newTo) {
        to = newTo;
    }

    public int getLayer() {
        return layer;
    }
}