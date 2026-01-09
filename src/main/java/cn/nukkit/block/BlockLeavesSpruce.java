package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockLeavesSpruce extends BlockLeaves {
    BlockLeavesSpruce() {

    }

    @Override
    public int getId() {
        return SPRUCE_LEAVES;
    }

    @Override
    public String getName() {
        return "Spruce Leaves";
    }

    @Override
    protected Item getSapling() {
        return Item.get(ItemBlockID.SPRUCE_SAPLING);
    }
}
