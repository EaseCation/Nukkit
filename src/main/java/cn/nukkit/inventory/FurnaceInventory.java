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

        if (who.isServerAuthoritativeInventoryEnabled()) {
            sendAuthoritativeProgress(who);
            return;
        }

        BlockEntityFurnace holder = getHolder();
        who.sendContainerData(this, ContainerSetDataPacket.PROPERTY_FURNACE_SMELT_PROGRESS, holder.getCookTime());
        who.sendContainerData(this, ContainerSetDataPacket.PROPERTY_FURNACE_REMAINING_FUEL_TIME, holder.getBurnDuration());
    }

    private void sendAuthoritativeProgress(Player player) {
        BlockEntityFurnace holder = getHolder();
        player.sendContainerData(this, ContainerSetDataPacket.PROPERTY_FURNACE_SMELT_PROGRESS, holder.getCookTime());
        player.sendContainerData(this, ContainerSetDataPacket.PROPERTY_FURNACE_REMAINING_FUEL_TIME, holder.getBurnTime());
        player.sendContainerData(this, ContainerSetDataPacket.PROPERTY_FURNACE_MAX_FUEL_TIME, holder.getMaxTime());
    }
}
