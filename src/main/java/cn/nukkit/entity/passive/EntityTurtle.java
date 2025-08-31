package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by PetteriM1
 */
public class EntityTurtle extends EntityAnimal {

    public static final int NETWORK_ID = EntityID.TURTLE;

    public EntityTurtle(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public String getName() {
        return "Turtle";
    }

    @Override
    public float getWidth() {
        if (isBaby()) {
            return 0.6f;
        }
        return 1.2f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.2f;
        }
        return 0.4f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(30);
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
        if (isBaby()) {
            return new Item[0];
        }
        return new Item[]{
                Item.get(Block.getItemId(Block.SEAGRASS), 0, ThreadLocalRandom.current().nextInt(3)),
        };
    }
}
