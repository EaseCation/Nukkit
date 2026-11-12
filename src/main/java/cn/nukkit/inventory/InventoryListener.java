package cn.nukkit.inventory;

import javax.annotation.Nullable;

public interface InventoryListener {
    int getWindowId(Inventory inventory);

    @Nullable
    default Inventory getWindowById(int id) {
        return null;
    }

    void onCurrentWindowChange(Inventory inventory);

    void onCurrentWindowRemove();

    void onSlotChange(Inventory inventory, int slot);

    default void syncContents(Inventory inventory) {
    }

    default void syncSlot(Inventory inventory, int slot) {
    }

    void syncSelectedHotbarSlot();
}
