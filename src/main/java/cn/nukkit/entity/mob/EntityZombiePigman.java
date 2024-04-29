package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public class EntityZombiePigman extends EntityMob implements EntitySmite {

    public static final int NETWORK_ID = EntityID.ZOMBIE_PIGMAN;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityZombiePigman(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        dataProperties.putShort(DATA_ZOMBIE_TYPE, EntityZombie.ZOMBIE_TYPE_PIG);

        this.setMaxHealth(20);

        fireProof = true;
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
        return "Zombie Pigman";
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
                Item.get(Item.ROTTEN_FLESH, 0, random.nextInt(2)),
                Item.get(Item.GOLD_NUGGET, 0, random.nextInt(2)),
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
    public Vector3f getMountedOffset(Entity entity) {
        return new Vector3f(0, 1.1f + entity.getRidingOffset(), -0.35f);
    }

    @Override
    public int getBaseArmorValue() {
        return 2;
    }
}
