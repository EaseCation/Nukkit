package cn.nukkit.potion;

import cn.nukkit.Player;
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
public class Potion implements PotionID, Cloneable {

    protected static Potion[] potions;

    public static void init() {
        potions = new Potion[256];

        Potions.registerVanillaPotions();
    }

    @Nullable
    public static Potion getPotion(int id) {
        if (id < 0 || id >= potions.length) {
            return null;
        }
        Potion potion = potions[id];
        if (potion == null) {
            return null;
        }
        return potion.clone();
    }

    protected final int id;

    protected final int level;

    protected boolean splash;

    Potion(int id) {
        this(id, 1);
    }

    Potion(int id, int level) {
        this(id, level, false);
    }

    Potion(int id, int level, boolean splash) {
        this.id = id;
        this.level = level;
        this.splash = splash;
    }

    public Effect getEffect() {
        return getEffect(this.getId(), this.isSplash());
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public boolean isSplash() {
        return splash;
    }

    public Potion setSplash(boolean splash) {
        this.splash = splash;
        return this;
    }

    public void applyPotion(Entity entity) {
        applyPotion(entity, Item.get(Item.POTION, getId()));
    }

    public void applyPotion(Entity entity, Item potionItem) {
        applyPotion(entity, potionItem, 0.5);
    }

    public void applyPotion(Entity entity, double health) {
        applyPotion(entity, Item.get(Item.POTION, getId()), health);
    }

    public void applyPotion(Entity entity, Item potionItem, double health) {
        if (!(entity instanceof EntityLiving)) {
            return;
        }

        Effect applyEffect = getEffect(this.getId(), this.isSplash());

        if (applyEffect == null) {
            return;
        }

        if (entity instanceof Player) {
            if (!((Player) entity).isSurvival() && !((Player) entity).isAdventure() && applyEffect.isBad()) {
                return;
            }
        }

        if (isSplash()) {
            int duration = (int) (applyEffect.getDuration() * health + 0.5);
            applyEffect.setDuration(duration);
        }

        PotionApplyEvent event = new PotionApplyEvent(this, potionItem, applyEffect, entity);

        entity.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        applyEffect = event.getApplyEffect();

        switch (this.getId()) {
            case INSTANT_HEALTH:
            case INSTANT_HEALTH_II:
                if (entity instanceof EntitySmite) {
                    entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, (float) (health * (double) (6 << (applyEffect.getAmplifier() + 1)))));
                } else {
                    entity.heal(new EntityRegainHealthEvent(entity, (float) (health * (double) (4 << (applyEffect.getAmplifier() + 1))), EntityRegainHealthEvent.CAUSE_MAGIC));
                }
                break;
            case HARMING:
            case HARMING_II:
                if (entity instanceof EntitySmite) {
                    entity.heal(new EntityRegainHealthEvent(entity, (float) (health * (double) (4 << (applyEffect.getAmplifier() + 1))), EntityRegainHealthEvent.CAUSE_MAGIC));
                } else {
                    entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, (float) (health * (double) (6 << (applyEffect.getAmplifier() + 1)))));
                }
                break;
            default:
                entity.addEffect(applyEffect);
        }
    }

    @Override
    public Potion clone() {
        try {
            return (Potion) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public static Effect getEffect(int potionType, boolean isSplash) {
        Effect effect;
        switch (potionType) {
            case WATER:
            case MUNDANE:
            case MUNDANE_II:
            case THICK:
            case AWKWARD:
                return null;
            case NIGHT_VISION:
            case NIGHT_VISION_LONG:
                effect = Effect.getEffect(Effect.NIGHT_VISION);
                break;
            case INVISIBLE:
            case INVISIBLE_LONG:
                effect = Effect.getEffect(Effect.INVISIBILITY);
                break;
            case LEAPING:
            case LEAPING_LONG:
            case LEAPING_II:
                effect = Effect.getEffect(Effect.JUMP_BOOST);
                break;
            case FIRE_RESISTANCE:
            case FIRE_RESISTANCE_LONG:
                effect = Effect.getEffect(Effect.FIRE_RESISTANCE);
                break;
            case SPEED:
            case SPEED_LONG:
            case SPEED_II:
                effect = Effect.getEffect(Effect.SPEED);
                break;
            case SLOWNESS:
            case SLOWNESS_LONG:
            case SLOWNESS_IV:
                effect = Effect.getEffect(Effect.SLOWNESS);
                break;
            case WATER_BREATHING:
            case WATER_BREATHING_LONG:
                effect = Effect.getEffect(Effect.WATER_BREATHING);
                break;
            case INSTANT_HEALTH:
            case INSTANT_HEALTH_II:
                return Effect.getEffect(Effect.INSTANT_HEALTH);
            case HARMING:
            case HARMING_II:
                return Effect.getEffect(Effect.INSTANT_DAMAGE);
            case POISON:
            case POISON_LONG:
            case POISON_II:
                effect = Effect.getEffect(Effect.POISON);
                break;
            case REGENERATION:
            case REGENERATION_LONG:
            case REGENERATION_II:
                effect = Effect.getEffect(Effect.REGENERATION);
                break;
            case STRENGTH:
            case STRENGTH_LONG:
            case STRENGTH_II:
                effect = Effect.getEffect(Effect.STRENGTH);
                break;
            case WEAKNESS:
            case WEAKNESS_LONG:
                effect = Effect.getEffect(Effect.WEAKNESS);
                break;
            case WITHER_II:
                effect = Effect.getEffect(Effect.WITHER);
                break;
            default:
                return null;
        }

        if (getLevel(potionType) > 1) {
            effect.setAmplifier(1);
        }

        if (!isInstant(potionType)) {
            effect.setDuration(20 * getApplySeconds(potionType, isSplash));
        }

        return effect;
    }

    public static int getLevel(int potionType) {
        switch (potionType) {
            case MUNDANE_II:
            case LEAPING_II:
            case SPEED_II:
            case INSTANT_HEALTH_II:
            case HARMING_II:
            case POISON_II:
            case REGENERATION_II:
            case STRENGTH_II:
            case WITHER_II:
            case TURTLE_MASTER_II:
                return 2;
            case SLOWNESS_IV:
                return 4;
            default:
                return 1;
        }
    }

    public static boolean isInstant(int potionType) {
        switch (potionType) {
            case INSTANT_HEALTH:
            case INSTANT_HEALTH_II:
            case HARMING:
            case HARMING_II:
                return true;
            default:
                return false;
        }
    }

    public static int getApplySeconds(int potionType, boolean isSplash) {
        if (isSplash) {
            switch (potionType) {
                case WATER:
                    return 0;
                case MUNDANE:
                    return 0;
                case MUNDANE_II:
                    return 0;
                case THICK:
                    return 0;
                case AWKWARD:
                    return 0;
                case NIGHT_VISION:
                    return 135;
                case NIGHT_VISION_LONG:
                    return 360;
                case INVISIBLE:
                    return 135;
                case INVISIBLE_LONG:
                    return 360;
                case LEAPING:
                    return 135;
                case LEAPING_LONG:
                    return 360;
                case LEAPING_II:
                    return 67;
                case FIRE_RESISTANCE:
                    return 135;
                case FIRE_RESISTANCE_LONG:
                    return 360;
                case SPEED:
                    return 135;
                case SPEED_LONG:
                    return 360;
                case SPEED_II:
                    return 67;
                case SLOWNESS:
                    return 67;
                case SLOWNESS_LONG:
                    return 180;
                case WATER_BREATHING:
                    return 135;
                case WATER_BREATHING_LONG:
                    return 360;
                case INSTANT_HEALTH:
                    return 0;
                case INSTANT_HEALTH_II:
                    return 0;
                case HARMING:
                    return 0;
                case HARMING_II:
                    return 0;
                case POISON:
                    return 33;
                case POISON_LONG:
                    return 90;
                case POISON_II:
                    return 16;
                case REGENERATION:
                    return 33;
                case REGENERATION_LONG:
                    return 90;
                case REGENERATION_II:
                    return 16;
                case STRENGTH:
                    return 135;
                case STRENGTH_LONG:
                    return 360;
                case STRENGTH_II:
                    return 67;
                case WEAKNESS:
                    return 67;
                case WEAKNESS_LONG:
                    return 180;
                case WITHER_II:
                    return 30;
                case SLOWNESS_IV:
                    return 15;
                default:
                    return 0;
            }
        } else {
            switch (potionType) {
                case WATER:
                    return 0;
                case MUNDANE:
                    return 0;
                case MUNDANE_II:
                    return 0;
                case THICK:
                    return 0;
                case AWKWARD:
                    return 0;
                case NIGHT_VISION:
                    return 180;
                case NIGHT_VISION_LONG:
                    return 480;
                case INVISIBLE:
                    return 180;
                case INVISIBLE_LONG:
                    return 480;
                case LEAPING:
                    return 180;
                case LEAPING_LONG:
                    return 480;
                case LEAPING_II:
                    return 90;
                case FIRE_RESISTANCE:
                    return 180;
                case FIRE_RESISTANCE_LONG:
                    return 480;
                case SPEED:
                    return 180;
                case SPEED_LONG:
                    return 480;
                case SPEED_II:
                    return 480;
                case SLOWNESS:
                    return 90;
                case SLOWNESS_LONG:
                    return 240;
                case WATER_BREATHING:
                    return 180;
                case WATER_BREATHING_LONG:
                    return 480;
                case INSTANT_HEALTH:
                    return 0;
                case INSTANT_HEALTH_II:
                    return 0;
                case HARMING:
                    return 0;
                case HARMING_II:
                    return 0;
                case POISON:
                    return 45;
                case POISON_LONG:
                    return 120;
                case POISON_II:
                    return 22;
                case REGENERATION:
                    return 45;
                case REGENERATION_LONG:
                    return 120;
                case REGENERATION_II:
                    return 22;
                case STRENGTH:
                    return 180;
                case STRENGTH_LONG:
                    return 480;
                case STRENGTH_II:
                    return 90;
                case WEAKNESS:
                    return 90;
                case WEAKNESS_LONG:
                    return 240;
                case WITHER_II:
                    return 30;
                case SLOWNESS_IV:
                    return 20;
                default:
                    return 0;
            }
        }
    }
}
