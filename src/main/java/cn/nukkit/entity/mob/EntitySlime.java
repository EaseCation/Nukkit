package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public class EntitySlime extends EntityMob {

    public static final int NETWORK_ID = EntityID.SLIME;

    public static final int SLIME_VARIANT_SMALL = 1;
    public static final int SLIME_VARIANT_MEDIUM = 2;
    public static final int SLIME_VARIANT_BIG = 4;

    public static final int SLIME_EVENT_NONE = 0;
    public static final int SLIME_EVENT_LAND = 1;
    public static final int SLIME_EVENT_JUMP = 2;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntitySlime(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        dataProperties.putByte(DATA_SLIME_CLIENT_EVENT, SLIME_EVENT_NONE);

        int size;
        if (namedTag.contains("Size")) {
            size = Math.max(namedTag.getByte("Size"), SLIME_VARIANT_SMALL);
        } else {
            size = switch (ThreadLocalRandom.current().nextInt(3)) {
                default -> SLIME_VARIANT_SMALL;
                case 1 -> SLIME_VARIANT_MEDIUM;
                case 2 -> SLIME_VARIANT_BIG;
            };
        }
        dataProperties.putInt(DATA_VARIANT, size);
        this.setMaxHealth(size * size);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putByte("Size", getDataPropertyInt(DATA_VARIANT));
    }

    @Override
    public float getWidth() {
        return 0.52f * getDataPropertyInt(DATA_VARIANT);
    }

    @Override
    public float getHeight() {
        return 0.52f * getDataPropertyInt(DATA_VARIANT);
    }

    @Override
    public String getName() {
        return "Slime";
    }

    @Override
    public Item[] getDrops() {
        if (getDataPropertyInt(DATA_VARIANT) != SLIME_VARIANT_SMALL) {
            return new Item[0];
        }
        return new Item[]{
                Item.get(Item.SLIME_BALL, 0, ThreadLocalRandom.current().nextInt(3)),
        };
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
