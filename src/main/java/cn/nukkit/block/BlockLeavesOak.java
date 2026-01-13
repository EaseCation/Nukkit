package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockLeavesOak extends BlockLeaves {
    BlockLeavesOak() {

    }

    @Override
    public int getId() {
        return OAK_LEAVES;
    }

    @Override
    public String getName() {
        return "Oak Leaves";
    }

    @Override
    protected boolean canDropApple() {
        return true;
    }

    @Override
    protected Item getSapling() {
        return Item.get(ItemBlockID.OAK_SAPLING);
    }

    @Override
    public String getDescriptionId() {
        return "tile.leaves.oak.name";
    }
}
