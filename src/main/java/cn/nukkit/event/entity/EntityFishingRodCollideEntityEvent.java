package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFishingHook;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class EntityFishingRodCollideEntityEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Entity collideEntity;

    public EntityFishingRodCollideEntityEvent(EntityFishingHook entity, Entity collideEntity) {
        this.entity = entity;
        this.collideEntity = collideEntity;
    }

    public EntityFishingHook getFishingHookEntity() {
        return (EntityFishingHook) super.getEntity();
    }

    public Entity getCollideEntity() {
        return collideEntity;
    }
}
