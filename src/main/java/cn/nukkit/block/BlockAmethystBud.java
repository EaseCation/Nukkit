package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

public abstract class BlockAmethystBud extends BlockAmethystCluster {
    protected BlockAmethystBud(int meta) {
        super(meta);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }
}
