package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityHappyGhast extends EntityAnimal {
    public static final int NETWORK_ID = EntityID.HAPPY_GHAST;

    public EntityHappyGhast(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getHeight() {
        return 4;
    }

    @Override
    public float getWidth() {
        return 4;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        setMaxHealth(20);
    }

    @Override
    public String getName() {
        return "Happy Ghast";
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
