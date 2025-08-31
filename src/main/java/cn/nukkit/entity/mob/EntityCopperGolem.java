package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityCopperGolem extends EntityMob {
    public static final int NETWORK_ID = EntityID.COPPER_GOLEM;

    public EntityCopperGolem(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Copper Golem";
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 0.98f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setMaxHealth(12);
    }

    @Override
    public void spawnTo(Player player) {
        if (getViewers().containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }
}
