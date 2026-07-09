package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.knockback.KnockbackSourceType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityDamageByChildEntityEvent extends EntityDamageByEntityEvent {

    private final Entity childEntity;

    public EntityDamageByChildEntityEvent(Entity damager, Entity childEntity, Entity entity, DamageCause cause, float damage) {
        super(damager, entity, cause, damage);
        this.childEntity = childEntity;
    }

    public EntityDamageByChildEntityEvent(Entity damager, Entity childEntity, Entity entity, DamageCause cause, float damage, KnockbackSourceType sourceType) {
        super(damager, entity, cause, damage, sourceType);
        this.childEntity = childEntity;
    }

    public Entity getChild() {
        return childEntity;
    }
}
