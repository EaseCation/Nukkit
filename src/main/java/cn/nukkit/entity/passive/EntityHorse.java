package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * @author PikyCZ
 */
public class EntityHorse extends EntityAbstractHorse {

    public static final int NETWORK_ID = EntityID.HORSE;

    public EntityHorse(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Horse";
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 1.6f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(15);
    }

    @Override
    public Item[] getDrops() {
        return Stream.concat(
                Stream.of(Item.get(Item.LEATHER, 0, ThreadLocalRandom.current().nextInt(3))),
                Arrays.stream(super.getDrops())
        ).toArray(Item[]::new);
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
            return new Vector3f(0, 2.3200102f, -0.2f);
        }
        return new Vector3f(0, 1.1f + entity.getRidingOffset(), -0.2f);
    }
}
