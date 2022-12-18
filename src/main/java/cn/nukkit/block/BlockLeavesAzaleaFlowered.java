package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockLeavesAzaleaFlowered extends BlockLeavesAzalea {
    public BlockLeavesAzaleaFlowered() {
        this(0);
    }

    public BlockLeavesAzaleaFlowered(int meta) {
        super(meta);
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
        return Item.get(getItemId(FLOWERING_AZALEA));
    }
}
