package cn.nukkit.potion;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.attribute.AttributeModifier;
import cn.nukkit.entity.attribute.AttributeModifiers;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityEffectEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.MobEffectPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;

import javax.annotation.Nullable;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Effect implements EffectID, Cloneable {

    static final Effect[] effects = new Effect[256];

    public static void init() {
        Effects.registerVanillaEffects();
    }

    @Nullable
    public static Effect getEffect(int id) {
        if (id < 0 || id >= effects.length) {
            return null;
        }
        Effect effect = effects[id];
        if (effect == null) {
            return null;
        }
        return effect.clone();
    }

    protected final int id;
    protected final String identifier;
    protected final String name;

    protected int duration;
    protected int amplifier;

    protected int color;

    protected boolean show = true;

    protected boolean ambient;

    protected final boolean bad;

    protected final Int2ObjectMap<AttributeModifier> attributeModifiers;

    Effect(int id, String identifier, String name, int r, int g, int b) {
        this(id, identifier, name, r, g, b, false);
    }

    Effect(int id, String identifier, String name, int r, int g, int b, Int2ObjectMap<AttributeModifier> attributeModifiers) {
        this(id, identifier, name, r, g, b, false, attributeModifiers);
    }

    Effect(int id, String identifier, String name, int r, int g, int b, boolean isBad) {
        this(id, identifier, name, r, g, b, isBad, Int2ObjectMaps.emptyMap());
    }

    Effect(int id, String identifier, String name, int r, int g, int b, boolean isBad, Int2ObjectMap<AttributeModifier> attributeModifiers) {
        this.id = id;
        this.identifier = identifier;
        this.name = name;
        this.bad = isBad;
        this.attributeModifiers = attributeModifiers;
        this.setColor(r, g, b);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Effect setDuration(int ticks) {
        this.duration = ticks;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isInfinite() {
        return duration < 0;
    }

    public boolean isVisible() {
        return show;
    }

    public Effect setVisible(boolean visible) {
        this.show = visible;
        return this;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public Effect setAmplifier(int amplifier) {
        this.amplifier = amplifier;
        return this;
    }

    public boolean isAmbient() {
        return ambient;
    }

    public Effect setAmbient(boolean ambient) {
        this.ambient = ambient;
        return this;
    }

    public boolean isBad() {
        return bad;
    }

    public boolean isInstantaneous() {
        return false;
    }

    public boolean canTick() {
        int interval;
        switch (this.id) {
            case Effect.POISON:
            case Effect.FATAL_POISON:
                if ((interval = (25 >> this.amplifier)) > 0) {
                    return (this.duration % interval) == 0;
                }
                return true;
            case Effect.WITHER:
                if ((interval = (40 >> this.amplifier)) > 0) {
                    return (this.duration % interval) == 0;
                }
                return true;
            case Effect.REGENERATION:
                if ((interval = (50 >> this.amplifier)) > 0) {
                    return (this.duration % interval) == 0;
                }
                return true;
        }
        return false;
    }

    public void applyEffect(Entity entity) {
        switch (this.id) {
            case Effect.POISON:
            case Effect.FATAL_POISON:
                if (entity.getHealth() > 1 || this.id == FATAL_POISON) {
                    entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, 1));
                }
                break;
            case Effect.WITHER:
                entity.attack(new EntityDamageEvent(entity, DamageCause.WITHER, 1));
                break;
            case Effect.REGENERATION:
                if (entity.getHealth() < entity.getMaxHealth()) {
                    entity.heal(new EntityRegainHealthEvent(entity, 1, EntityRegainHealthEvent.CAUSE_MAGIC));
                }
                break;
        }
    }

    public int getColor() {
        return this.color;
    }

    public int getRed() {
        return this.color >> 16;
    }

    public int getGreen() {
        return (this.color >> 8) & 0xff;
    }

    public int getBlue() {
        return this.color & 0xff;
    }

    public void setColor(int rgb) {
        this.color = rgb & 0xffffff;
    }

    public void setColor(int r, int g, int b) {
        this.color = ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
    }

    public boolean add(Entity entity) {
        Effect oldEffect = entity.getEffect(getId());
        boolean override = false;
        EntityEffectEvent.Action action = EntityEffectEvent.Action.ADDED;
        if (oldEffect != null) {
            int newAmplifier = Math.abs(this.getAmplifier());
            int oldAmplifier = Math.abs(oldEffect.getAmplifier());
            override = (newAmplifier > oldAmplifier) || (newAmplifier == oldAmplifier && !oldEffect.isInfinite() && (this.getDuration() >= oldEffect.getDuration() || this.isInfinite()));
            action = EntityEffectEvent.Action.CHANGED;
        }

        EntityEffectEvent event = new EntityEffectEvent(entity, action, getId(), oldEffect, this, override);
        event.call();
        if (event.isCancelled()) return false;
        override = event.isOverride();
        if (oldEffect != null && !override) return false;

        if (entity instanceof Player) {
            Player player = (Player) entity;

            MobEffectPacket pk = new MobEffectPacket();
            pk.eid = entity.getId();
            pk.effectId = this.getId();
            pk.amplifier = this.getAmplifier();
            pk.particles = this.isVisible();
            pk.duration = this.isInfinite() ? -1 : this.getDuration();
            if (oldEffect != null) {
                pk.eventId = MobEffectPacket.EVENT_MODIFY;
            } else {
                pk.eventId = MobEffectPacket.EVENT_ADD;
            }
            player.dataPacket(pk);

            if (this.id == Effect.SPEED) {
                attributeModifiers.forEach((attributeId, modifier) -> player.getMovementSpeedAttribute().replaceModifier(createAttributeModifier(modifier)));
                return true;
            }

            if (this.id == Effect.SLOWNESS) {
                attributeModifiers.forEach((attributeId, modifier) -> player.getMovementSpeedAttribute().replaceModifier(createAttributeModifier(modifier)));
                return true;
            }
        }

        if (this.id == Effect.INVISIBILITY) {
            entity.setDataFlag(Entity.DATA_FLAG_INVISIBLE, true);
            entity.setNameTagVisible(false);
            return true;
        }

        if (this.id == Effect.ABSORPTION) {
            int add = (this.amplifier + 1) * 4;
            entity.setAbsorption(entity.getAbsorption() + add);
            return true;
        }

        return true;
    }

    public boolean remove(Entity entity) {
        EntityEffectEvent event = new EntityEffectEvent(entity, EntityEffectEvent.Action.REMOVED, getId(), this, null, false);
        event.call();
        if (event.isCancelled()) return false;

        if (entity instanceof Player) {
            Player player = (Player) entity;

            MobEffectPacket pk = new MobEffectPacket();
            pk.eid = entity.getId();
            pk.effectId = this.getId();
            pk.eventId = MobEffectPacket.EVENT_REMOVE;
            player.dataPacket(pk);

            if (this.id == Effect.SPEED) {
                player.getMovementSpeedAttribute().removeModifier(AttributeModifiers.MOVEMENT_SPEED.getId());
                return true;
            }

            if (this.id == Effect.SLOWNESS) {
                player.getMovementSpeedAttribute().removeModifier(AttributeModifiers.MOVEMENT_SLOWDOWN.getId());
                return true;
            }
        }

        if (this.id == Effect.INVISIBILITY) {
            entity.setDataFlag(Entity.DATA_FLAG_INVISIBLE, false);
            entity.setNameTagVisible(true);
            return true;
        }

        if (this.id == Effect.ABSORPTION) {
            entity.setAbsorption(0);
            return true;
        }

        return true;
    }

    @Override
    public Effect clone() {
        try {
            return (Effect) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public CompoundTag save() {
        return save(new CompoundTag());
    }

    public CompoundTag save(CompoundTag tag) {
        return tag.putByte("Id", id)
                .putByte("Amplifier", amplifier)
                .putInt("Duration", duration)
                .putInt("DurationEasy", duration)
                .putInt("DurationNormal", duration)
                .putInt("DurationHard", duration)
                .putBoolean("Ambient", ambient)
                .putBoolean("ShowParticles", show)
                .putBoolean("DisplayOnScreenTextureAnimation", false);
    }

    @Nullable
    public static Effect load(CompoundTag tag) {
        Effect effect = getEffect(tag.getByte("Id"));
        if (effect == null) {
            return null;
        }
        return effect.setAmplifier(tag.getByte("Amplifier"))
                .setDuration(tag.getInt("Duration"))
                .setAmbient(tag.getBoolean("Ambient"))
                .setVisible(tag.getBoolean("ShowParticles"));
    }

    public static int calculateColor(Effect... effects) {
        int total = 0;
        int r = 0;
        int g = 0;
        int b = 0;

        for (Effect effect : effects) {
            if (!effect.isVisible()) {
                continue;
            }

            int level = effect.getAmplifier() + 1;

            r += effect.getRed() * level;
            g += effect.getGreen() * level;
            b += effect.getBlue() * level;

            total += level;
        }

        if (total == 0) {
            return 0;
        }

        r = (r / total) & 0xff;
        g = (g / total) & 0xff;
        b = (b / total) & 0xff;
        return (r << 16) | (g << 8) | b;
    }

    private AttributeModifier createAttributeModifier(AttributeModifier modifier) {
        return new AttributeModifier(modifier.getId(), modifier.getName(), getAttributeModifierValue(modifier), modifier.getOperation(), modifier.getOperand(), false);
    }

    protected float getAttributeModifierValue(AttributeModifier modifier) {
        return modifier.getAmount() * (amplifier + 1);
    }
}
