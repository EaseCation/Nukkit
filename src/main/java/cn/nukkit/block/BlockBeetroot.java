package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2015/11/22 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockBeetroot extends BlockCrops {
    BlockBeetroot() {

    }

    @Override
    public int getId() {
        return BEETROOT;
    }

    @Override
    public String getName() {
        return "Beetroot Block";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.BEETROOT_SEEDS);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (this.getDamage() >= 0x07) {
            return new Item[]{
                    Item.get(Item.BEETROOT, 0, 1),
                    Item.get(Item.BEETROOT_SEEDS, 0, ThreadLocalRandom.current().nextInt(1, 5)),
            };
        } else {
            return new Item[]{
                    Item.get(Item.BEETROOT_SEEDS, 0, 1)
            };
        }
    }
}
