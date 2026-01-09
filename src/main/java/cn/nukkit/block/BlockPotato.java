package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Pub4Game on 15.01.2016.
 */
public class BlockPotato extends BlockCrops {

    BlockPotato() {

    }

    @Override
    public String getName() {
        return "Potato Block";
    }

    @Override
    public int getId() {
        return POTATOES;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.POTATO);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (getDamage() >= 0x07) {
            return new Item[]{
                    Item.get(Item.POTATO, 0, ThreadLocalRandom.current().nextInt(3) + 1)
            };
        } else {
            return new Item[]{
                    Item.get(Item.POTATO)
            };
        }
    }
}
