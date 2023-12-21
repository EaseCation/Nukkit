package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.EntityHumanType;
import cn.nukkit.event.entity.EntityArmorChangeEvent;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.ContainerIds;

import java.util.Collection;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlayerInventory extends BaseInventory {

    protected int itemInHandIndex = 0;

    public PlayerInventory(EntityHumanType player) {
        super(player, InventoryType.PLAYER);
    }

    @Override
    public int getSize() {
        return super.getSize() - 4;
    }

    @Override
    public void setSize(int size) {
        super.setSize(size + 4);
        this.sendContents(this.getViewers());
    }

    /**
     * Called when a client equips a hotbar inventorySlot. This method should not be used by plugins.
     * This method will call PlayerItemHeldEvent.
     *
     * @param slot hotbar slot Number of the hotbar slot to equip.
     * @return boolean if the equipment change was successful, false if not.
     */
    public boolean equipItem(int slot) {
        if (!isHotbarSlot(slot)) {
            this.sendContents((Player) this.getHolder());
            return false;
        }

        if (this.getHolder() instanceof Player) {
            Player player = (Player) this.getHolder();
            PlayerItemHeldEvent ev = new PlayerItemHeldEvent(player, this.getItem(slot), slot);
            this.getHolder().getLevel().getServer().getPluginManager().callEvent(ev);

            if (ev.isCancelled()) {
                this.sendContents(this.getViewers());
                return false;
            }

            if (player.fishing != null) {
                if (!(this.getItem(slot).equals(player.fishing.rod))) {
                    player.stopFishing(false);
                }
            }
        }

        this.setHeldItemIndex(slot, false);
        return true;
    }

    private boolean isHotbarSlot(int slot) {
        return slot >= 0 && slot <= this.getHotbarSize();
    }

    @Deprecated
    public int getHotbarSlotIndex(int index) {
        return index;
    }

    @Deprecated
    public void setHotbarSlotIndex(int index, int slot) {

    }

    public int getHeldItemIndex() {
        return this.itemInHandIndex;
    }

    public void setHeldItemIndex(int index) {
        setHeldItemIndex(index, true);
    }

    public void setHeldItemIndex(int index, boolean send) {
        if (index >= 0 && index < this.getHotbarSize()) {
            this.itemInHandIndex = index;

            if (this.getHolder() instanceof Player && send) {
                this.sendHeldItem((Player) this.getHolder());
            }

            this.sendHeldItem(this.getHolder().getViewers().values());
        }
    }

    public Item getItemInHand() {
        Item item = this.getItem(this.getHeldItemIndex());
        if (item != null) {
            return item;
        }
        return Items.air();
    }

    public boolean setItemInHand(Item item) {
        return this.setItem(this.getHeldItemIndex(), item);
    }

    @Deprecated
    public int getHeldItemSlot() {
        return this.itemInHandIndex;
    }

    public void setHeldItemSlot(int slot) {
        if (!isHotbarSlot(slot)) {
            return;
        }

        this.itemInHandIndex = slot;

        if (this.getHolder() instanceof Player) {
            this.sendHeldItem((Player) this.getHolder());
        }

        this.sendHeldItem(this.getViewers());
    }

    public void sendHeldItem(Player... players) {
        Item item = this.getItemInHand();
        long entityId = this.getHolder().getId();

        for (Player player : players) {
            if (player == this.getHolder()) {
                this.sendSlot(this.getHeldItemIndex(), player);

                MobEquipmentPacket pk0 = new MobEquipmentPacket();
                pk0.item = item;
                pk0.inventorySlot = pk0.hotbarSlot = this.getHeldItemIndex();
                pk0.eid = entityId;
                player.dataPacket(pk0);
                continue;
            }

            MobEquipmentPacket pk = new MobEquipmentPacket();
            pk.item = item;
            pk.inventorySlot = pk.hotbarSlot = this.getHeldItemIndex();
            pk.eid = entityId;
            player.dataPacket(pk);
        }
    }

    public void sendHeldItem(Collection<Player> players) {
        this.sendHeldItem(players.toArray(new Player[0]));
    }

    @Override
    public void onSlotChange(int index, Item before, Item after, boolean send) {
        EntityHuman holder = this.getHolder();
        if (holder instanceof Player && !((Player) holder).spawned) {
            return;
        }

        if (index >= this.getSize()) {
            this.sendArmorSlot(index, this.getViewers());
            this.sendArmorSlot(index, this.getHolder().getViewers().values());

            int sound;
            if (after != null && (sound = after.getEquippingSound()) != -1 && !after.equals(before, false)) {
                holder.level.addLevelSoundEvent(holder, sound);
            }
        } else {
            super.onSlotChange(index, before, after, send);
        }
    }

    public int getHotbarSize() {
        return 9;
    }

    public Item getArmorItem(int index) {
        return this.getItem(this.getSize() + index);
    }

    public boolean setArmorItem(int index, Item item) {
        return this.setArmorItem(index, item, false);
    }

    public boolean setArmorItem(int index, Item item, boolean ignoreArmorEvents) {
        return this.setItem(this.getSize() + index, item, true, ignoreArmorEvents);
    }

    public Item getHelmet() {
        return this.getItem(this.getSize());
    }

    public Item getChestplate() {
        return this.getItem(this.getSize() + 1);
    }

    public Item getLeggings() {
        return this.getItem(this.getSize() + 2);
    }

    public Item getBoots() {
        return this.getItem(this.getSize() + 3);
    }

    public boolean setHelmet(Item helmet) {
        return this.setHelmet(helmet, false);
    }

    public boolean setHelmet(Item helmet, boolean ignoreArmorEvents) {
        return this.setItem(this.getSize(), helmet, true, ignoreArmorEvents);
    }

    public boolean setChestplate(Item chestplate) {
        return this.setChestplate(chestplate, false);
    }

    public boolean setChestplate(Item chestplate, boolean ignoreArmorEvents) {
        return this.setItem(this.getSize() + 1, chestplate, true, ignoreArmorEvents);
    }

    public boolean setLeggings(Item leggings) {
        return this.setLeggings(leggings, false);
    }

    public boolean setLeggings(Item leggings, boolean ignoreArmorEvents) {
        return this.setItem(this.getSize() + 2, leggings, true, ignoreArmorEvents);
    }

    public boolean setBoots(Item boots) {
        return this.setBoots(boots, false);
    }

    public boolean setBoots(Item boots, boolean ignoreArmorEvents) {
        return this.setItem(this.getSize() + 3, boots, true, ignoreArmorEvents);
    }

    @Override
    public boolean setItem(int index, Item item) {
        return setItem(index, item, true, false);
    }

    private boolean setItem(int index, Item item, boolean send, boolean ignoreArmorEvents) {
        if (index < 0 || index >= this.size) {
            return false;
        }
        if (item.isNull()) {
            return this.clear(index);
        }

        //Armor change
        if (!ignoreArmorEvents && index >= this.getSize()) {
            EntityArmorChangeEvent ev = new EntityArmorChangeEvent(this.getHolder(), this.getItem(index), item, index);
            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled() && this.getHolder() != null) {
                this.sendArmorSlot(index, this.getViewers());
                return false;
            }
            item = ev.getNewItem();
        } else {
            EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent(this.getHolder(), this.getItem(index), item, index);
            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                this.sendSlot(index, this.getViewers());
                return false;
            }
            item = ev.getNewItem();
        }
        Item old = this.getItem(index);
        Item newItem = item.clone();
        this.slots.put(index, newItem);
        this.onSlotChange(index, old, newItem, send);
        return true;
    }

    @Override
    public boolean clear(int index, boolean send) {
        Item old = this.slots.get(index);
        if (old != null) {
            Item item = Items.air();
            if (index >= this.getSize() && index < this.size) {
                EntityArmorChangeEvent ev = new EntityArmorChangeEvent(this.getHolder(), old, item, index);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    if (index >= this.size) {
                        this.sendArmorSlot(index, this.getViewers());
                    } else {
                        this.sendSlot(index, this.getViewers());
                    }
                    return false;
                }
                item = ev.getNewItem();
            } else {
                EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent(this.getHolder(), old, item, index);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    if (index >= this.size) {
                        this.sendArmorSlot(index, this.getViewers());
                    } else {
                        this.sendSlot(index, this.getViewers());
                    }
                    return false;
                }
                item = ev.getNewItem();
            }

            Item newItem;
            if (!item.isNull()) {
                newItem = item.clone();
                this.slots.put(index, newItem);
            } else {
                newItem = null;
                this.slots.remove(index);
            }

            this.onSlotChange(index, old, newItem, send);
        }

        return true;
    }

    public Item[] getArmorContents() {
        Item[] armor = new Item[4];
        for (int i = 0; i < 4; i++) {
            armor[i] = this.getItem(this.getSize() + i);
        }

        return armor;
    }

    @Override
    public void clearAll() {
        int limit = this.getSize() + 4;
        for (int index = 0; index < limit; ++index) {
            this.clear(index);
        }
        getHolder().getOffhandInventory().clearAll();
    }

    public void sendArmorContents(Player player) {
        this.sendArmorContents(new Player[]{player});
    }

    public void sendArmorContents(Player[] players) {
        Item[] armor = this.getArmorContents();
        long entityId = this.getHolder().getId();

        for (Player player : players) {
            if (player == this.getHolder()) {
                InventoryContentPacket pk2 = new InventoryContentPacket();
                pk2.inventoryId = ContainerIds.ARMOR;
                pk2.slots = armor;
                player.dataPacket(pk2);
            } else {
                MobArmorEquipmentPacket pk = new MobArmorEquipmentPacket();
                pk.eid = entityId;
                pk.slots = armor;
                player.dataPacket(pk);
            }
        }
    }

    public void setArmorContents(Item[] items) {
        if (items.length < 4) {
            Item[] newItems = new Item[4];
            System.arraycopy(items, 0, newItems, 0, items.length);
            items = newItems;
        }

        for (int i = 0; i < 4; ++i) {
            if (items[i] == null) {
                items[i] = Items.air();
            }

            if (items[i].isNull()) {
                this.clear(this.getSize() + i);
            } else {
                this.setItem(this.getSize() + i, items[i]);
            }
        }
    }

    public void sendArmorContents(Collection<Player> players) {
        this.sendArmorContents(players.toArray(new Player[0]));
    }

    public void sendArmorSlot(int index, Player player) {
        this.sendArmorSlot(index, new Player[]{player});
    }

    public void sendArmorSlot(int index, Player[] players) {
        Item[] armor = this.getArmorContents();
        long entityId = this.getHolder().getId();

        for (Player player : players) {
            if (player == this.getHolder()) {
                InventorySlotPacket pk2 = new InventorySlotPacket();
                pk2.inventoryId = ContainerIds.ARMOR;
                pk2.slot = index - this.getSize();
                pk2.item = this.getItem(index);
                player.dataPacket(pk2);
            } else {
                MobArmorEquipmentPacket pk = new MobArmorEquipmentPacket();
                pk.eid = entityId;
                pk.slots = armor;
                player.dataPacket(pk);
            }
        }
    }

    public void sendArmorSlot(int index, Collection<Player> players) {
        this.sendArmorSlot(index, players.toArray(new Player[0]));
    }

    @Override
    public void sendContents(Player player) {
        this.sendContents(new Player[]{player});
    }

    @Override
    public void sendContents(Collection<Player> players) {
        this.sendContents(players.toArray(new Player[0]));
    }

    @Override
    public void sendContents(Player[] players) {
        Item[] slots = new Item[this.getSize()];
        for (int i = 0; i < this.getSize(); ++i) {
            slots[i] = this.getItem(i);
        }

        for (Player player : players) {
            int id = player.getWindowId(this);
            if (id == ContainerIds.NONE || !player.spawned) {
                if (this.getHolder() != player) this.close(player);
                continue;
            }
            InventoryContentPacket pk = new InventoryContentPacket();
            pk.slots = slots;
            pk.inventoryId = id;
            player.dataPacket(pk);
        }
    }

    @Override
    public void sendSlot(int index, Player player) {
        this.sendSlot(index, new Player[]{player});
    }

    @Override
    public void sendSlot(int index, Collection<Player> players) {
        this.sendSlot(index, players.toArray(new Player[0]));
    }

    @Override
    public void sendSlot(int index, Player... players) {
        Item item = this.getItem(index);

        for (Player player : players) {
            if (player == this.getHolder()) {
                InventorySlotPacket pk = new InventorySlotPacket();
                pk.slot = index;
                pk.item = item;
                pk.inventoryId = ContainerIds.INVENTORY;
                player.dataPacket(pk);
            } else {
                int id = player.getWindowId(this);
                if (id == ContainerIds.NONE) {
                    this.close(player);
                    continue;
                }
                InventorySlotPacket pk = new InventorySlotPacket();
                pk.slot = index;
                pk.item = item;
                pk.inventoryId = id;
                player.dataPacket(pk);
            }
        }
    }

    public void sendCreativeContents() {
        if (!(this.getHolder() instanceof Player)) {
            return;
        }
        Player p = (Player) this.getHolder();

        InventoryContentPacket pk = new InventoryContentPacket();
        pk.inventoryId = ContainerIds.CREATIVE;

        if (!p.isSpectator()) { //fill it for all gamemodes except spectator
            pk.slots = p.getCreativeItems().toArray(new Item[0]);
        }

        p.dataPacket(pk);
    }

    @Override
    public EntityHuman getHolder() {
        return (EntityHuman) super.getHolder();
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.windowId = who.getWindowId(this);
        pk.type = this.getType().getNetworkType();
        pk.x = who.getFloorX();
        pk.y = who.getFloorY();
        pk.z = who.getFloorZ();
        who.dataPacket(pk);
    }

    @Override
    public void onClose(Player who) {
        int windowId = who.getWindowId(this);
        if (windowId != ContainerIds.NONE) {
            ContainerClosePacket pk = new ContainerClosePacket();
            pk.windowId = windowId;
            who.dataPacket(pk);
        }
        // player can never stop viewing their own inventory
        if (who != holder) {
            super.onClose(who);
        }
    }

    public void addItemOrDrop(Item... slots) {
        for (Item drop : addItem(slots)) {
            EntityHuman holder = getHolder();
            holder.level.dropItem(holder, drop);
        }
    }
}
