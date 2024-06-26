package cn.nukkit.potion;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityRegainHealthEvent;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class InstantEffect extends Effect {
    InstantEffect(int id, String identifier, String name, int r, int g, int b) {
        super(id, identifier, name, r, g, b);
    }

    InstantEffect(int id, String identifier, String name, int r, int g, int b, boolean isBad) {
        super(id, identifier, name, r, g, b, isBad);
    }

    @Override
    public boolean isInstantaneous() {
        return true;
    }

    @Override
    public boolean canTick() {
        return true;
    }

    @Override
    public void applyEffect(Entity entity) {
        switch (id) {
            case Effect.INSTANT_HEALTH:
                if (entity instanceof EntitySmite) {
                    entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, 6 << amplifier));
                } else {
                    entity.heal(new EntityRegainHealthEvent(entity, 4 << amplifier, EntityRegainHealthEvent.CAUSE_MAGIC));
                }
                break;
            case Effect.INSTANT_DAMAGE:
                if (entity instanceof EntitySmite) {
                    entity.heal(new EntityRegainHealthEvent(entity, 4 << amplifier, EntityRegainHealthEvent.CAUSE_MAGIC));
                } else {
                    entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, 6 << amplifier));
                }
                break;
            case Effect.SATURATION:
                if (entity instanceof Player player) {
                    int level = 1 + amplifier;
                    player.getFoodData().addFoodLevel(level, level * 2);
                }
                break;
        }
    }
}
