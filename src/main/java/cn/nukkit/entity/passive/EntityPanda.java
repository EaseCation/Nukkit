package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

public class EntityPanda extends EntityAnimal {

    public static final int NETWORK_ID = EntityID.PANDA;

    public static final int PANDA_VARIANT_DEFAULT = 0;
    public static final int PANDA_VARIANT_LAZY = 1;
    public static final int PANDA_VARIANT_WORRIED = 2;
    public static final int PANDA_VARIANT_PLAYFUL = 3;
    public static final int PANDA_VARIANT_BROWN = 4;
    public static final int PANDA_VARIANT_WEAK = 5;
    public static final int PANDA_VARIANT_AGGRESSIVE = 6;

    public EntityPanda(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Panda";
    }

    @Override
    public float getWidth() {
        return 1.7f;
    }

    @Override
    public float getHeight() {
        return 1.5f;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        dataProperties.putInt(DATA_VARIANT, namedTag.getInt("Variant", PANDA_VARIANT_DEFAULT));

        this.setMaxHealth(20);
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

    @Override
    public Item[] getDrops() {
        return new Item[]{
                Item.get(Block.getItemId(Block.BAMBOO), 0, ThreadLocalRandom.current().nextInt(3)),
        };
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        return new Vector3f(0, 1.105f + entity.getRidingOffset(), 0);
    }
}
