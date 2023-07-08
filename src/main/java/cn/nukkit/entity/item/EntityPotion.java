package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCampfire;
import cn.nukkit.block.Blocks;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.potion.PotionCollideEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.SpellParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;

/**
 * @author xtypr
 */
public class EntityPotion extends EntityProjectile {

    public static final int NETWORK_ID = EntityID.SPLASH_POTION;

    public int potionId;

    public Item item;

    public EntityPotion(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public EntityPotion(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        potionId = this.namedTag.getShort("PotionId");

        item = NBTIO.getItemHelper(this.namedTag.getCompound("Item"));
        if (item.getId() != ItemID.SPLASH_POTION) {
            item = Item.get(ItemID.SPLASH_POTION, potionId);
        }
        if (item.getDamage() != potionId) {
            item.setDamage(potionId);
        }

        this.dataProperties.putShort(DATA_AUX_VALUE_DATA, this.potionId);

        /*Effect effect = Potion.getEffect(potionId, true); TODO: potion color

        if(effect != null) {
            int count = 0;
            int[] c = effect.getColor();
            count += effect.getAmplifier() + 1;

            int r = ((c[0] * (effect.getAmplifier() + 1)) / count) & 0xff;
            int g = ((c[1] * (effect.getAmplifier() + 1)) / count) & 0xff;
            int b = ((c[2] * (effect.getAmplifier() + 1)) / count) & 0xff;

            this.setDataProperty(new IntEntityData(Entity.DATA_UNKNOWN, (r << 16) + (g << 8) + b));
        }*/
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getLength() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    protected float getGravity() {
        return 0.05f;
    }

    @Override
    protected float getDrag() {
        return 0.01f;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        this.splash(entity);
    }

    private void splash(Entity collidedWith) {
        Potion potion = Potion.getPotion(this.potionId);
        PotionCollideEvent event = new PotionCollideEvent(potion, this);
        this.server.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        this.close();

        potion = event.getPotion();
        if (potion == null) {
            return;
        }

        boolean isWater = potion.getId() == Potion.WATER;
        Effect[] effects = potion.getEffects();

        this.getLevel().addParticle(new SpellParticle(this, Effect.calculateColor(effects)));
        this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_GLASS);

        if (isWater) {
            Block block = level.getBlock(this);
            if (block instanceof BlockCampfire) {
                ((BlockCampfire) block).tryDouseFire();
            } else {
                if (block.isFire()) {
                    level.setBlock(block, Blocks.air(), true);
                }

                for (BlockFace face : Plane.HORIZONTAL) {
                    Block side = block.getSide(face);
                    if (side.isFire()) {
                        level.setBlock(side, Blocks.air(), true);
                    } else if (side instanceof BlockCampfire) {
                        ((BlockCampfire) side).tryDouseFire();
                    }
                }
            }
        }

        if (isLinger()) {
            ListTag<CompoundTag> mobEffects = new ListTag<>("mobEffects");
            for (Effect effect : effects) {
                mobEffects.add(effect.save());
            }

            EntityAreaEffectCloud aoeCloud = new EntityAreaEffectCloud(getChunk(), getDefaultNBT(this)
                    .putList(mobEffects));
            aoeCloud.setOwner(shootingEntity);
            aoeCloud.spawnToAll();
            return;
        }

        if (!isWater && effects[0].getId() == Effect.NO_EFFECT) {
            return;
        }

        Entity[] entities = this.getLevel().getNearbyEntities(this.getBoundingBox().grow(4.125, 2.125, 4.125), this);
        for (Entity entity : entities) {
            if (!entity.isAlive()) {
                continue;
            }

            if (entity instanceof Player && ((Player) entity).isSpectator()) {
                continue;
            }

            double distance = entity.distanceSquared(this);
            if (distance >= 16) {
                continue;
            }

            if (isWater) {
                if (entity.isOnFire()) {
                    level.addLevelSoundEvent(entity.upVec(), LevelSoundEventPacket.SOUND_FIZZ);
                    level.addLevelEvent(entity, LevelEventPacket.EVENT_PARTICLE_FIZZ_EFFECT, 513);
                    entity.extinguish();
                }
                continue;
            }

            float d = entity.equals(collidedWith) ? 1 : (float) (1 - Math.sqrt(distance) / 4);
            potion.applyPotion(entity, item, 0.75f * d, d);
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        this.timing.startTiming();

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.age > 1200) {
            this.kill();
            hasUpdate = true;
        } else if (this.isCollided) {
            this.splash(null);
            hasUpdate = true;
        }

        this.timing.stopTiming();
        return hasUpdate;
    }

    @Override
    public void spawnTo(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }

    public boolean isLinger() {
        return false;
    }
}
