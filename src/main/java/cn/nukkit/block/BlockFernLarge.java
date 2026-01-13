package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

import java.util.concurrent.ThreadLocalRandom;

public class BlockFernLarge extends BlockDoublePlant {
    BlockFernLarge() {

    }

    @Override
    public int getId() {
        return LARGE_FERN;
    }

    @Override
    public String getName() {
        return "Large Fern";
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
    public String getDescriptionId() {
        return "tile.double_plant.fern.name";
    }
}
