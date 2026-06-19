package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityBreeze extends EntityMob {
    public static final int NETWORK_ID = EntityID.BREEZE;

    public EntityBreeze(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.77f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setMaxHealth(30);
    }

    @Override
    public String getName() {
        return "Breeze";
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == DamageCause.FALL) {
            return false;
        }
        if (source.getCause() == DamageCause.PROJECTILE && source instanceof EntityDamageByEntityEvent event) {
            Entity projectile = source instanceof EntityDamageByChildEntityEvent ev ? ev.getChild() : event.getDamager();
            if (bounceProjectile(projectile)) {
                return false;
            }
        }
        return super.attack(source);
    }

    @Override
    public void spawnTo(Player player) {
        if (hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }

    @Override
    public boolean bounceProjectile(Entity projectile) {
        return projectile.getNetworkId() != EntityID.WIND_CHARGE_PROJECTILE && projectile.getNetworkId() != EntityID.BREEZE_WIND_CHARGE_PROJECTILE;
    }
}
