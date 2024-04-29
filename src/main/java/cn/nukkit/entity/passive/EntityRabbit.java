package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Author: BeYkeRYkt
 * Nukkit Project
 */
public class EntityRabbit extends EntityAnimal {

    public static final int NETWORK_ID = EntityID.RABBIT;

    public static final int RABBIT_VARIANT_BROWN = 0;
    public static final int RABBIT_VARIANT_WHITE = 1;
    public static final int RABBIT_VARIANT_BLACK = 2;
    public static final int RABBIT_VARIANT_SPLOTCHED = 3;
    public static final int RABBIT_VARIANT_DESERT = 4;
    public static final int RABBIT_VARIANT_SALT = 5;

    public EntityRabbit(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.67f;
    }

    @Override
    public float getHeight() {
        return 0.67f;
    }

    @Override
    public String getName() {
        return "Rabbit";
    }

    @Override
    public Item[] getDrops() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new Item[]{
                Item.get(this.isOnFire() ? Item.COOKED_RABBIT : Item.RABBIT, 0, random.nextInt(2)),
                Item.get(Item.RABBIT_HIDE, 0, random.nextInt(2)),
//                Item.get(Item.RABBIT_FOOT),
        };
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        dataProperties.putInt(DATA_VARIANT, namedTag.getInt("Variant", RABBIT_VARIANT_BROWN));

        setMaxHealth(3);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt("Variant", getDataPropertyInt(DATA_VARIANT));
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
