package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityFurnace;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ContainerSetDataPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FurnaceInventory extends ContainerInventory {

    public FurnaceInventory(BlockEntityFurnace furnace) {
        super(furnace, InventoryType.FURNACE);
    }

    protected FurnaceInventory(BlockEntityFurnace furnace, InventoryType type) {
        super(furnace, type);
    }

    @Override
    public BlockEntityFurnace getHolder() {
        return (BlockEntityFurnace) this.holder;
    }

    public Item getResult() {
        return this.getItem(2);
    }

    public Item getFuel() {
        return this.getItem(1);
    }

    public Item getSmelting() {
        return this.getItem(0);
    }

    public boolean setResult(Item item) {
        return this.setItem(2, item);
    }

    public boolean setFuel(Item item) {
        return this.setItem(1, item);
    }

    public boolean setSmelting(Item item) {
        return this.setItem(0, item);
    }

    @Override
    public void onSlotChange(int index, Item before, Item after, boolean send) {
        super.onSlotChange(index, before, after, send);

        BlockEntityFurnace furnace = getHolder();
        if (index == 2 && before != null && !before.isNull() && (after == null || after.isNull())) {
            furnace.postTakeResult(before);
        }

        furnace.scheduleUpdate();
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);

        BlockEntityFurnace holder = getHolder();
        int windowId = who.getWindowId(this);

        ContainerSetDataPacket pk0 = new ContainerSetDataPacket();
        pk0.windowId = windowId;
        pk0.property = ContainerSetDataPacket.PROPERTY_FURNACE_SMELT_PROGRESS;
        pk0.value = holder.getCookTime();
        who.dataPacket(pk0);

        ContainerSetDataPacket pk1 = new ContainerSetDataPacket();
        pk1.windowId = windowId;
        pk1.property = ContainerSetDataPacket.PROPERTY_FURNACE_REMAINING_FUEL_TIME;
        pk1.value = holder.getBurnDuration();
        who.dataPacket(pk1);
    }
}
