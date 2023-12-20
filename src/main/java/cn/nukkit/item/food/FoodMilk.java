package cn.nukkit.item.food;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

/**
 * Created by Snake1999 on 2016/1/21.
 * Package cn.nukkit.item.food in project nukkit.
 */
public class FoodMilk extends Food {
    @Override
    protected boolean onEatenBy(Player player) {
        super.onEatenBy(player);

        player.removeAllEffects();

        if (!player.isSurvivalLike()) {
            return true;
        }

        player.getInventory().addItem(Item.get(Item.BUCKET));
        return true;
    }
}
