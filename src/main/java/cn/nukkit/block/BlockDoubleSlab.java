package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

public abstract class BlockDoubleSlab extends BlockSolid {

    @Deprecated
    public static final int TYPE_MASK = 0b111;
    public static final int TOP_SLOT_BIT = 0b1;

    @Override
    public boolean isSlab() {
        return true;
    }

    @Override
    public boolean isDoubleSlab() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        Item drop = toItem(true);
        drop.setCount(2);
        return new Item[]{drop};
    }
}
