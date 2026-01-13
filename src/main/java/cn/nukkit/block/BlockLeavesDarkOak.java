package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockLeavesDarkOak extends BlockLeaves {
    BlockLeavesDarkOak() {

    }

    @Override
    public int getId() {
        return DARK_OAK_LEAVES;
    }

    @Override
    public String getName() {
        return "Dark Oak Leaves";
    }

    @Override
    protected boolean canDropApple() {
        return true;
    }

    @Override
    protected Item getSapling() {
        return Item.get(ItemBlockID.DARK_OAK_SAPLING);
    }

    @Override
    public String getDescriptionId() {
        return "tile.leaves2.big_oak.name";
    }
}
