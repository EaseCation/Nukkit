package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Pub4Game on 15.01.2016.
 */
public class BlockStemMelon extends BlockStem {

    BlockStemMelon() {

    }

    @Override
    public int getId() {
        return MELON_STEM;
    }

    @Override
    public String getName() {
        return "Melon Stem";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.MELON_SEEDS);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        int count = 0;
        int growth = getGrowth();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 3; i++) {
            if (random.nextInt(15) <= growth) {
                count++;
            }
        }
        if (count == 0) {
            return new Item[0];
        }
        return new Item[]{
                Item.get(Item.MELON_SEEDS, 0, count)
        };
    }

    @Override
    public int getFruitBlockId() {
        return MELON_BLOCK;
    }
}
