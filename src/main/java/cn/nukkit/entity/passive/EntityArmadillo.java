package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityArmadillo extends EntityAnimal {
    public static final int NETWORK_ID = EntityID.ARMADILLO;

    public EntityArmadillo(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 0.65f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setMaxHealth(12);
    }

    @Override
    public String getName() {
        return "Armadillo";
    }

    @Override
    public void spawnTo(Player player) {
        if (hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }
}
