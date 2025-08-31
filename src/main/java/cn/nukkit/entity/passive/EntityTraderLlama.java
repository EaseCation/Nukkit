package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFullNames;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.entity.attribute.Attribute;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.inventory.HorseInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.UpdateAttributesPacket;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class EntityTraderLlama extends EntityAnimal implements EntityInteractable, EntityRideable, InventoryHolder {
    public static final int NETWORK_ID = EntityID.TRADER_LLAMA;

    protected HorseInventory inventory;

    public EntityTraderLlama(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Trader Llama";
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 1.87f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        dataProperties.putInt(DATA_MAX_STRENGTH, 5);
        dataProperties.putInt(DATA_STRENGTH, 1); // TODO: 1-5

        int variant;
        if (namedTag.contains("Variant")) {
            variant = namedTag.getInt("Variant");
        } else {
            variant = ThreadLocalRandom.current().nextInt(4);
        }
        dataProperties.putInt(DATA_VARIANT, variant);
        dataProperties.putInt(DATA_MARK_VARIANT, namedTag.getInt("MarkVariant", EntityLlama.LLAMA_MARK_TRADER));

        inventory = new HorseInventory(this);
        ListTag<CompoundTag> items = namedTag.getList("Items", (ListTag<CompoundTag>) null);
        if (items == null) {
            namedTag.putList(new ListTag<>("Items"));
        } else {
            Int2ObjectMap<Item> slots = inventory.getContentsUnsafe();
            Iterator<CompoundTag> iter = items.iterator();
            while (iter.hasNext()) {
                CompoundTag tag = iter.next();

                int slot = tag.getByte("Slot");
                if (slot < 0 || slot >= inventory.getSize()) {
                    iter.remove();
                    continue;
                }

                Item item = NBTIO.getItemHelper(tag);
                if (item.isNull()) {
                    iter.remove();
                    continue;
                }

                slots.put(slot, item);
            }
        }

        boolean tamed = namedTag.getBoolean("IsTamed");
        if (tamed) {
            onTamed(false);
        }
        setDataFlag(DATA_FLAG_TAMED, tamed, false);

        boolean chested = namedTag.getBoolean("Chested");
        if (chested) {
            inventory.setSize(1 + getDataPropertyInt(DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH) * getDataPropertyInt(DATA_STRENGTH));
        } else {
            inventory.setSize(1);
        }
        setDataFlag(DATA_FLAG_CHESTED, chested, false);

        movementSpeed = 0.25f;

        this.setMaxHealth(15);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt("Variant", getDataPropertyInt(DATA_VARIANT));
        namedTag.putInt("MarkVariant", getDataPropertyInt(DATA_MARK_VARIANT));

        ListTag<CompoundTag> items = new ListTag<>("Items");
        if (inventory != null) {
            Int2ObjectMap<Item> slots = inventory.getContentsUnsafe();
            for (Int2ObjectMap.Entry<Item> entry : slots.int2ObjectEntrySet()) {
                int slot = entry.getIntKey();
                if (slot < 0 || slot >= inventory.getSize()) {
                    continue;
                }

                Item item = entry.getValue();
                if (item == null || item.isNull()) {
                    continue;
                }

                items.add(NBTIO.putItemHelper(item, slot));
            }
        }
        namedTag.putList(items);

        namedTag.putBoolean("IsTamed", getDataFlag(DATA_FLAG_TAMED));
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
    protected DataPacket createAddEntityPacket() {
        AddEntityPacket addEntity = new AddEntityPacket();
        addEntity.type = this.getNetworkId();
        addEntity.entityUniqueId = this.getId();
        addEntity.entityRuntimeId = this.getId();
        addEntity.yaw = (float) this.yaw;
        addEntity.headYaw = (float) this.yaw;
        addEntity.pitch = (float) this.pitch;
        addEntity.x = (float) this.x;
        addEntity.y = (float) this.y + this.getBaseOffset();
        addEntity.z = (float) this.z;
        addEntity.speedX = (float) this.motionX;
        addEntity.speedY = (float) this.motionY;
        addEntity.speedZ = (float) this.motionZ;
        addEntity.metadata = this.dataProperties;
        Pair<Int2IntMap, Int2FloatMap> propertyValues = getProperties().getValues();
        if (propertyValues != null) {
            addEntity.intProperties = propertyValues.left();
            addEntity.floatProperties = propertyValues.right();
        }

        int maxHealth = getMaxHealth();
        addEntity.attributes = new Attribute[]{
                Attribute.getAttribute(Attribute.HEALTH).setMaxValue(maxHealth).setDefaultMaxValue(maxHealth).setDefaultValue(maxHealth).setValue(getHealth()),
        };

        return addEntity;
    }

    @Override
    public boolean setHealth(float health) {
        if (!super.setHealth(health)) {
            return false;
        }

        UpdateAttributesPacket packet = new UpdateAttributesPacket();
        packet.entityId = getId();
        int maxHealth = getMaxHealth();
        packet.entries = new Attribute[]{
                Attribute.getAttribute(Attribute.HEALTH).setMaxValue(maxHealth).setDefaultMaxValue(maxHealth).setDefaultValue(maxHealth).setValue(getHealth()),
        };
        Server.broadcastPacket(getViewers().values(), packet);
        return true;
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);

        if (getHealth() > maxHealth) {
            this.health = maxHealth;
        }

        UpdateAttributesPacket packet = new UpdateAttributesPacket();
        packet.entityId = getId();
        int maximumHealth = getMaxHealth();
        packet.entries = new Attribute[]{
                Attribute.getAttribute(Attribute.HEALTH).setMaxValue(maximumHealth).setDefaultMaxValue(maximumHealth).setDefaultValue(maximumHealth).setValue(getHealth()),
        };
        Server.broadcastPacket(getViewers().values(), packet);
    }

    @Override
    public Item[] getDrops() {
        return Stream.concat(
                Stream.of(
                        Item.get(Item.LEATHER, 0, ThreadLocalRandom.current().nextInt(3)),
                        Item.get(ItemBlockID.CHEST, 0, getDataFlag(DATA_FLAG_CHESTED) ? 1 : 0)
                ),
                Arrays.stream(inventory.getContents().values().toArray(new Item[0]))
        ).toArray(Item[]::new);
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        if (entity.getNetworkId() == -1) {
            return new Vector3f(0, 2.3900099f, -0.3f);
        }
        return new Vector3f(0, 1.17f + entity.getRidingOffset(), -0.3f);
    }

    @Override
    public void kill() {
        super.kill();
        inventory.clearAll();
    }

    @Override
    public boolean canDoInteraction(Player player) {
        if (player.isSneaking()) {
            return getDataFlag(DATA_FLAG_TAMED);
        }
        if (getDataFlag(DATA_FLAG_TAMED)) {
            if (!getDataFlag(DATA_FLAG_CHESTED) && player.getInventory().getItemInHand().is(ItemBlockID.CHEST)) {
                return true;
            }
            if (inventory.getItem(0).isNull() && player.getInventory().getItemInHand().is(ItemBlockID.CARPET)) {
                return true;
            }
        }
        return passengers.isEmpty();
    }

    @Override
    public String getInteractButtonText(Player player) {
        if (player.isSneaking()) {
            return getDataFlag(DATA_FLAG_TAMED) ? "action.interact.opencontainer" : "";
        }
        if (getDataFlag(DATA_FLAG_TAMED)) {
            if (!getDataFlag(DATA_FLAG_CHESTED) && player.getInventory().getItemInHand().is(ItemBlockID.CHEST)) {
                return "action.interact.attachchest";
            }
            if (inventory.getItem(0).isNull() && player.getInventory().getItemInHand().is(ItemBlockID.CARPET)) {
                return "action.interact.equipcarpet";
            }
        }
        if (!passengers.isEmpty()) {
            return "";
        }
        return getDataFlag(DATA_FLAG_TAMED) ? "action.interact.ride.horse" : "action.interact.mount";
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        if (player.isSneaking()) {
            openInventory(player);
            return false;
        }

        if (getDataFlag(DATA_FLAG_TAMED)) {
            if (!getDataFlag(DATA_FLAG_CHESTED) && item.is(ItemBlockID.CHEST)) {
                setDataFlag(DATA_FLAG_CHESTED, true);
                inventory.setSize(1 + getDataPropertyInt(DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH) * getDataPropertyInt(DATA_STRENGTH));
                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ARMOR_EQUIP_GENERIC);
                return true;
            }

            if (item.is(ItemBlockID.CARPET) && inventory.getItem(0).isNull()) {
                Item carpet = item.clone();
                carpet.setCount(1);
                inventory.setItem(0, carpet);
                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ARMOR_EQUIP_GENERIC, EntityFullNames.TRADER_LLAMA);
                return true;
            }
        }

        if (passengers.isEmpty()) {
            mountEntity(player);
        }
        return false;
    }

    @Override
    public void openInventory(Player player) {
        if (!getDataFlag(DATA_FLAG_TAMED)) {
            return;
        }

        player.addWindow(getInventory());
    }

    @Override
    public HorseInventory getInventory() {
        return inventory;
    }

    public void onTamed() {
        onTamed(true);
    }

    public void onTamed(boolean send) {
        setDataProperty(new ByteEntityData(DATA_CONTAINER_TYPE, inventory.getType().getNetworkType()), send);
        setDataProperty(new IntEntityData(DATA_CONTAINER_BASE_SIZE, 1 + 15), send);
        setDataProperty(new IntEntityData(DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH, 3), send);
    }
}
