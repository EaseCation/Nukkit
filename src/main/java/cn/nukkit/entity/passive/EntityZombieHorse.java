package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFullNames;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

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
    protected void initEntity() {
        super.initEntity();

        dataProperties.putByte(DATA_HORSE_TYPE, HORSE_TYPE_ZOMBIE);

        inventory.setSize(2);

        boolean tamed = namedTag.getBoolean("IsTamed");
        if (tamed) {
            onTamed(false);
        }
        setDataFlag(DATA_FLAG_TAMED, tamed, false);

        movementSpeed = 0.1125f; //TODO: 0.1125-0.3375

        this.setMaxHealth(25);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putBoolean("IsTamed", isTamed());
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

        inventory.sendArmorContents(player);

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
        if (true/*!V1_21_130.isAvailable()*/) {
            return false;
        }
        return super.canRide();
    }

    @Override
    public boolean canDoInteraction(Player player) {
        if (true/*!V1_21_130.isAvailable()*/) {
            return false;
        }
        if (isBaby()) {
            return false;
        }
        if (player.isSneaking()) {
            return isTamed();
        }
        if (inventory.getItem(1).isNull() && player.getInventory().getItemInHand().isHorseArmor()) {
            return true;
        }
        if (!isSaddled() && player.getInventory().getItemInHand().is(Item.SADDLE)) {
            return true;
        }
        return passengers.isEmpty();
    }

    @Override
    public String getInteractButtonText(Player player) {
        if (true/*!V1_21_130.isAvailable()*/) {
            return "";
        }
        if (isBaby()) {
            return "";
        }
        if (player.isSneaking()) {
            return isTamed() ? "action.interact.opencontainer" : "";
        }
        if (inventory.getItem(1).isNull() && player.getInventory().getItemInHand().isHorseArmor()) {
            return "action.interact.equiphorsearmor";
        }
        if (!isSaddled() && player.getInventory().getItemInHand().is(Item.SADDLE)) {
            return "action.interact.saddle";
        }
        if (!passengers.isEmpty()) {
            return "";
        }
        return isTamed() ? "action.interact.ride.horse" : "action.interact.mount";
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        if (true/*!V1_21_130.isAvailable()*/) {
            return false;
        }
        if (isBaby()) {
            return false;
        }

        if (!isTamed() && (item.is(Item.SADDLE) || item.isHorseArmor())) {
            makeMad();
            return false;
        }

        if (player.isSneaking()) {
            openInventory(player);
            return false;
        }

        if (item.isHorseArmor() && inventory.getItem(1).isNull()) {
            Item horseArmor = item.clone();
            horseArmor.setCount(1);
            inventory.setItem(1, horseArmor);
            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ARMOR_EQUIP_GENERIC, EntityFullNames.ZOMBIE_HORSE);
            return true;
        }

        if (item.is(Item.SADDLE) && !isSaddled()) {
            Item saddle = item.clone();
            saddle.setCount(1);
            inventory.setItem(0, saddle);
            return true;
        }

        if (passengers.isEmpty()) {
            mountEntity(player);
        }
        return false;
    }

    @Override
    public void openInventory(Player player) {
        if (true/*!V1_21_130.isAvailable()*/) {
            return;
        }
        super.openInventory(player);
    }

    @Override
    public void updateSaddled(boolean saddled, boolean send) {
        if (true/*!V1_21_130.isAvailable()*/) {
            return;
        }
        super.updateSaddled(saddled, send);
    }

    @Override
    public void updatePlayerJump(boolean jumping) {
        if (true/*!V1_21_130.isAvailable()*/) {
            return;
        }
        super.updatePlayerJump(jumping);
    }

    @Override
    public float getJumpStrength() {
        return 0.5f; //TODO: 0.5-0.7
    }

    public void onTamed() {
        onTamed(true);
    }

    public void onTamed(boolean send) {
        setDataProperty(new ByteEntityData(DATA_CONTAINER_TYPE, inventory.getType().getNetworkType()), send);
        setDataProperty(new IntEntityData(DATA_CONTAINER_BASE_SIZE, 2), send);
    }
}
