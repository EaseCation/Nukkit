package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityCreaking extends EntityMob {
    public static final int NETWORK_ID = EntityID.CREAKING;

    public EntityCreaking(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 2.7f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setMaxHealth(1);
    }

    @Override
    public String getName() {
        return "Creaking";
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
