package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityWarden extends EntityMob {

    public static final int NETWORK_ID = EntityID.WARDEN;

    public EntityWarden(FullChunk chunk, CompoundTag nbt) {
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
        return 2.9f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(500);
        fireProof = true;
    }

    @Override
    public String getName() {
        return "Warden";
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
    public Item[] getDrops() {
        return new Item[]{
                Item.get(ItemBlockID.SCULK_CATALYST),
        };
    }

    @Override
    protected float getKnockbackResistance() {
        return 1;
    }

    @Override
    public boolean canDisableShield() {
        return true;
    }
}
