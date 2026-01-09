package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

import java.util.concurrent.ThreadLocalRandom;

public class BlockGrassTall extends BlockDoublePlant {
    BlockGrassTall() {

    }

    @Override
    public int getId() {
        return TALL_GRASS;
    }

    @Override
    public String getName() {
        return "Tall Grass";
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isShears()) {
            return new Item[]{
                    toItem(true)
            };
        }
        if (ThreadLocalRandom.current().nextInt(8) == 0) {
            return new Item[]{
                    Item.get(Item.WHEAT_SEEDS)
            };
        }
        return new Item[0];
    }

    @Override
    public int getCompostableChance() {
        return 50;
    }
}
