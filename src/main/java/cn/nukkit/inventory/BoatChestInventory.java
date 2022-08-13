package cn.nukkit.inventory;

import cn.nukkit.entity.item.EntityBoatChest;

public class BoatChestInventory extends ContainerInventory {

    public BoatChestInventory(EntityBoatChest boat) {
        super(boat, InventoryType.BOAT_CHEST);
    }

    @Override
    public EntityBoatChest getHolder() {
        return (EntityBoatChest) this.holder;
    }
}
