package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

public class EntitySulfurCube extends EntityMob {
    public static final int NETWORK_ID = EntityID.SULFUR_CUBE;

    public EntitySulfurCube(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        dataProperties.putByte(DATA_SLIME_CLIENT_EVENT, EntitySlime.SLIME_EVENT_NONE);

        int size;
        if (namedTag.contains("Size")) {
            size = Math.max(namedTag.getByte("Size"), EntitySlime.SLIME_VARIANT_SMALL);
        } else {
            size = switch (ThreadLocalRandom.current().nextInt(2)) {
                default -> EntitySlime.SLIME_VARIANT_SMALL;
                case 1 -> EntitySlime.SLIME_VARIANT_MEDIUM;
            };
        }
        dataProperties.putInt(DATA_VARIANT, size);
        this.setMaxHealth(size * 4);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putByte("Size", getDataPropertyInt(DATA_VARIANT));
    }

    @Override
    public float getWidth() {
        return 0.49f * getDataPropertyInt(DATA_VARIANT);
    }

    @Override
    public float getHeight() {
        return 0.49f * getDataPropertyInt(DATA_VARIANT);
    }

    @Override
    public String getName() {
        return "Sulfur Cube";
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
