package cn.nukkit.potion;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.MobEffectPacket;

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

    Effect(int id, String identifier, String name, int r, int g, int b) {
        this(id, identifier, name, r, g, b, false);
    }

    Effect(int id, String identifier, String name, int r, int g, int b, boolean isBad) {
        this.id = id;
        this.identifier = identifier;
        this.name = name;
        this.bad = isBad;
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
            case Effect.REGENERATION:
                if ((interval = (40 >> this.amplifier)) > 0) {
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
                if (entity.getHealth() > 1|| this.id == FATAL_POISON) {
                    entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, 1));
                }
                break;
            case Effect.WITHER:
                entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, 1));
                break;
            case Effect.REGENERATION:
                if (entity.getHealth() < entity.getMaxHealth()) {
                    entity.heal(new EntityRegainHealthEvent(entity, 1, EntityRegainHealthEvent.CAUSE_MAGIC));
                }
                break;
        }
    }

    public int[] getColor() {
        return new int[]{this.color >> 16, (this.color >> 8) & 0xff, this.color & 0xff};
    }

    public void setColor(int r, int g, int b) {
        this.color = ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff);
    }

    public void add(Entity entity) {
        Effect oldEffect = entity.getEffect(getId());
        if (oldEffect != null && (Math.abs(this.getAmplifier()) < Math.abs(oldEffect.getAmplifier()) ||
                Math.abs(this.getAmplifier()) == Math.abs(oldEffect.getAmplifier())
                        && this.getDuration() < oldEffect.getDuration())) {
            return;
        }
        if (entity instanceof Player) {
            Player player = (Player) entity;

            MobEffectPacket pk = new MobEffectPacket();
            pk.eid = entity.getId();
            pk.effectId = this.getId();
            pk.amplifier = this.getAmplifier();
            pk.particles = this.isVisible();
            pk.duration = this.getDuration();
            if (oldEffect != null) {
                pk.eventId = MobEffectPacket.EVENT_MODIFY;
            } else {
                pk.eventId = MobEffectPacket.EVENT_ADD;
            }

            player.dataPacket(pk);

            if (this.id == Effect.SPEED) {
                if (oldEffect != null) {
                    player.setMovementSpeed(player.getMovementSpeed() / (1 + 0.2f * (oldEffect.amplifier + 1)), false);
                }
                player.setMovementSpeed(player.getMovementSpeed() * (1 + 0.2f * (this.amplifier + 1)));
            }

            if (this.id == Effect.SLOWNESS) {
                if (oldEffect != null) {
                    player.setMovementSpeed(player.getMovementSpeed() / (1 - 0.15f * (oldEffect.amplifier + 1)), false);
                }
                player.setMovementSpeed(player.getMovementSpeed() * (1 - 0.15f * (this.amplifier + 1)));
                player.setSprinting(false);
            }
        }

        if (this.id == Effect.INVISIBILITY) {
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INVISIBLE, true);
            entity.setNameTagVisible(false);
        }

        if (this.id == Effect.ABSORPTION) {
            int add = (this.amplifier + 1) * 4;
            if (add > entity.getAbsorption()) entity.setAbsorption(add);
        }
    }

    public void remove(Entity entity) {
        if (entity instanceof Player) {
            MobEffectPacket pk = new MobEffectPacket();
            pk.eid = entity.getId();
            pk.effectId = this.getId();
            pk.eventId = MobEffectPacket.EVENT_REMOVE;

            ((Player) entity).dataPacket(pk);

            if (this.id == Effect.SPEED) {
                entity.setSprinting(false);
                ((Player) entity).setMovementSpeed(Player.DEFAULT_SPEED);
            }
            if (this.id == Effect.SLOWNESS) {
                entity.setSprinting(false);
                ((Player) entity).setMovementSpeed(Player.DEFAULT_SPEED);
            }
        }

        if (this.id == Effect.INVISIBILITY) {
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INVISIBLE, false);
            entity.setNameTagVisible(true);
        }

        if (this.id == Effect.ABSORPTION) {
            entity.setAbsorption(0);
        }
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

            int[] color = effect.getColor();
            r += color[0] * level;
            g += color[1] * level;
            b += color[2] * level;

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
}
