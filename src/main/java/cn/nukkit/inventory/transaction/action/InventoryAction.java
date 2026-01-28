package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.item.Item;

/**
 * @author CreeperFace
 */
public abstract class InventoryAction {

    protected Item sourceItem;
    protected Item targetItem;

    public InventoryAction(Item sourceItem, Item targetItem) {
        this.sourceItem = sourceItem;
        this.targetItem = targetItem;
    }

    /**
     * Returns the item that was present before the action took place.
     *
     * @return Item
     */
    public Item getSourceItem() {
        return sourceItem.clone();
    }

    public Item getSourceItemUnsafe() {
        return sourceItem;
    }

    /**
     * Returns the item that the action attempted to replace the source item with.
     */
    public Item getTargetItem() {
        return targetItem.clone();
    }

    public Item getTargetItemUnsafe() {
        return targetItem;
    }

    public void setTargetItem(Item targetItem) {
        this.targetItem = targetItem;
    }

    /**
     * Called by inventory transactions before any actions are processed. If this returns false, the transaction will
     * be cancelled.
     */
    public boolean onPreExecute(Player source) {
        return true;
    }

    public boolean onPreExecuteNoSync(Player source) {
        return true;
    }

    /**
     * Returns whether this action is currently valid. This should perform any necessary sanity checks.
     */
    public abstract boolean isValid(Player source);

    /**
     * Called when the action is added to the specified InventoryTransaction.
     */
    public void onAddToTransaction(InventoryTransaction transaction) {

    }

    /**
     * Performs actions needed to complete the inventory-action server-side. Returns if it was successful. Will return
     * false if plugins cancelled events. This will only be called if the transaction which it is part of is considered
     * valid.
     */
    public abstract boolean execute(Player source);

    /**
     * Performs additional actions when this inventory-action completed successfully.
     */
    public abstract void onExecuteSuccess(Player source);

    /**
     * Performs additional actions when this inventory-action did not complete successfully.
     */
    public abstract void onExecuteFail(Player source);

    public boolean hasComponents() {
        return true;
    }
}
