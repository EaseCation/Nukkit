package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public class EntityWitherSkeleton extends EntityMob implements EntitySmite {

    public static final int NETWORK_ID = EntityID.WITHER_SKELETON;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityWitherSkeleton(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
    }

    @Override
    public float getWidth() {
        return 0.72f;
    }

    @Override
    public float getHeight() {
        return 2.01f;
    }

    @Override
    public String getName() {
        return "Wither Skeleton";
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
                Item.get(Item.BONE, 0, random.nextInt(3)),
                Item.get(Item.COAL, 0, random.nextInt(2)),
        };
    }

    @Override
    public float getRidingOffset() {
        return -0.5f;
    }

    @Override
    public boolean canBeAffected(int effectId) {
        if (effectId == Effect.WITHER) {
            return false;
        }
        return super.canBeAffected(effectId);
    }
}
