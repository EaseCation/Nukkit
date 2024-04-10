package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityMagmaCube extends EntityMob {

    public static final int NETWORK_ID = EntityID.MAGMA_CUBE;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityMagmaCube(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(4);
        fireProof = true;
    }

    @Override
    public float getWidth() {
        return 1.04f;
    }

    @Override
    public float getHeight() {
        return 1.02f;
    }

    @Override
    public String getName() {
        return "Magma Cube";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{
//                Item.get(Item.MAGMA_CREAM, 0, ThreadLocalRandom.current().nextInt(2)),
        };
    }

    @Override
    public void spawnTo(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }

    @Override
    public int getBaseArmorValue() {
        return 3 * getDataPropertyInt(DATA_VARIANT);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == DamageCause.FALL) {
            return false;
        }
        return super.attack(source);
    }
}
