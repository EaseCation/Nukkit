package cn.nukkit.item.food;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item.food in project nukkit.
 */
public class FoodInBowl extends Food {

    public FoodInBowl(int restoreFood, float restoreSaturation) {
        this.setRestoreFood(restoreFood);
        this.setRestoreSaturation(restoreSaturation);
    }

    @Override
    protected boolean onEatenBy(Player player) {
        super.onEatenBy(player);

        if (!player.isSurvivalLike()) {
            return true;
        }

        player.getInventory().addItem(Item.get(Item.BOWL));
        return true;
    }

}
