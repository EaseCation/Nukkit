package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Author: BeYkeRYkt
 * Nukkit Project
 */
public class EntityWolf extends EntityAnimal {

    public static final int NETWORK_ID = EntityID.WOLF;

    public static final int WOLF_VARIANT_PALE = 0;
    public static final int WOLF_VARIANT_ASHEN = 1;
    public static final int WOLF_VARIANT_BLACK = 2;
    public static final int WOLF_VARIANT_CHESTNUT = 3;
    public static final int WOLF_VARIANT_RUSTY = 4;
    public static final int WOLF_VARIANT_SNOWY = 5;
    public static final int WOLF_VARIANT_SPOTTED = 6;
    public static final int WOLF_VARIANT_STRIPED = 7;
    public static final int WOLF_VARIANT_WOODS = 8;

    public EntityWolf(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 0.8f;
    }

    @Override
    public String getName() {
        return "Wolf";
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        dataProperties.putInt(DATA_VARIANT, namedTag.getInt("Variant", WOLF_VARIANT_PALE));

        this.setMaxHealth(8);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt("Variant", getDataPropertyInt(DATA_VARIANT));
    }

    @Override
    public boolean isBreedingItem(Item item) {
        return false; //only certain food
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
        return new Vector3f(0, 0.675f + entity.getRidingOffset(), -0.1f);
    }
}
