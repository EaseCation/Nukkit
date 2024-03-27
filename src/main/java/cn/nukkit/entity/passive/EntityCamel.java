package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityCamel extends EntityAnimal {
    public static final int NETWORK_ID = EntityID.CAMEL;

    public EntityCamel(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Camel";
    }

    @Override
    public float getWidth() {
        return 1.7f;
    }

    @Override
    public float getHeight() {
        return 2.375f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(32);
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
    public Vector3f getMountedOffset(Entity entity) {
        if (entity.getNetworkId() == -1) {
            return new Vector3f(0, 3.12501f, 0.5f);
//            return new Vector3f(0, 3.12501f, -0.5f); //TODO: passenger
        }
        return new Vector3f(0, 1.905f + entity.getRidingOffset(), 0.5f);
//        return new Vector3f(0, 1.905f + entity.getRidingOffset(), -0.5f); //TODO: passenger
    }
}
