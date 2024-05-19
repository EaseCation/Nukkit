package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Erik Miller | EinBexiii
 */
public class EntityHoglin extends EntityMob {

    public final static int NETWORK_ID = EntityID.HOGLIN;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityHoglin(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(40);
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 1.4f;
    }

    @Override
    public String getName() {
        return "Hoglin";
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
                Item.get(this.isOnFire() ? Item.COOKED_PORKCHOP : Item.PORKCHOP, 0, random.nextInt(2, 5)),
                Item.get(Item.LEATHER, 0, random.nextInt(2)),
        };
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        return new Vector3f(0, 0.9f + entity.getRidingOffset(), -0.3f);
//        return new Vector3f(0, 2.4f + entity.getRidingOffset(), -0.3f); //TODO: middle
//        return new Vector3f(0, 3.9f + entity.getRidingOffset(), -0.3f); //TODO: top
    }

    @Override
    protected float getKnockbackResistance() {
        return 0.6f;
    }
}
