package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityRavager extends EntityMob {

    public static final int NETWORK_ID = EntityID.RAVAGER;

    public EntityRavager(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(100);
    }

    @Override
    public float getHeight() {
        return 2.2f;
    }

    @Override
    public float getWidth() {
        return 1.95f;
    }

    @Override
    public String getName() {
        return "Ravager";
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
        return new Vector3f(0, 2.1f + entity.getRidingOffset(), -0.3f);
    }
}
