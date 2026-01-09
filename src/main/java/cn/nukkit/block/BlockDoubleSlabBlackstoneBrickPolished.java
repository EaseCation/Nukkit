package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockDoubleSlabBlackstoneBrickPolished extends BlockDoubleSlabBlackstone {
    BlockDoubleSlabBlackstoneBrickPolished() {

    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Polished Blackstone Brick Slab";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.POLISHED_BLACKSTONE_BRICK_SLAB);
    }
}
