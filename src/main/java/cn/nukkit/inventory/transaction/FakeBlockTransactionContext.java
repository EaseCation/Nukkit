package cn.nukkit.inventory.transaction;

import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.DoubleChestInventory;
import cn.nukkit.item.Item;

import javax.annotation.Nullable;

final class FakeBlockTransactionContext {
    @Nullable
    private final Inventory inventory;
    private final Item[] slots;
    private final Item[] identities;
    private final boolean[] trackIdentity;

    FakeBlockTransactionContext(@Nullable Inventory inventory) {
        this.inventory = inventory;
        slots = inventory == null ? new Item[0] : new Item[inventory.getSize()];
        identities = new Item[slots.length];
        trackIdentity = new boolean[slots.length];
        for (int index = 0; index < slots.length; index++) {
            slots[index] = inventory.getItem(index).clone();
            PhysicalSlot physical = resolve(inventory, index);
            if (physical != null) {
                trackIdentity[index] = true;
                identities[index] = physical.inventory().slots.get(physical.slot());
            }
        }
    }

    boolean isCurrent() {
        if (inventory == null || inventory.getSize() != slots.length) {
            return false;
        }
        for (int index = 0; index < slots.length; index++) {
            if (!slots[index].equalsExact(inventory.getItem(index))) {
                return false;
            }
            if (trackIdentity[index]) {
                PhysicalSlot physical = resolve(inventory, index);
                if (physical == null || physical.inventory().slots.get(physical.slot()) != identities[index]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Nullable
    private static PhysicalSlot resolve(Inventory inventory, int slot) {
        if (inventory instanceof DoubleChestInventory doubleChest) {
            int leftSize = doubleChest.getLeftSide().getSize();
            return slot < leftSize ? resolve(doubleChest.getLeftSide(), slot)
                    : resolve(doubleChest.getRightSide(), slot - leftSize);
        }
        return inventory instanceof BaseInventory base && base.slots != null ? new PhysicalSlot(base, slot) : null;
    }

    private record PhysicalSlot(BaseInventory inventory, int slot) {
    }
}
