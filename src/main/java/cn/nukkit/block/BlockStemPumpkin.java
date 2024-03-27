package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Pub4Game on 15.01.2016.
 */
public class BlockStemPumpkin extends BlockStem {

    public BlockStemPumpkin() {
        this(0);
    }

    public BlockStemPumpkin(int meta) {
        super(meta);
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
        return new Item[]{
                Item.get(Item.PUMPKIN_SEEDS, 0, ThreadLocalRandom.current().nextInt(0, 4))
        };
    }

    @Override
    public int getFruitBlockId() {
        return PUMPKIN;
    }
}
