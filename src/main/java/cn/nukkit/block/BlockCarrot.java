package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Nukkit Project Team
 */
public class BlockCarrot extends BlockCrops {

    BlockCarrot() {

    }

    @Override
    public String getName() {
        return "Carrot Block";
    }

    @Override
    public int getId() {
        return CARROTS;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (getDamage() >= 0x07) {
            return new Item[]{
                    Item.get(Item.CARROT, 0, ThreadLocalRandom.current().nextInt(3) + 1)
            };
        }
        return new Item[]{
                Item.get(Item.CARROT)
        };
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.CARROT);
    }
}
