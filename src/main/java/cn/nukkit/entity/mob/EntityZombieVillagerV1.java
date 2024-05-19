package cn.nukkit.entity.mob;

import cn.nukkit.Difficulty;
import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.passive.EntityVillagerV1;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public class EntityZombieVillagerV1 extends EntityMob implements EntitySmite {

    public static final int NETWORK_ID = EntityID.ZOMBIE_VILLAGER;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityZombieVillagerV1(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        dataProperties.putShort(DATA_ZOMBIE_TYPE, EntityZombie.ZOMBIE_TYPE_VILLAGER);

        dataProperties.putInt(DATA_VARIANT, namedTag.getInt("Variant", EntityVillagerV1.PROFESSION_GENERIC));

        this.setMaxHealth(20);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt("Variant", getDataPropertyInt(DATA_VARIANT));
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    public String getName() {
        return "Zombie Villager";
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
    public Item[] getDrops() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new Item[]{
                Item.get(Item.ROTTEN_FLESH, 0, random.nextInt(3)),
        };
    }

    @Override
    public float getRidingOffset() {
        if (getDataFlag(DATA_FLAG_BABY)) {
            return super.getRidingOffset();
        }
        return -0.5f;
    }

    @Override
    protected void onAttackSuccess(EntityDamageByEntityEvent source) {
        if (!isOnFire()) {
            return;
        }
        int difficulty = server.getDifficulty();
        if (difficulty < Difficulty.NORMAL.ordinal()) {
            return;
        }
        source.getEntity().setOnFire(2);
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        return new Vector3f(0, 1.1f + entity.getRidingOffset(), -0.35f);
    }

    @Override
    public int getBaseArmorValue() {
        return 2;
    }
}
