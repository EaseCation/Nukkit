package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.inventory.ArmorInventory;
import cn.nukkit.inventory.FurnaceInventory;
import cn.nukkit.inventory.ExplicitItemUseHandAccess;
import cn.nukkit.inventory.ExplicitItemUseHandPolicy;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.PlayerOffhandInventory;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * @author CreeperFace
 */
@ToString
public class SlotChangeAction extends InventoryAction {

    private final Inventory inventory;
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
        if (inventory instanceof PlayerOffhandInventory
                && source instanceof ExplicitItemUseHandAccess access
                && !ExplicitItemUseHandPolicy.isOffhandInventoryMutationAllowed(
                        access.isExplicitItemUseHandClient(), access.isExplicitItemUseHandAllowed())) {
            return false;
        }
        Item check = inventory.getItem(this.inventorySlot);

        return check.equalsExact(this.sourceItem)
                && targetItem.getCount() <= targetItem.getMaxStackSize()
                && targetItem.getCount() <= inventory.getMaxStackSize()
                && (!(inventory instanceof PlayerOffhandInventory) || targetItem.canDualWield() || targetItem.isNull())
                && (!(inventory instanceof ArmorInventory)
                || isItemValidForArmorSlot(this.inventorySlot, this.targetItem));
    }

    static boolean isItemValidForArmorSlot(int inventorySlot, Item item) {
        if (item.isNull()) {
            return true;
        }
        if (item.getCount() != 1) {
            return false;
        }

        return switch (inventorySlot) {
            case ArmorInventory.SLOT_HEAD -> item.isHelmet() || item.isSkull() || item.is(ItemBlockID.CARVED_PUMPKIN);
            case ArmorInventory.SLOT_TORSO -> item.isChestplate();
            case ArmorInventory.SLOT_LEGS -> item.isLeggings();
            case ArmorInventory.SLOT_FEET -> item.isBoots();
            default -> false;
        };
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
