package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockLeavesAzalea extends BlockLeaves {
    BlockLeavesAzalea() {

    }

    @Override
    public int getId() {
        return AZALEA_LEAVES;
    }

    public String getName() {
        return "Azalea Leaves";
    }

    @Override
    protected Item getSapling() {
        return Item.get(ItemBlockID.AZALEA);
    }
}
