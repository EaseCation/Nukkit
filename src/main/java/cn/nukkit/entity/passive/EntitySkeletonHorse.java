package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public class EntitySkeletonHorse extends EntityAbstractHorse implements EntitySmite {

    public static final int NETWORK_ID = EntityID.SKELETON_HORSE;

    public EntitySkeletonHorse(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Skeleton Horse";
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 1.6f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(15);
    }

    @Override
    public Item[] getDrops() {
        // Skeleton horse doesn't drop inventory
        return new Item[] {
                Item.get(Item.BONE, 0, ThreadLocalRandom.current().nextInt(3)),
        };
    }

    @Override
    public void openInventory(Player player) {
        // Skeleton horse doesn't have inventory. So do not open it
    }

    @Override
    public boolean canRide() {
        // Skeleton horse can ride without saddle
        return true;
    }

    @Override
    public void spawnTo(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }
}
