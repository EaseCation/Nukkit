package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Erik Miller | EinBexiii
 */
public class EntityPiglin extends EntityMob {

    public final static int NETWORK_ID = EntityID.PIGLIN;

    public static final int PIGLIN_VARIANT_RANGED = 0;
    public static final int PIGLIN_VARIANT_MELEE = 1;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityPiglin(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        dataProperties.putInt(DATA_VARIANT, namedTag.getInt("Variant", PIGLIN_VARIANT_MELEE));

        this.setMaxHealth(16);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt("Variant", getDataPropertyInt(DATA_VARIANT));
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
    public String getName() {
        return "Piglin";
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
    public float getRidingOffset() {
        if (getDataFlag(DATA_FLAG_BABY)) {
            return super.getRidingOffset();
        }
        return -0.5f;
    }
}
