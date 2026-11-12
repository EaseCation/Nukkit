package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import lombok.ToString;

/**
 * This action type shows up when a creative player puts an item into the creative inventory menu to destroy it.
 * The output is the item destroyed. You can think of this action type like setting an item into /dev/null
 */
@ToString
public class DestroyItemAction extends InventoryAction {
    public DestroyItemAction(Item target) {
        super(Items.air(), target);
    }

    @Override
    public boolean isValid(Player source) {
        return source.isCreative();
    }

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
        return false;
    }
}
