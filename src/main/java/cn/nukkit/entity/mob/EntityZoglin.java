package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Erik Miller | EinBexiii
 */
public class EntityZoglin extends EntityMob implements EntitySmite {

    public final static int NETWORK_ID = EntityID.ZOGLIN;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityZoglin(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(40);
        fireProof = true;
    }

    @Override
    public float getWidth() {
        if (isBaby()) {
            return 0.85f;
        }
        return 1.4f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.85f;
        }
        return 1.4f;
    }

    @Override
    public String getName() {
        return "Zoglin";
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
                Item.get(Item.ROTTEN_FLESH, 0, random.nextInt(1, 4)),
        };
    }

    @Override
    protected float getKnockbackResistance() {
        return 0.6f;
    }

    public boolean isBaby() {
        return getDataFlag(DATA_FLAG_BABY);
    }
}
