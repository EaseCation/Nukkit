package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.inventory.FurnaceInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.item.Item;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * @author CreeperFace
 */
@ToString
public class SlotChangeAction extends InventoryAction {

    protected Inventory inventory;
    private final int inventorySlot;

    public SlotChangeAction(Inventory inventory, int inventorySlot, Item sourceItem, Item targetItem) {
        super(sourceItem, targetItem);
        this.inventory = inventory;
        this.inventorySlot = inventorySlot;
    }

    /**
     * Returns the inventory involved in this action.
     *
     * @return inventory
     */
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Returns the inventorySlot in the inventory which this action modified.
     *
     * @return slot
     */
    public int getSlot() {
        return inventorySlot;
    }

    /**
     * Checks if the item in the inventory at the specified inventorySlot is the same as this action's source item.
     *
     * @param source player
     * @return valid
     */
    @Override
    public boolean isValid(Player source) {
        Item check = inventory.getItem(this.inventorySlot);

        return check.equalsExact(this.sourceItem) && targetItem.getCount() <= targetItem.getMaxStackSize() && targetItem.getCount() <= inventory.getMaxStackSize();
    }

    /**
     * Sets the item into the target inventory.
     *
     * @param source player
     * @return successfully executed
     */
    @Override
    public boolean execute(Player source) {
        return this.inventory.setItem(this.inventorySlot, this.targetItem, false);
    }

    /**
     * Sends inventorySlot changes to other viewers of the inventory. This will not send any change back to the source Player.
     *
     * @param source player
     */
    @Override
    public void onExecuteSuccess(Player source) {
        Set<Player> viewers = new HashSet<>(this.inventory.getViewers());
        viewers.remove(source);

        this.inventory.sendSlot(this.inventorySlot, viewers);

        if (inventory instanceof FurnaceInventory furnace && inventorySlot == 2 && !sourceItem.isNull() && targetItem.isNull()) {
            int xp = furnace.getHolder().withdrawStoredXpReward();
            if (xp > 0) {
                source.level.dropExpOrb(source, xp);
            }
        }
    }

    /**
     * Sends the original inventorySlot contents to the source player to revert the action.
     *
     * @param source player
     */
    @Override
    public void onExecuteFail(Player source) {
        this.inventory.sendSlot(this.inventorySlot, source);
    }

    @Override
    public void onAddToTransaction(InventoryTransaction transaction) {
        transaction.addInventory(this.inventory);
    }
}
