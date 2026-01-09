package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockLeavesBirch extends BlockLeaves {
    BlockLeavesBirch() {

    }

    @Override
    public int getId() {
        return BIRCH_LEAVES;
    }

    @Override
    public String getName() {
        return "Birch Leaves";
    }

    @Override
    protected Item getSapling() {
        return Item.get(ItemBlockID.BIRCH_SAPLING);
    }
}
