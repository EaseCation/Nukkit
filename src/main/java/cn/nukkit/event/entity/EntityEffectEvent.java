package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.potion.Effect;

import javax.annotation.Nullable;
import java.util.Optional;

public class EntityEffectEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Action action;
    private final int effectID;
    @Nullable
    private final Effect oldEffect;
    @Nullable
    private final Effect newEffect;
    private boolean override;

    public EntityEffectEvent(Entity entity, Action action, int effectID, @Nullable Effect oldEffect, @Nullable Effect newEffect, boolean override) {
        this.entity = entity;
        this.action = action;
        this.effectID = effectID;
        this.oldEffect = oldEffect;
        this.newEffect = newEffect;
        this.override = override;
    }

    public Action getAction() {
        return action;
    }

    public int getEffectID() {
        return effectID;
    }

    public Optional<Effect> getOldEffect() {
        return Optional.ofNullable(oldEffect);
    }

    public Optional<Effect> getNewEffect() {
        return Optional.ofNullable(newEffect);
    }

    public boolean isOverride() {
        return override;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }

    public enum Action {
        ADDED,
        REMOVED,
        CHANGED
    }
}
