package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

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
        return Item.get(Item.MELON_SEEDS);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                Item.get(Item.MELON_SEEDS, 0, ThreadLocalRandom.current().nextInt(0, 4))
        };
    }

    @Override
    public int getFruitBlockId() {
        return MELON_BLOCK;
    }
}
