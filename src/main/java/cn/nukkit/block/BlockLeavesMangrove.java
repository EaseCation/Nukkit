package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockLeavesMangrove extends BlockLeaves {
    BlockLeavesMangrove() {

    }

    @Override
    public int getId() {
        return MANGROVE_LEAVES;
    }

    public String getName() {
        return "Mangrove Leaves";
    }

    @Override
    protected int getSaplingDropChance() {
        return 0;
    }

    @Override
    protected Item getSapling() {
        return Item.get(ItemBlockID.MANGROVE_PROPAGULE);
    }
}
