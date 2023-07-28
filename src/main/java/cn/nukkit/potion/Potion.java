package cn.nukkit.potion;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.potion.PotionApplyEvent;
import cn.nukkit.item.Item;

import javax.annotation.Nullable;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Potion implements PotionID {

    static final Potion[] potions = new Potion[256];

    public static void init() {
        Potions.registerVanillaPotions();
    }

    @Nullable
    public static Potion getPotion(int id) {
        if (id < 0 || id >= potions.length) {
            return null;
        }
        return potions[id];
    }

    private final int id;
    private final String identifier;
    private final String name;
    private final Effect[] effects;

    Potion(int id, String identifier, String name, Effect... effects) {
        this.id = id;
        this.identifier = identifier;
        this.name = name;
        this.effects = effects;
    }

    public int getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public Effect[] getEffects() {
        return effects;
    }

    public void applyPotion(Entity entity) {
        applyPotion(entity, Item.get(Item.POTION, getId()));
    }

    public void applyPotion(Entity entity, Item potionItem) {
        applyPotion(entity, potionItem, 1, 1);
    }

    public void applyPotion(Entity entity, float durationScale, float instantScale) {
        applyPotion(entity, Item.get(Item.POTION, getId()), durationScale, instantScale);
    }

    public void applyPotion(Entity entity, Item potionItem, float durationScale, float instantScale) {
        if (effects[0].getId() == EffectID.NO_EFFECT) {
            return;
        }

        if (!(entity instanceof EntityLiving)) {
            return;
        }

        Effect[] applyEffects = new Effect[effects.length];
        for (int i = 0; i < effects.length; i++) {
            Effect effect = effects[i].clone();
            if (!effect.isInstantaneous()) {
                effect.setDuration((int) (durationScale * effect.getDuration() + 0.5f));
            }
            applyEffects[i] = effect;
        }

        PotionApplyEvent event = new PotionApplyEvent(this, potionItem, applyEffects, entity);
        entity.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        applyEffects = event.getApplyEffects();

        for (Effect effect : applyEffects) {
            switch (effect.getId()) {
                case Effect.INSTANT_HEALTH:
                    if (entity instanceof EntitySmite) {
                        entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, instantScale * (6 << effect.getAmplifier())));
                    } else {
                        entity.heal(new EntityRegainHealthEvent(entity, instantScale * (4 << effect.getAmplifier()), EntityRegainHealthEvent.CAUSE_MAGIC));
                    }
                    break;
                case Effect.INSTANT_DAMAGE:
                    if (entity instanceof EntitySmite) {
                        entity.heal(new EntityRegainHealthEvent(entity, instantScale * (4 << effect.getAmplifier()), EntityRegainHealthEvent.CAUSE_MAGIC));
                    } else {
                        entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, instantScale * (6 << effect.getAmplifier())));
                    }
                    break;
                default:
                    entity.addEffect(effect);
                    break;
            }
        }
    }

    public boolean isValidPotion() {
        return isValidPotion(id);
    }

    public static boolean isValidPotion(int potionId) {
        return potionId > AWKWARD;
    }
}
