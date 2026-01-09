package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockLeavesAcacia extends BlockLeaves {
    BlockLeavesAcacia() {

    }

    @Override
    public int getId() {
        return ACACIA_LEAVES;
    }

    @Override
    public String getName() {
        return "Acacia Leaves";
    }

    @Override
    protected Item getSapling() {
        return Item.get(ItemBlockID.ACACIA_SAPLING);
    }
}
