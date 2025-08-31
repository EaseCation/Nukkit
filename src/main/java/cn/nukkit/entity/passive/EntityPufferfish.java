package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by PetteriM1
 */
public class EntityPufferfish extends EntityWaterAnimal {

    public static final int NETWORK_ID = EntityID.PUFFERFISH;

    public static final int PUFFERFISH_VARIANT_NORMAL = 0;
    public static final int PUFFERFISH_VARIANT_HALF = 1;
    public static final int PUFFERFISH_VARIANT_FULL = 2;

    public EntityPufferfish(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public String getName() {
        return "Pufferfish";
    }

    @Override
    public float getWidth() {
        return 0.8f;
    }

    @Override
    public float getHeight() {
        return 0.8f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        dataProperties.putInt(DATA_VARIANT, namedTag.getInt("Variant", PUFFERFISH_VARIANT_NORMAL));

        this.setMaxHealth(3);
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
                Item.get(Item.PUFFERFISH),
        };
    }
}
