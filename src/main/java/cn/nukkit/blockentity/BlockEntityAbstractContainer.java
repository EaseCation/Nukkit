package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Iterator;

public abstract class BlockEntityAbstractContainer extends BlockEntitySpawnable implements BlockEntityContainer, InventoryHolder, BlockEntityNameable {
    protected BlockEntityAbstractContainer(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        ListTag<CompoundTag> items = namedTag.getList("Items", (ListTag<CompoundTag>) null);
        if (items == null) {
            namedTag.putList(new ListTag<>("Items"));
        } else {
            Int2ObjectMap<Item> slots = getRealInventory().getContentsUnsafe();
            Iterator<CompoundTag> iter = items.iterator();
            while (iter.hasNext()) {
                CompoundTag tag = iter.next();

                int slot = tag.getByte("Slot");
                if (slot < 0 || slot >= getSize()) {
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

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        Int2ObjectMap<Item> slots = getRealInventory().getContentsUnsafe();
        ListTag<CompoundTag> items = new ListTag<>("Items");
        for (Int2ObjectMap.Entry<Item> entry : slots.int2ObjectEntrySet()) {
            int slot = entry.getIntKey();
            if (slot < 0 || slot >= getSize()) {
                continue;
            }

            Item item = entry.getValue();
            if (item == null || item.isNull()) {
                continue;
            }

            items.add(NBTIO.putItemHelper(item, slot));
        }
        namedTag.putList(items);
    }

    @Override
    public void onBreak() {
        Inventory inventory = getRealInventory();

        for (Item content : inventory.getContents().values()) {
            level.dropItem(this, content);
        }

        inventory.clearAll();
    }

    @Override
    public void close() {
        if (isClosed()) {
            return;
        }

        Inventory inventory = getRealInventory();
        for (Player player : new ObjectArrayList<>(inventory.getViewers())) {
            player.removeWindow(inventory);
        }

        super.close();
    }

    @Override
    public Item getItem(int index) {
        for (CompoundTag item : namedTag.getList("Items", CompoundTag.class)) {
            if (item.getByte("Slot") == index) {
                return NBTIO.getItemHelper(item);
            }
        }

        return Items.air();
    }

    @Override
    public void setItem(int index, Item item) {
        ListTag<CompoundTag> items = namedTag.getList("Items", CompoundTag.class);

        for (int i = 0; i < items.size(); i++) {
            CompoundTag tag = items.get(i);
            if (tag.getByte("Slot") == index) {
                if (item.isNull()) {
                    items.remove(i);
                } else {
                    items.add(i, NBTIO.putItemHelper(item, index));
                }
                break;
            }
        }

        if (!item.isNull()) {
            items.add(NBTIO.putItemHelper(item, index));
        }
    }

    public Inventory getRealInventory() {
        return getInventory();
    }
}
