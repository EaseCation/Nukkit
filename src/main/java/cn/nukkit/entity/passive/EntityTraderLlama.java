package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

public class EntityTraderLlama extends EntityAnimal {
    public static final int NETWORK_ID = EntityID.TRADER_LLAMA;

    public EntityTraderLlama(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Trader Llama";
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 1.87f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(15);
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
        return new Item[]{
                Item.get(Item.LEATHER, 0, ThreadLocalRandom.current().nextInt(3)),
        };
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        if (entity.getNetworkId() == -1) {
            return new Vector3f(0, 2.3900099f, -0.3f);
        }
        return new Vector3f(0, 1.17f + entity.getRidingOffset(), -0.3f);
    }
}
