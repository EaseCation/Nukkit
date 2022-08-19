package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

import java.util.List;

/**
 * Called when a block explodes.
 */
public class BlockExplodeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final List<Block> blocks;
    protected double yield;
    protected boolean fire;

    public BlockExplodeEvent(Block source, List<Block> blocks, double yield, boolean fire) {
        super(source);
        this.blocks = blocks;
        this.yield = yield;
        this.fire = fire;
    }

    /**
     * Returns the list of blocks that would have been removed or were removed from the explosion event.
     *
     * @return all blown-up blocks
     */
    public List<Block> getBlockList() {
        return this.blocks;
    }

    /**
     * Returns the percentage of blocks to drop from this explosion.
     *
     * @return the yield
     */
    public double getYield() {
        return this.yield;
    }

    /**
     * Sets the percentage of blocks to drop from this explosion.
     *
     * @param yield the new yield percentage
     */
    public void setYield(double yield) {
        this.yield = yield;
    }

    public boolean isOnFire() {
        return this.fire;
    }

    public void setOnFire(boolean fire) {
        this.fire = fire;
    }
}
