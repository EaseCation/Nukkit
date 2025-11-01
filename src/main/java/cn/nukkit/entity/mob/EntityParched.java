package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityParched extends EntityMob implements EntitySmite {
    public static final int NETWORK_ID = EntityID.PARCHED;

    public EntityParched(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Parched";
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setMaxHealth(16);
    }

    @Override
    public void spawnTo(Player player) {
        if (hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }

    @Override
    public float getRidingOffset() {
        return -0.5f;
    }
}
