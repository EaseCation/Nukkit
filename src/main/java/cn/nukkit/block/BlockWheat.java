package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockWheat extends BlockCrops {

    BlockWheat() {

    }

    @Override
    public String getName() {
        return "Wheat Block";
    }

    @Override
    public int getId() {
        return WHEAT;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.WHEAT_SEEDS);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (this.getDamage() >= 0x07) {
            return new Item[]{
                    Item.get(Item.WHEAT),
                    Item.get(Item.WHEAT_SEEDS, 0, ThreadLocalRandom.current().nextInt(4)),
            };
        } else {
            return new Item[]{
                    Item.get(Item.WHEAT_SEEDS),
            };
        }
    }
}
