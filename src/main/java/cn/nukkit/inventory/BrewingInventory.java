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
        who.sendContainerData(this, ContainerSetDataPacket.PROPERTY_BREWING_STAND_BREW_TIME, holder.isBrewing() ? holder.brewTime : 0);
        who.sendContainerData(this, ContainerSetDataPacket.PROPERTY_BREWING_STAND_FUEL_AMOUNT, holder.getFuel());
        who.sendContainerData(this, ContainerSetDataPacket.PROPERTY_BREWING_STAND_FUEL_TOTAL, holder.fuelTotal);
    }
}
