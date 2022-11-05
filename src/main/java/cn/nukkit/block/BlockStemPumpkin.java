package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSeedsPumpkin;

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
        return new ItemSeedsPumpkin();
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                new ItemSeedsPumpkin(0, ThreadLocalRandom.current().nextInt(0, 4))
        };
    }

    @Override
    public int getFruitBlockId() {
        return PUMPKIN;
    }
}
