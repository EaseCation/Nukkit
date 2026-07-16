package cn.nukkit.item.food;

import cn.nukkit.item.Item;

public class FoodEffectiveInBowl extends FoodEffective {
    public FoodEffectiveInBowl(int restoreFood, float restoreSaturation) {
        super(restoreFood, restoreSaturation);
    }

    @Override
    public Item getContainerItem() {
        return Item.get(Item.BOWL);
    }
}
