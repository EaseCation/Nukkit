package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

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

        dataProperties.putByte(DATA_SLIME_CLIENT_EVENT, EntitySlime.SLIME_EVENT_NONE);

        int size;
        if (namedTag.contains("Size")) {
            size = Math.max(namedTag.getByte("Size"), EntitySlime.SLIME_VARIANT_SMALL);
        } else {
            size = switch (ThreadLocalRandom.current().nextInt(3)) {
                default -> EntitySlime.SLIME_VARIANT_SMALL;
                case 1 -> EntitySlime.SLIME_VARIANT_MEDIUM;
                case 2 -> EntitySlime.SLIME_VARIANT_BIG;
            };
        }
        dataProperties.putInt(DATA_VARIANT, size);
        this.setMaxHealth(size * size);

        fireProof = true;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putByte("Size", getDataPropertyInt(DATA_VARIANT));
    }

    @Override
    public float getWidth() {
        return 0.52f * getDataPropertyInt(DATA_VARIANT);
    }

    @Override
    public float getHeight() {
        return 0.52f * getDataPropertyInt(DATA_VARIANT);
    }

    @Override
    public String getName() {
        return "Magma Cube";
    }

    @Override
    public Item[] getDrops() {
        if (getDataPropertyInt(DATA_VARIANT) == EntitySlime.SLIME_VARIANT_SMALL) {
            return new Item[0];
        }
        return new Item[]{
                Item.get(Item.MAGMA_CREAM, 0, ThreadLocalRandom.current().nextInt(2)),
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
