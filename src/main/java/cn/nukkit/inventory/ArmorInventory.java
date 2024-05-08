package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityArmorChangeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.MobArmorEquipmentPacket;
import cn.nukkit.network.protocol.types.ContainerIds;

public class ArmorInventory extends BaseInventory {
    public static final int SLOT_HEAD = 0;
    public static final int SLOT_TORSO = 1;
    public static final int SLOT_LEGS = 2;
    public static final int SLOT_FEET = 3;

    private final Entity entity;

    public ArmorInventory(Entity entity) {
        super((InventoryHolder) entity, InventoryType.ENTITY_ARMOR);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public void setSize(int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onSlotChange(int index, Item before, Item after, boolean send) {
        if (entity instanceof Player player && !player.spawned) {
            return;
        }

        sendContents(viewers);
        sendContents(entity.getViewers().values());

        int sound;
        if (after != null && (sound = after.getEquippingSound()) != -1 && !after.equals(before, false)) {
            entity.level.addLevelSoundEvent(entity, sound);
        }
    }

    @Override
    public void sendContents(Player... players) {
        Item[] items = new Item[4];
        for (int i = 0; i < 4; i++) {
            items[i] = getItem(i);
        }

        for (Player player : players) {
            if (player == holder) {
                InventoryContentPacket packet = new InventoryContentPacket();
                packet.inventoryId = ContainerIds.ARMOR;
                packet.slots = items;
                player.dataPacket(packet);
            } else {
                MobArmorEquipmentPacket packet = new MobArmorEquipmentPacket();
                packet.eid = entity.getId();
                packet.slots = items;
                player.dataPacket(packet);
            }
        }
    }

    @Override
    public void sendSlot(int index, Player... players) {
        Item[] items = new Item[4];
        for (int i = 0; i < 4; i++) {
            items[i] = getItem(i);
        }

        for (Player player : players) {
            if (player == holder) {
                InventorySlotPacket packet = new InventorySlotPacket();
                packet.inventoryId = ContainerIds.ARMOR;
                packet.slot = index;
                packet.item = items[index];
                player.dataPacket(packet);
            } else {
                MobArmorEquipmentPacket packet = new MobArmorEquipmentPacket();
                packet.eid = entity.getId();
                packet.slots = items;
                player.dataPacket(packet);
            }
        }
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        return setItem(index, item, send, false);
    }

    public boolean setItem(int index, Item item, boolean send, boolean ignoreEvent) {
        if (index < 0 || index >= size) {
            return false;
        }
        if (item.isNull()) {
            return clear(index);
        }

        if (!ignoreEvent) {
            EntityArmorChangeEvent event = new EntityArmorChangeEvent(entity, getItem(index), item, index);
            event.call();
            if (event.isCancelled()) {
                sendSlot(index, viewers);
                return false;
            }
            item = event.getNewItem();
        }

        Item old = getItem(index);
        Item newItem = item.clone();
        slots.put(index, newItem);

        onSlotChange(index, old, newItem, send);
        return true;
    }

    @Override
    public boolean clear(int index, boolean send) {
        Item old = slots.get(index);
        if (old == null) {
            return true;
        }

        Item item = Items.air();
        EntityArmorChangeEvent event = new EntityArmorChangeEvent(entity, old, item, index);
        event.call();
        if (event.isCancelled()) {
            sendSlot(index, viewers);
            return false;
        }
        item = event.getNewItem();

        Item newItem;
        if (!item.isNull()) {
            newItem = item.clone();
            slots.put(index, newItem);
        } else {
            newItem = null;
            slots.remove(index);
        }

        onSlotChange(index, old, newItem, send);
        return true;
    }

    public Item getHelmet(){
        return getItem(SLOT_HEAD);
    }

    public Item getChestplate(){
        return getItem(SLOT_TORSO);
    }

    public Item getLeggings(){
        return getItem(SLOT_LEGS);
    }

    public Item getBoots(){
        return getItem(SLOT_FEET);
    }

    public boolean setHelmet(Item item) {
        return setHelmet(item, false);
    }

    public boolean setHelmet(Item item, boolean ignoreEvent) {
        return setItem(SLOT_HEAD, item, true, ignoreEvent);
    }

    public boolean setChestplate(Item item) {
        return setChestplate(item, false);
    }

    public boolean setChestplate(Item item, boolean ignoreEvent) {
        return setItem(SLOT_TORSO, item, true, ignoreEvent);
    }

    public boolean setLeggings(Item item) {
        return setLeggings(item, false);
    }

    public boolean setLeggings(Item item, boolean ignoreEvent) {
        return setItem(SLOT_LEGS, item, true, ignoreEvent);
    }

    public boolean setBoots(Item item) {
        return setBoots(item, false);
    }

    public boolean setBoots(Item item, boolean ignoreEvent) {
        return setItem(SLOT_FEET, item, true, ignoreEvent);
    }
}
