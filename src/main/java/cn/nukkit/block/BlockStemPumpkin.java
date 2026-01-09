package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Pub4Game on 15.01.2016.
 */
public class BlockStemPumpkin extends BlockStem {

    BlockStemPumpkin() {

    }

    @Override
    public int getId() {
        return PUMPKIN_STEM;
    }

    @Override
    public String getName() {
        return "Pumpkin Stem";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.PUMPKIN_SEEDS);
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
                Item.get(Item.PUMPKIN_SEEDS, 0, count)
        };
    }

    @Override
    public int getFruitBlockId() {
        return PUMPKIN;
    }
}
