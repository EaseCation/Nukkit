package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSeedsMelon;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Pub4Game on 15.01.2016.
 */
public class BlockStemMelon extends BlockStem {

    public BlockStemMelon() {
        this(0);
    }

    public BlockStemMelon(int meta) {
        super(meta);
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
        return new ItemSeedsMelon();
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                new ItemSeedsMelon(0, ThreadLocalRandom.current().nextInt(0, 4))
        };
    }

    @Override
    public int getFruitBlockId() {
        return MELON_BLOCK;
    }
}
