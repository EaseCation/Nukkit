package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.data.NBTEntityData;
import cn.nukkit.entity.data.SlotEntityData;
import cn.nukkit.entity.data.Vector3fEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Mth;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author CreeperFace
 */
public class EntityFirework extends Entity {

    public static final int NETWORK_ID = EntityID.FIREWORKS_ROCKET;

    private static final Vector3f DEFAULT_DIRECTION = new Vector3f(0, 1, 0);

    private int fireworkAge;
    private int lifetime;
    private Item firework;
    @Nullable
    private Entity attached;
    @Setter
    private boolean calculateAttack = true;

    public EntityFirework(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, false);
    }

    public EntityFirework(FullChunk chunk, CompoundTag nbt, boolean projectile) {
        this(chunk, nbt, projectile, null);
    }

    public EntityFirework(FullChunk chunk, CompoundTag nbt, @Nullable Entity attached) {
        this(chunk, nbt, true, attached);
    }

    public EntityFirework(FullChunk chunk, CompoundTag nbt, boolean projectile, @Nullable Entity attached) {
        super(chunk, nbt);
        this.attached = attached;

        if (namedTag.contains("FireworkItem")) {
            firework = NBTIO.getItemHelper(namedTag.getCompound("FireworkItem"));
        } else {
            firework = Item.get(Item.FIREWORK_ROCKET);
        }

        Random rand = ThreadLocalRandom.current();
        this.lifetime = firework instanceof ItemFirework ? ((ItemFirework) firework).getLifeTime() : (20 + rand.nextInt(12));
        this.setDataProperty(new SlotEntityData(Entity.DATA_DISPLAY_ITEM, firework), false);
        //this.setDataProperty(new NBTEntityData(Entity.DATA_FIREWORK_ITEM, firework.getNamedTag()), false);
        this.setDataProperty(new Vector3fEntityData(Entity.DATA_FIREWORK_DIRECTION, projectile ? new Vector3f((float) motionX, (float) motionY, (float) motionZ) : DEFAULT_DIRECTION), false);
        this.setDataProperty(new LongEntityData(Entity.DATA_FIREWORK_ATTACHED_EID, attached != null ? attached.getId() : -1), false);

        if (projectile) {
            this.motionX = rand.nextGaussian() * 0.001 + this.motionX * 0.02;
            this.motionY = rand.nextGaussian() * 0.001 + this.motionY * 0.02;
            this.motionZ = rand.nextGaussian() * 0.001 + this.motionZ * 0.02;
        } else {
            this.motionX = rand.nextGaussian() * 0.001;
            this.motionY = rand.nextGaussian() * 0.001 + 1 * 0.02;
            this.motionZ = rand.nextGaussian() * 0.001;
        }
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;
        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }
        this.lastUpdate = currentTick;

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {
            if (attached != null) {
                //TODO: elytra
            }

            Vector3f dir = getDataPropertyVector3f(DATA_FIREWORK_DIRECTION);
            this.motionX = this.motionX * 1.05 + dir.x * 0.03;
            this.motionY = this.motionY * 1.05 + dir.y * 0.03;
            this.motionZ = this.motionZ * 1.05 + dir.z * 0.03;

            this.move(this.motionX, this.motionY, this.motionZ);

            this.updateMovement();

            float f = (float) Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.yaw = (float) (Mth.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
            this.pitch = (float) (Mth.atan2(this.motionY, f) * (180D / Math.PI));

            if (this.fireworkAge == 0) {
                this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_LAUNCH);
            }

            this.fireworkAge++;

            hasUpdate = true;
            if (this.fireworkAge >= this.lifetime) {
                EntityEventPacket pk = new EntityEventPacket();
                pk.event = EntityEventPacket.FIREWORK_EXPLOSION;
                pk.eid = this.getId();
                Server.broadcastPacket(getViewers().values(), pk);

                if (calculateAttack && firework instanceof ItemFirework) {
                    ItemFirework firework = (ItemFirework) this.firework;
                    CompoundTag nbt = firework.getNamedTag();
                    if (nbt != null) {
                        ListTag<CompoundTag> explosions = nbt.getCompound("Fireworks").getList("Explosions", CompoundTag.class);
                        if (!explosions.isEmpty()) {
                            float damage = 2 * explosions.size() + 5;

                            long attached = getDataPropertyLong(DATA_FIREWORK_ATTACHED_EID);
                            if (attached != -1) {
                                Entity entity = level.getEntity(attached);
                                if (entity != null) {
                                    entity.attack(new EntityDamageByEntityEvent(this, entity, DamageCause.FIREWORKS, damage));
                                }
                            }

                            Entity[] entities = level.getNearbyEntities(getBoundingBox().grow(5), this);
                            for (Entity entity : entities) {
                                if (entity.getId() == attached) {
                                    continue;
                                }

                                if (distanceSquared(entity) > 5 * 5) {
                                    continue;
                                }

                                boolean inSight = false;
                                for (int i = 0; i < 2; i++) {
                                    if (level.getCollisionBlocks(new SimpleAxisAlignedBB(this, entity.add(0, 0.5 * i, 0)), true).length == 0) {
                                        inSight = true;
                                        break;
                                    }
                                }
                                if (!inSight) {
                                    continue;
                                }

                                entity.attack(new EntityDamageByEntityEvent(this, entity, DamageCause.FIREWORKS, damage * (float) Math.sqrt((5 - distance(entity)) / 5)));
                            }
                        }
                    }
                }

                this.close();
            }
        }

        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return (source.getCause() == DamageCause.VOID ||
                source.getCause() == DamageCause.FIRE_TICK ||
                source.getCause() == DamageCause.ENTITY_EXPLOSION ||
                source.getCause() == DamageCause.BLOCK_EXPLOSION)
                && super.attack(source);
    }

    public void setFirework(Item item) {
        this.firework = item;
        this.setDataProperty(new NBTEntityData(Entity.DATA_DISPLAY_ITEM, item.getNamedTag()));
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    public void spawnTo(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }

    public int getLifeTime() {
        return lifetime;
    }
}
