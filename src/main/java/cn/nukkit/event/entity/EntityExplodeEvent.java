package cn.nukkit.event.entity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;

import java.util.List;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class EntityExplodeEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final Position position;
    protected List<Block> blocks;
    protected double yield;
    protected boolean fire;

    public EntityExplodeEvent(Entity entity, Position position, List<Block> blocks, double yield) {
        this(entity, position, blocks, yield, false);
    }

    public EntityExplodeEvent(Entity entity, Position position, List<Block> blocks, double yield, boolean fire) {
        this.entity = entity;
        this.position = position;
        this.blocks = blocks;
        this.yield = yield;
        this.fire = fire;
    }

    public Position getPosition() {
        return this.position;
    }

    public List<Block> getBlockList() {
        return this.blocks;
    }

    public void setBlockList(List<Block> blocks) {
        this.blocks = blocks;
    }

    public double getYield() {
        return this.yield;
    }

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
