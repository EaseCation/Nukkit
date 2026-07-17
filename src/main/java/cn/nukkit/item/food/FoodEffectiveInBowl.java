package cn.nukkit.item.food;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

public class FoodEffectiveInBowl extends FoodEffective {
    public FoodEffectiveInBowl(int restoreFood, float restoreSaturation) {
        super(restoreFood, restoreSaturation);
    }

    @Override
    protected boolean onEatenBy(Player player) {
        super.onEatenBy(player);
        if (FoodContainerDeliveryPolicy.shouldDeliverDuringEating(
                player.isSurvivalLike(), player.isOffhandItemInteraction())) {
            player.getInventory().addItem(this.getContainerItem());
        }
        return true;
    }

    @Override
    public Item getContainerItem() {
        return Item.get(Item.BOWL);
    }
}
