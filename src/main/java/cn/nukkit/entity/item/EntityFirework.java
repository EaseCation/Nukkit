package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.data.NBTEntityData;
import cn.nukkit.entity.data.SlotEntityData;
import cn.nukkit.entity.data.Vector3fEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author CreeperFace
 */
public class EntityFirework extends Entity {

    public static final int NETWORK_ID = 72;

    private int fireworkAge;
    private int lifetime;
    private Item firework;

    public EntityFirework(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.fireworkAge = 0;

        Random rand = ThreadLocalRandom.current();
        this.motionX = rand.nextGaussian() * 0.001D;
        this.motionZ = rand.nextGaussian() * 0.001D;
        this.motionY = 0.05D;

        if (nbt.contains("FireworkItem")) {
            firework = NBTIO.getItemHelper(nbt.getCompound("FireworkItem"));
        } else {
            firework = new ItemFirework();
        }

        this.lifetime = firework instanceof ItemFirework ? ((ItemFirework) firework).getLifeTime() : (30 + rand.nextInt(12));
        this.setDataProperty(new SlotEntityData(Entity.DATA_DISPLAY_ITEM, firework));
        //this.setDataProperty(new NBTEntityData(Entity.DATA_DISPLAY_ITEM, firework.getNamedTag()));
        this.setDataProperty(new Vector3fEntityData(Entity.DATA_DISPLAY_OFFSET, new Vector3f(0, 1, 0)));
        this.setDataProperty(new LongEntityData(Entity.DATA_HAS_DISPLAY, -1));
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

        this.timing.startTiming();


        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {

            this.motionX *= 1.15D;
            this.motionZ *= 1.15D;
            this.motionY += 0.04D;
            this.move(this.motionX, this.motionY, this.motionZ);

            this.updateMovement();


            float f = (float) Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.yaw = (float) (Math.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

            this.pitch = (float) (Math.atan2(this.motionY, f) * (180D / Math.PI));


            if (this.fireworkAge == 0) {
                this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_LAUNCH);
            }

            this.fireworkAge++;

            hasUpdate = true;
            if (this.fireworkAge >= this.lifetime) {
                EntityEventPacket pk = new EntityEventPacket();
                pk.data = 0;
                pk.event = EntityEventPacket.FIREWORK_EXPLOSION;
                pk.eid = this.getId();

                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_LARGE_BLAST, -1);

                Server.broadcastPacket(getViewers().values(), pk);

                this.kill();
                hasUpdate = true;
            }
        }

        this.timing.stopTiming();

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
        super.spawnTo(player);

        AddEntityPacket pk = new AddEntityPacket();
        pk.type = NETWORK_ID;
        pk.entityUniqueId = this.getId();
        pk.entityRuntimeId = this.getId();
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.metadata = this.dataProperties;
        player.dataPacket(pk);
    }
}
