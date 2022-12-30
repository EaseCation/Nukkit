package cn.nukkit.block;

import cn.nukkit.item.Item;

public abstract class BlockAmethystBud extends BlockAmethystCluster {
    protected BlockAmethystBud(int meta) {
        super(meta);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }
}
