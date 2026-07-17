package cn.nukkit.item.food;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

public class FoodInBottle extends Food {
    public FoodInBottle(int restoreFood, float restoreSaturation) {
        this.setRestoreFood(restoreFood);
        this.setRestoreSaturation(restoreSaturation);
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
        return Item.get(Item.GLASS_BOTTLE);
    }
}
