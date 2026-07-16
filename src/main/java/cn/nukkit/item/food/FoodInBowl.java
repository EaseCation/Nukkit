package cn.nukkit.item.food;

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
    public Item getContainerItem() {
        return Item.get(Item.BOWL);
    }

}
