package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityNautilus extends EntityWaterAnimal {
    public static final int NETWORK_ID = EntityID.NAUTILUS;

    public EntityNautilus(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.875f;
    }

    @Override
    public float getHeight() {
        return 0.95f;
    }

    @Override
    public String getName() {
        return "Nautilus";
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        setMaxHealth(15);
    }

    @Override
    public void spawnTo(Player player) {
        if (getViewers().containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }

    @Override
    protected float getKnockbackResistance() {
        return 0.3f;
    }
}
