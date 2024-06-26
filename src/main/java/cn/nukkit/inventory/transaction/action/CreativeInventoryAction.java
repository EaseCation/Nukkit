package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import lombok.ToString;

/**
 * @author CreeperFace
 */
@ToString
public class CreativeInventoryAction extends InventoryAction {
    /**
     * Player put an item into the creative window to destroy it.
     */
    public static final int TYPE_DELETE_ITEM = 0;
    /**
     * Player took an item from the creative window.
     */
    public static final int TYPE_CREATE_ITEM = 1;

    protected int actionType;

    public CreativeInventoryAction(Item source, Item target, int action) {
        super(source, target);
        actionType = action;
    }

    /**
     * Checks that the player is in creative, and (if creating an item) that the item exists in the creative inventory.
     *
     * @param source player
     * @return valid
     */
    @Override
    public boolean isValid(Player source) {
        return source.isCreative() && targetItem.getCount() <= targetItem.getMaxStackSize() /*&&
                (this.actionType == TYPE_DELETE_ITEM || source.getCreativeItemIndex(this.sourceItem) != -1)*/;
    }

    /**
     * Returns the type of the action.
     *
     * @return action type
     */
    public int getActionType() {
        return actionType;
    }

    /**
     * No need to do anything extra here: this type just provides a place for items to disappear or appear from.
     *
     * @param source playere
     * @return successfully executed
     */
    @Override
    public boolean execute(Player source) {
        return true;
    }

    @Override
    public void onExecuteSuccess(Player source) {

    }

    @Override
    public void onExecuteFail(Player source) {

    }

    @Override
    public boolean hasComponents() {
        return actionType != TYPE_DELETE_ITEM;
    }
}
