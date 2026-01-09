package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

public abstract class BlockAmethystBud extends BlockAmethystCluster {

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }
}
