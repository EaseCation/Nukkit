package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntitySniffer extends EntityAnimal {
    public static final int NETWORK_ID = EntityID.SNIFFER;

    public EntitySniffer(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Sniffer";
    }

    @Override
    public float getWidth() {
        return 1.9f;
    }

    @Override
    public float getHeight() {
        return 1.75f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(14);
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
