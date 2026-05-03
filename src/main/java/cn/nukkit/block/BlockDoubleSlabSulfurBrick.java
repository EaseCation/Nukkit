package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockDoubleSlabSulfurBrick extends BlockDoubleSlabSulfur {
    BlockDoubleSlabSulfurBrick() {
    }

    @Override
    public int getId() {
        return SULFUR_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Sulfur Brick Slab";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.SULFUR_BRICK_SLAB);
    }
}
