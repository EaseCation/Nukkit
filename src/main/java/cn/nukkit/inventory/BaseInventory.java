package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.Items;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.types.ContainerIds;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.log4j.Log4j2;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public abstract class BaseInventory implements Inventory {

    protected final InventoryType type;

    protected int maxStackSize = Inventory.MAX_STACK;

    protected int size;

    protected final String name;

    protected final String title;

    public final Int2ObjectMap<Item> slots = new Int2ObjectOpenHashMap<>();

    protected final Set<Player> viewers = new HashSet<>();

    protected InventoryHolder holder;

    public BaseInventory(InventoryHolder holder, InventoryType type) {
        this(holder, type, new Int2ObjectOpenHashMap<>());
    }

    public BaseInventory(InventoryHolder holder, InventoryType type, Map<Integer, Item> items) {
        this(holder, type, items, null);
    }

    public BaseInventory(InventoryHolder holder, InventoryType type, Map<Integer, Item> items, Integer overrideSize) {
        this(holder, type, items, overrideSize, null);
    }

    public BaseInventory(InventoryHolder holder, InventoryType type, Map<Integer, Item> items, Integer overrideSize, String overrideTitle) {
        this.holder = holder;

        this.type = type;

        if (overrideSize != null) {
            this.size = overrideSize;
        } else {
            this.size = this.type.getDefaultSize();
        }

        if (overrideTitle != null) {
            this.title = overrideTitle;
        } else {
            this.title = this.type.getDefaultTitle();
        }

        this.name = this.type.getDefaultTitle();

        if (!(this instanceof DoubleChestInventory)) {
            this.setContents(items);
        }
    }

    @Override
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Item getItem(int index) {
        Item item = this.slots.get(index);
        if (item == null) {
            return Item.get(ItemID.AIR);
        }
        return item.clone();
    }

    @Override
    public Int2ObjectMap<Item> getContents() {
        return new Int2ObjectOpenHashMap<>(this.slots);
    }

    @Override
    public Int2ObjectMap<Item> getContentsUnsafe() {
        return this.slots;
    }

    @Override
    public void setContents(Map<Integer, Item> items) {
        if (items.isEmpty()) {
//            this.clearAll();
            for (int i = 0; i < this.size; ++i) {
                this.clear(i);
            }
            return;
        }

        for (int i = 0; i < this.size; ++i) {
            Item item = items.get(i);
            if (item == null || !this.setItem(i, item)) {
                this.clear(i);
            }
        }
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        item = item.clone();
        if (index < 0 || index >= this.size) {
            return false;
        }
        if (item.isNull()) {
            return this.clear(index, send);
        }

        InventoryHolder holder = this.getHolder();
        if (holder instanceof Entity) {
            EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent((Entity) holder, this.getItem(index), item, index);
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
    public boolean contains(Item item) {
        int count = Math.max(1, item.getCount());
        boolean checkDamage = item.hasMeta() && item.getDamage() >= 0;
        boolean checkTag = item.getCompoundTag() != null;
        for (Item i : this.getContents().values()) {
            if (item.equals(i, checkDamage, checkTag)) {
                count -= i.getCount();
                if (count <= 0) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Map<Integer, Item> all(Item item) {
        Int2ObjectMap<Item> slots = new Int2ObjectOpenHashMap<>();
        boolean checkDamage = item.hasMeta() && item.getDamage() >= 0;
        boolean checkTag = item.getCompoundTag() != null;
        for (Int2ObjectMap.Entry<Item> entry : this.getContents().int2ObjectEntrySet()) {
            if (item.equals(entry.getValue(), checkDamage, checkTag)) {
                slots.put(entry.getIntKey(), entry.getValue());
            }
        }

        return slots;
    }

    @Override
    public void remove(Item item) {
        boolean checkDamage = item.hasMeta();
        boolean checkTag = item.getCompoundTag() != null;
        for (Int2ObjectMap.Entry<Item> entry : this.getContents().int2ObjectEntrySet()) {
            if (item.equals(entry.getValue(), checkDamage, checkTag)) {
                this.clear(entry.getIntKey());
            }
        }
    }

    @Override
    public int first(Item item, boolean exact) {
        int count = Math.max(1, item.getCount());
        boolean checkDamage = item.hasMeta();
        boolean checkTag = item.getCompoundTag() != null;
        for (Int2ObjectMap.Entry<Item> entry : this.getContents().int2ObjectEntrySet()) {
            if (item.equals(entry.getValue(), checkDamage, checkTag) && (entry.getValue().getCount() == count || (!exact && entry.getValue().getCount() > count))) {
                return entry.getIntKey();
            }
        }

        return -1;
    }

    @Override
    public int firstEmpty(Item item) {
        for (int i = 0; i < this.size; ++i) {
            if (this.getItem(i).isNull()) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void decreaseCount(int slot) {
        Item item = this.getItem(slot);

        if (item.getCount() > 0) {
            item.count--;
            this.setItem(slot, item);
        }
    }

    @Override
    public boolean canAddItem(Item item) {
        item = item.clone();
        boolean checkDamage = item.hasMeta();
        boolean checkTag = item.getCompoundTag() != null;
        for (int i = 0; i < this.getSize(); ++i) {
            Item slot = this.getItem(i);
            if (item.equals(slot, checkDamage, checkTag)) {
                int diff;
                if ((diff = slot.getMaxStackSize() - slot.getCount()) > 0) {
                    item.setCount(item.getCount() - diff);
                }
            } else if (slot.isNull()) {
                item.setCount(item.getCount() - this.getMaxStackSize());
            }

            if (item.getCount() <= 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Item[] addItem(Item... slots) {
        List<Item> itemSlots = new ObjectArrayList<>();
        for (Item slot : slots) {
            if (!slot.isNull()) {
                itemSlots.add(slot.clone());
            }
        }

        IntList emptySlots = new IntArrayList();

        for (int i = 0; i < this.getSize(); ++i) {
            Item item = this.getItem(i);
            if (item.isNull()) {
                emptySlots.add(i);
            }

            for (Item slot : new ObjectArrayList<>(itemSlots)) {
                if (slot.equals(item) && item.getCount() < item.getMaxStackSize()) {
                    int amount = Math.min(item.getMaxStackSize() - item.getCount(), slot.getCount());
                    amount = Math.min(amount, this.getMaxStackSize());
                    if (amount > 0) {
                        slot.setCount(slot.getCount() - amount);
                        item.setCount(item.getCount() + amount);
                        this.setItem(i, item);
                        if (slot.getCount() <= 0) {
                            itemSlots.remove(slot);
                        }
                    }
                }
            }

            if (itemSlots.isEmpty()) {
                break;
            }
        }

        if (!itemSlots.isEmpty() && !emptySlots.isEmpty()) {
            for (int slotIndex : emptySlots) {
                if (!itemSlots.isEmpty()) {
                    Item slot = itemSlots.get(0);
                    int amount = Math.min(slot.getMaxStackSize(), slot.getCount());
                    amount = Math.min(amount, this.getMaxStackSize());
                    slot.setCount(slot.getCount() - amount);
                    Item item = slot.clone();
                    item.setCount(amount);
                    this.setItem(slotIndex, item);
                    if (slot.getCount() <= 0) {
                        itemSlots.remove(slot);
                    }
                }
            }
        }

        return itemSlots.toArray(new Item[0]);
    }

    @Override
    public Item[] removeItem(Item... slots) {
        List<Item> itemSlots = new ObjectArrayList<>();
        for (Item slot : slots) {
            if (slot.getId() != 0 && slot.getCount() > 0) {
                itemSlots.add(slot.clone());
            }
        }

        for (int i = 0; i < this.size; ++i) {
            Item item = this.getItem(i);
            if (item.isNull()) {
                continue;
            }

            for (Item slot : new ObjectArrayList<>(itemSlots)) {
                if (slot.equals(item, item.hasMeta(), item.getCompoundTag() != null)) {
                    int amount = Math.min(item.getCount(), slot.getCount());
                    slot.setCount(slot.getCount() - amount);
                    item.setCount(item.getCount() - amount);
                    this.setItem(i, item);
                    if (slot.getCount() <= 0) {
                        itemSlots.remove(slot);
                    }

                }
            }

            if (itemSlots.isEmpty()) {
                break;
            }
        }

        return itemSlots.toArray(new Item[0]);
    }

    @Override
    public boolean clear(int index, boolean send) {
        Item old = this.slots.get(index);
        if (old != null) {
            Item item = Items.air();
            InventoryHolder holder = this.getHolder();
            if (holder instanceof Entity) {
                EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent((Entity) holder, old, item, index);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    this.sendSlot(index, this.getViewers());
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

    @Override
    public void clearAll() {
        for (int index : this.getContents().keySet()) {
            this.clear(index);
        }
    }

    @Override
    public Set<Player> getViewers() {
        return viewers;
    }

    @Override
    public InventoryHolder getHolder() {
        return holder;
    }

    @Override
    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    @Override
    public boolean open(Player who) {
        InventoryOpenEvent ev = new InventoryOpenEvent(this, who);
        who.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }
        this.onOpen(who);

        return true;
    }

    @Override
    public void close(Player who) {
        this.onClose(who);
    }

    @Override
    public void onOpen(Player who) {
        this.viewers.add(who);
    }

    @Override
    public void onClose(Player who) {
        this.viewers.remove(who);
    }

    @Override
    public void onSlotChange(int index, Item before, Item after, boolean send) {
        if (send) {
            this.sendSlot(index, this.getViewers());
        }

        if (holder instanceof BlockEntity) {
            ((BlockEntity) holder).setDirty();
        }
    }

    @Override
    public void sendContents(Player player) {
        this.sendContents(new Player[]{player});
    }

    @Override
    public void sendContents(Player... players) {
        Item[] slots = new Item[this.getSize()];
        for (int i = 0; i < this.getSize(); ++i) {
            slots[i] = this.getItem(i);
        }

        for (Player player : players) {
            int id = player.getWindowId(this);
            if (id == ContainerIds.NONE || !player.spawned) {
                this.close(player);
                continue;
            }
            InventoryContentPacket pk = new InventoryContentPacket();
            pk.slots = slots;
            pk.inventoryId = id;
            player.dataPacket(pk);
        }
    }

    @Override
    public boolean isFull() {
        if (this.slots.size() < this.getSize()) {
            return false;
        }

        for (Item item : this.slots.values()) {
            if (item == null || item.getId() == 0 || item.getCount() < item.getMaxStackSize() || item.getCount() < this.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isEmpty() {
        if (this.getMaxStackSize() <= 0) {
            return false;
        }

        for (Item item : this.slots.values()) {
            if (item != null && item.getId() != 0 && item.getCount() > 0) {
                return false;
            }
        }

        return true;
    }

    public int getFreeSpace(Item item) {
        int maxStackSize = Math.min(item.getMaxStackSize(), this.getMaxStackSize());
        int space = (this.getSize() - this.slots.size()) * maxStackSize;

        for (Item slot : this.getContents().values()) {
            if (slot == null || slot.getId() == 0) {
                space += maxStackSize;
                continue;
            }

            if (slot.equals(item, true, true)) {
                space += maxStackSize - slot.getCount();
            }
        }

        return space;
    }

    @Override
    public void sendContents(Collection<Player> players) {
        this.sendContents(players.toArray(new Player[0]));
    }

    @Override
    public void sendSlot(int index, Player player) {
        this.sendSlot(index, new Player[]{player});
    }

    @Override
    public void sendSlot(int index, Player... players) {
        Item item = this.getItem(index);

        for (Player player : players) {
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

    @Override
    public void sendSlot(int index, Collection<Player> players) {
        this.sendSlot(index, players.toArray(new Player[0]));
    }

    @Override
    public InventoryType getType() {
        return type;
    }

    @Override
    public Item peek(Item target) {
        boolean checkAux = target.hasMeta() && target.getDamage() >= 0;
        boolean checkNbt = target.getCompoundTag() != null;

        for (int i = 0; i < this.getSize(); i++) {
            Item item = this.getItem(i);
            if (item.isNull()) {
                continue;
            }

            if (target.equals(item, checkAux, checkNbt)) {
                return item;
            }
        }

        return Items.air();
    }
}
