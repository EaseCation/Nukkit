package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import lombok.ToString;

/**
 * This action is used by creative players to balance transactions involving the creative inventory menu.
 * The source item is the item being created ("taken" from the creative menu).
 */
@ToString
public class CreateItemAction extends InventoryAction {
    public CreateItemAction(Item source) {
        super(source, Items.air());
    }

    @Override
    public boolean isValid(Player source) {
        if (!source.isCreative()) {
            return false;
        }
//        if (source.getCreativeItemIndex(sourceItem) == -1) { //TODO: mv
//            return false;
//        }
        return sourceItem.getCount() <= sourceItem.getMaxStackSize();
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
}
