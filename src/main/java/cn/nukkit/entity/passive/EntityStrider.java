package cn.nukkit.entity.passive;

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
public class EntityStrider extends EntityAnimal {

    public final static int NETWORK_ID = EntityID.STRIDER;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityStrider(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
        fireProof = true;
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 1.7f;
    }

    @Override
    public String getName() {
        return "Strider";
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
                Item.get(Item.STRING, 0, random.nextInt(2, 6)),
        };
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        switch (entity.getNetworkId()) {
            case -1:
                return new Vector3f(0, 2.8200102f, -0.2f);
            case EntityID.STRIDER:
                return new Vector3f(0, 1.6f + entity.getRidingOffset(), 0);
        }
        return new Vector3f(0, 1.65f + entity.getRidingOffset(), -0.2f);
    }
}
