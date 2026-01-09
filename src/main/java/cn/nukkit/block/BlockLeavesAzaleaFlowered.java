package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockLeavesAzaleaFlowered extends BlockLeavesAzalea {
    BlockLeavesAzaleaFlowered() {

    }

    @Override
    public int getId() {
        return AZALEA_LEAVES_FLOWERED;
    }

    public String getName() {
        return "Flowering Azalea Leaves";
    }

    @Override
    protected Item getSapling() {
        return Item.get(ItemBlockID.FLOWERING_AZALEA);
    }
}
