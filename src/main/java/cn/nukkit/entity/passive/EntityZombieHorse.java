package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntitySmite;
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
public class EntityZombieHorse extends EntityAbstractHorse implements EntitySmite {

    public static final int NETWORK_ID = EntityID.ZOMBIE_HORSE;

    public EntityZombieHorse(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Zombie Horse";
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

        dataProperties.putByte(DATA_HORSE_TYPE, HORSE_TYPE_ZOMBIE);

        setDataFlag(DATA_FLAG_TAMED, true, false);

        movementSpeed = 0.2f;

        this.setMaxHealth(15);
    }

    @Override
    public Item[] getDrops() {
        return Stream.concat(
                Stream.of(Item.get(Item.ROTTEN_FLESH, 0, ThreadLocalRandom.current().nextInt(3))),
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
            return new Vector3f(0, 2.42001f, -0.2f);
        }
        return new Vector3f(0, 1.2f + entity.getRidingOffset(), -0.2f);
    }

    @Override
    public boolean canRide() {
        return false;
    }

    @Override
    public boolean canDoInteraction(Player player) {
        return false;
    }

    @Override
    public String getInteractButtonText(Player player) {
        return "";
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        return false;
    }

    @Override
    public void openInventory(Player player) {
    }

    @Override
    public void updateSaddled(boolean saddled, boolean send) {
    }

    @Override
    public void updatePlayerJump(boolean jumping) {
    }

    @Override
    public float getJumpStrength() {
        return 0.4f; //TODO: 0.4-1.0
    }
}
