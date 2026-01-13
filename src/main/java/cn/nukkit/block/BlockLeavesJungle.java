package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockLeavesJungle extends BlockLeaves {
    BlockLeavesJungle() {

    }

    @Override
    public int getId() {
        return JUNGLE_LEAVES;
    }

    @Override
    public String getName() {
        return "Jungle Leaves";
    }

    @Override
    protected int getSaplingDropChance() {
        return 40;
    }

    @Override
    protected Item getSapling() {
        return Item.get(ItemBlockID.JUNGLE_SAPLING);
    }

    @Override
    public String getDescriptionId() {
        return "tile.leaves.jungle.name";
    }
}
