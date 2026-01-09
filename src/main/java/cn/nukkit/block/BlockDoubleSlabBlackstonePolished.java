package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockDoubleSlabBlackstonePolished extends BlockDoubleSlabBlackstone {
    BlockDoubleSlabBlackstonePolished() {

    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Polished Blackstone Slab";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.POLISHED_BLACKSTONE_SLAB);
    }
}
