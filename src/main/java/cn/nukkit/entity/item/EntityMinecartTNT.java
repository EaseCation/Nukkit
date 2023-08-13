package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.MinecartType;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Author: Adam Matthew [larryTheCoder]
 * <p>
 * Nukkit Project.
 */
public class EntityMinecartTNT extends EntityMinecartAbstract implements EntityExplosive {

    public static final int NETWORK_ID = EntityID.TNT_MINECART;

    private int fuse;

    public EntityMinecartTNT(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        super.setDisplayBlock(Block.get(BlockID.TNT), false);
    }

    @Override
    public boolean isRideable() {
        return false;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        if (namedTag.contains("TNTFuse")) {
            fuse = namedTag.getByte("TNTFuse");
        } else {
            fuse = 80;
        }
        this.setDataFlag(DATA_FLAG_CHARGED, false, false);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (this.isPrimed()) {
            int tickDiff = currentTick - lastUpdate;
            if (tickDiff <= 0) {
                return false;
            }

            if (fuse % 5 == 0) {
                setDataProperty(new IntEntityData(DATA_FUSE_LENGTH, fuse));
            }

            fuse -= tickDiff;

            if (isAlive() && fuse <= 0) {
                if (this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)) {
                    this.explode();
                } else {
                    this.close();
                }

                return false;
            }
        }

        return super.onUpdate(currentTick);
    }

    @Override
    public void activate(int x, int y, int z, boolean flag) {
        if (!this.prime()) {
            return;
        }

        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_IGNITE);
    }

    @Override
    public void explode() {
        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, 3);
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        this.close();

        Explosion explosion = new Explosion(this, event.getForce(), this);
        if (event.isBlockBreaking()) {
            explosion.explodeA();
        }
        explosion.explodeB();
    }

    @Override
    public void dropItem() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) this.lastDamageCause).getDamager();
            if (damager instanceof Player && ((Player) damager).isCreative()) {
                return;
            }
        }
        this.level.dropItem(this, Item.get(Item.TNT_MINECART));
    }

    @Override
    public String getName() {
        return getType().getName();
    }

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(3);
    }

    @Override
    public int getNetworkId() {
        return EntityMinecartTNT.NETWORK_ID;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        super.namedTag.putInt("TNTFuse", this.fuse);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean interact = super.onInteract(player, item, clickedPos);
        if (item.getId() == Item.FLINT_AND_STEEL || item.getId() == Item.FIRE_CHARGE || item.hasEnchantment(Enchantment.FIRE_ASPECT)) {
            if (!this.prime()) {
                return interact;
            }

            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_IGNITE);
            return true;
        }

        return interact;
    }

    @Override
    public boolean mountEntity(Entity entity, byte mode) {
        return false;
    }

    @Override
    public String getInteractButtonText(Player player) {
        return "";
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        DamageCause cause = source.getCause();

        if (this.isPrimed()) {
            if (cause != DamageCause.VOID) {
                return false;
            }
        } else if (cause == DamageCause.ENTITY_EXPLOSION || cause == DamageCause.BLOCK_EXPLOSION) {
            float damage = source.getDamage();
            float health = this.getHealth();
            if (damage > 0) {
                if (health - damage * 15 < 1) {
                    source.setDamage(Math.max(0, health / 15 - 1));
                }

                this.prime(ThreadLocalRandom.current().nextInt(10, 31));
            }
        }

        return super.attack(source);
    }

    public boolean isPrimed() {
        return this.fuse < 80;
    }

    public boolean prime() {
        return this.prime(79);
    }

    public boolean prime(int fuse) {
        if (this.isPrimed()) {
            return false;
        }

        if (!this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)) {
            return false;
        }

        this.fuse = fuse;
        return true;
    }
}
