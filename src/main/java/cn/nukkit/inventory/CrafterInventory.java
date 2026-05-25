package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityCrafter;
import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class CrafterInventory extends ContainerInventory {
    public CrafterInventory(BlockEntityCrafter blockEntity) {
        super(blockEntity, InventoryType.CRAFTER);
    }

    @Override
    public BlockEntityCrafter getHolder() {
        return (BlockEntityCrafter) super.getHolder();
    }

    @Override
    public int firstEmpty(Item item) {
        for (int i = 0; i < this.size; ++i) {
            if (getHolder().isSlotDisabled(i)) {
                continue;
            }
            if (this.getItem(i).isNull()) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public boolean canAddItem(Item item) {
        item = item.clone();
        boolean checkDamage = item.hasMeta();
        boolean checkTag = item.getCompoundTag() != null;
        for (int i = 0; i < this.getSize(); ++i) {
            if (getHolder().isSlotDisabled(i)) {
                continue;
            }
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

        for (int i = 0; i < this.getSize(); ++i) {
            if (itemSlots.isEmpty()) {
                break;
            }
            if (getHolder().isSlotDisabled(i)) {
                continue;
            }

            Item item = this.getItem(i);
            if (!item.isNull()) {
                continue;
            }

            Item slot = itemSlots.getFirst();
            int count = Math.min(slot.getMaxStackSize(), slot.getCount());
            count = Math.min(count, this.getMaxStackSize());
            slot.setCount(slot.getCount() - count);
            item = slot.clone();
            item.setCount(count);
            this.setItem(i, item);
            if (slot.getCount() <= 0) {
                itemSlots.remove(slot);
            }
        }

        for (Item slot : new ObjectArrayList<>(itemSlots)) {
            while (slot.getCount() > 0) {
                int targetSlot = this.findLeastSlot(slot);
                if (targetSlot < 0) {
                    break;
                }

                Item item = this.getItem(targetSlot);
                int count = Math.min(item.getMaxStackSize() - item.getCount(), slot.getCount());
                count = Math.min(count, this.getMaxStackSize());
                slot.setCount(slot.getCount() - count);
                item.setCount(item.getCount() + count);
                this.setItem(targetSlot, item);
            }

            if (slot.getCount() <= 0) {
                itemSlots.remove(slot);
            }
        }

        return itemSlots.toArray(new Item[0]);
    }

    private int findLeastSlot(Item target) {
        int result = -1;
        int count = Integer.MAX_VALUE;

        for (int i = 0; i < this.getSize(); ++i) {
            if (getHolder().isSlotDisabled(i)) {
                continue;
            }

            Item item = this.getItem(i);
            if (target.equals(item) && item.getCount() < item.getMaxStackSize() && item.getCount() < count) {
                result = i;
                count = item.getCount();
            }
        }

        return result;
    }

    @Override
    public boolean isFull() {
        if (this.slots.size() < this.getSize()) {
            return false;
        }

        for (int i = 0; i < this.getSize(); ++i) {
            if (getHolder().isSlotDisabled(i)) {
                continue;
            }
            Item item = this.getItem(i);
            if (item.is(Item.AIR) || item.getCount() < item.getMaxStackSize() || item.getCount() < this.getMaxStackSize()) {
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

        for (int i = 0; i < this.getSize(); ++i) {
            if (getHolder().isSlotDisabled(i)) {
                continue;
            }
            Item item = this.getItem(i);
            if (!item.isNull()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int getFreeSpace(Item item) {
        int maxStackSize = Math.min(item.getMaxStackSize(), this.getMaxStackSize());
        int space = (this.getSize() - this.slots.size()) * maxStackSize;

        for (int i = 0; i < this.getSize(); ++i) {
            if (getHolder().isSlotDisabled(i)) {
                continue;
            }
            Item slot = this.getItem(i);
            if (slot.is(Item.AIR)) {
                space += maxStackSize;
                continue;
            }

            if (slot.equals(item, true, true)) {
                space += maxStackSize - slot.getCount();
            }
        }

        return space;
    }
}
