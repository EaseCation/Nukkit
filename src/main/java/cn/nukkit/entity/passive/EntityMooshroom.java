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
 * Author: BeYkeRYkt
 * Nukkit Project
 */
public class EntityMooshroom extends EntityAnimal {

    public static final int NETWORK_ID = EntityID.MOOSHROOM;

    public static final int MOOSHROOM_VARIANT_RED = 0;
    public static final int MOOSHROOM_VARIANT_BROWN = 1;

    public static final int MOOSHROOM_MARK_NOTHING = -1;
    public static final int MOOSHROOM_MARK_POPPY = 0;
    public static final int MOOSHROOM_MARK_CORNFLOWER = 1;
    public static final int MOOSHROOM_MARK_TULIPS = 2;
    public static final int MOOSHROOM_MARK_AZURE_BLUET = 3;
    public static final int MOOSHROOM_MARK_LILY_OF_THE_VALLEY = 4;
    public static final int MOOSHROOM_MARK_DANDELION = 5;
    public static final int MOOSHROOM_MARK_BLUE_ORCHID = 6;
    public static final int MOOSHROOM_MARK_ALLIUM = 7;
    public static final int MOOSHROOM_MARK_OXEYE_DAISY = 8;
    public static final int MOOSHROOM_MARK_WITHER_ROSE = 9;
    public static final int MOOSHROOM_MARK_TORCHFLOWER = 10;

    public EntityMooshroom(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 1.3f;
    }

    @Override
    public String getName() {
        return "Mooshroom";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{
                Item.get(Item.LEATHER, 0, ThreadLocalRandom.current().nextInt(3)),
                Item.get(this.isOnFire() ? Item.COOKED_BEEF : Item.BEEF, 0, ThreadLocalRandom.current().nextInt(1, 4)),
        };
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        dataProperties.putInt(DATA_VARIANT, namedTag.getInt("Variant", MOOSHROOM_VARIANT_RED));
        dataProperties.putInt(DATA_MARK_VARIANT, namedTag.getInt("MarkVariant", MOOSHROOM_MARK_NOTHING));

        setMaxHealth(10);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt("Variant", getDataPropertyInt(DATA_VARIANT));
        namedTag.putInt("MarkVariant", getDataPropertyInt(DATA_MARK_VARIANT));
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
    public Vector3f getMountedOffset(Entity entity) {
        return new Vector3f(0, 1.105f + entity.getRidingOffset(), 0);
    }
}
