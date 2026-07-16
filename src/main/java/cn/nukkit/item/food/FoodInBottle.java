package cn.nukkit.item.food;

import cn.nukkit.item.Item;

public class FoodInBottle extends Food {
    public FoodInBottle(int restoreFood, float restoreSaturation) {
        this.setRestoreFood(restoreFood);
        this.setRestoreSaturation(restoreSaturation);
    }

    @Override
    public Item getContainerItem() {
        return Item.get(Item.GLASS_BOTTLE);
    }
}
