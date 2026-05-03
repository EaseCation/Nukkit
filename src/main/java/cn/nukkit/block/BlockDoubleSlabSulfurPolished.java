package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockDoubleSlabSulfurPolished extends BlockDoubleSlabSulfur {
    BlockDoubleSlabSulfurPolished() {
    }

    @Override
    public int getId() {
        return POLISHED_SULFUR_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Polished Sulfur Slab";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.POLISHED_SULFUR_SLAB);
    }
}
