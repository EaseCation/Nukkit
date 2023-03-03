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
        player.getInventory().addItem(Item.get(Item.GLASS_BOTTLE));
        return true;
    }
}
