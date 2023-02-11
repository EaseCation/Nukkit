package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntitySlime extends EntityMob {

    public static final int NETWORK_ID = EntityID.SLIME;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntitySlime(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(4);
    }

    @Override
    public float getWidth() {
        return 1.04f;
    }

    @Override
    public float getHeight() {
        return 1.04f;
    }

    @Override
    public String getName() {
        return "Slime";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{
//                Item.get(Item.SLIME_BALL, 0, ThreadLocalRandom.current().nextInt(3)),
        };
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
