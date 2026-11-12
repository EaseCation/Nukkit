package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import lombok.ToString;

@ToString
public class PayItemAction extends DestroyItemAction {
    public PayItemAction(Item target) {
        super(target);
    }

    @Override
    public boolean isValid(Player source) {
        if (targetItem.getCount() != 1) {
            return false;
        }
        int id = targetItem.getId();
        return id == Item.IRON_INGOT || id == Item.GOLD_INGOT || id == Item.EMERALD || id == Item.DIAMOND || id == Item.NETHERITE_INGOT;
    }
}
