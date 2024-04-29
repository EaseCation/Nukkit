package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
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
public class EntityMule extends EntityAbstractHorse {

    public static final int NETWORK_ID = EntityID.MULE;

    public EntityMule(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public Item[] getDrops() {
        return Stream.concat(
                Stream.of(
                        Item.get(Item.LEATHER, 0, ThreadLocalRandom.current().nextInt(3)),
                        Item.get(Item.CHEST, 0, getDataFlag(DATA_FLAG_CHESTED) ? 1 : 0)
                ),
                Arrays.stream(super.getDrops())
        ).toArray(Item[]::new);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Mule";
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

        dataProperties.putByte(DATA_HORSE_TYPE, HORSE_TYPE_MULE);

        boolean tamed = namedTag.getBoolean("IsTamed");
        if (tamed) {
            onTamed(false);
        }
        setDataFlag(DATA_FLAG_TAMED, tamed, false);

        boolean chested = namedTag.getBoolean("Chested");
        if (chested) {
            inventory.setSize(1 + 15);
        } else {
            inventory.setSize(1);
        }
        setDataFlag(DATA_FLAG_CHESTED, chested, false);

        movementSpeed = 0.175f;

        this.setMaxHealth(15); //TODO: 15-30
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putBoolean("IsTamed", isTamed());
        namedTag.putBoolean("Chested", getDataFlag(DATA_FLAG_CHESTED));
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
            return new Vector3f(0, 2.1950102f, -0.2f);
        }
        return new Vector3f(0, 0.975f + entity.getRidingOffset(), -0.2f);
    }

    @Override
    public boolean canDoInteraction(Player player) {
        if (isBaby()) {
            return false;
        }
        if (player.isSneaking()) {
            return isTamed();
        }
        if (!isSaddled() && player.getInventory().getItemInHand().is(Item.SADDLE)) {
            return true;
        }
        if (!getDataFlag(DATA_FLAG_CHESTED) && isTamed() && player.getInventory().getItemInHand().is(ItemBlockID.CHEST)) {
            return true;
        }
        return passengers.isEmpty();
    }

    @Override
    public String getInteractButtonText(Player player) {
        if (isBaby()) {
            return "";
        }
        if (player.isSneaking() && isTamed()) {
            return isTamed() ? "action.interact.opencontainer" : "";
        }
        if (!isSaddled() && player.getInventory().getItemInHand().is(Item.SADDLE)) {
            return "action.interact.saddle";
        }
        if (!getDataFlag(DATA_FLAG_CHESTED) && isTamed() && player.getInventory().getItemInHand().is(ItemBlockID.CHEST)) {
            return "action.interact.attachchest";
        }
        if (!passengers.isEmpty()) {
            return "";
        }
        return isTamed() ? "action.interact.ride.horse" : "action.interact.mount";
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        if (isBaby()) {
            return false;
        }

        if (!isTamed() && item.is(Item.SADDLE)) {
            makeMad();
            return false;
        }

        if (player.isSneaking()) {
            openInventory(player);
            return false;
        }

        if (item.is(Item.SADDLE) && !isSaddled()) {
            Item saddle = item.clone();
            saddle.setCount(1);
            inventory.setItem(0, saddle);
            return true;
        }

        if (item.is(ItemBlockID.CHEST) && !getDataFlag(DATA_FLAG_CHESTED) && isTamed()) {
            setDataFlag(DATA_FLAG_CHESTED, true);
            inventory.setSize(1 + 15);
            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ARMOR_EQUIP_GENERIC);
            return true;
        }

        if (passengers.isEmpty()) {
            mountEntity(player);
        }
        return false;
    }

    public void onTamed() {
        onTamed(true);
    }

    public void onTamed(boolean send) {
        setDataProperty(new ByteEntityData(DATA_CONTAINER_TYPE, inventory.getType().getNetworkType()), send);
        setDataProperty(new IntEntityData(DATA_CONTAINER_BASE_SIZE, 1 + 15), send);
    }
}
