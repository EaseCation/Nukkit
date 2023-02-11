package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Kaooot
 * @version 1.0
 */
public class EntityBee extends EntityAnimal {

    public static final int NETWORK_ID = EntityID.BEE;

    public EntityBee(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Bee";
    }

    @Override
    public float getWidth() {
        return 0.55f;
    }

    @Override
    public float getHeight() {
        return 0.5f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(10);
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
