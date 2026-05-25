package cn.nukkit.blockentity;

import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;

public interface HopperInteractable {
    default boolean pull(BlockEntityHopper hopper) {
        return false;
    }

    default boolean push(BlockEntityHopper hopper) {
        Inventory inventory = hopper.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            Item item = inventory.getItem(i);
            if (item.isNull()) {
                continue;
            }

            if (!push(item)) {
                continue;
            }

            inventory.setItem(i, item);
            return true;
        }
        return false;
    }

    default boolean push(Item item) {
        return false;
    }
}
