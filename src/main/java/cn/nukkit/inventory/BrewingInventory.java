package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityBrewingStand;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ContainerSetDataPacket;

public class BrewingInventory extends ContainerInventory {
    public BrewingInventory(BlockEntityBrewingStand brewingStand) {
        super(brewingStand, InventoryType.BREWING_STAND);
    }

    @Override
    public BlockEntityBrewingStand getHolder() {
        return (BlockEntityBrewingStand) this.holder;
    }

    public Item getIngredient() {
        return getItem(0);
    }

    public void setIngredient(Item item) {
        setItem(0, item);
    }

    public void setFuel(Item fuel) {
        setItem(4, fuel);
    }

    public Item getFuel() {
        return getItem(4);
    }

    @Override
    public void onSlotChange(int index, Item before, Item after, boolean send) {
        super.onSlotChange(index, before, after, send);

        if (index >= 1 && index <= 3) {
            this.getHolder().updateBlock();
        }

        this.getHolder().scheduleUpdate();
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);

        BlockEntityBrewingStand holder = getHolder();
        int windowId = who.getWindowId(this);

        ContainerSetDataPacket pk0 = new ContainerSetDataPacket();
        pk0.windowId = windowId;
        pk0.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_BREW_TIME;
        pk0.value = holder.isBrewing() ? holder.brewTime : 0;
        who.dataPacket(pk0);

        ContainerSetDataPacket pk1 = new ContainerSetDataPacket();
        pk1.windowId = windowId;
        pk1.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_FUEL_AMOUNT;
        pk1.value = holder.getFuel();
        who.dataPacket(pk1);

        ContainerSetDataPacket pk2 = new ContainerSetDataPacket();
        pk2.windowId = windowId;
        pk2.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_FUEL_TOTAL;
        pk2.value = holder.fuelTotal;
        who.dataPacket(pk2);
    }
}