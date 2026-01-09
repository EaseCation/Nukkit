package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockLeavesPaleOak extends BlockLeaves {
    BlockLeavesPaleOak() {

    }

    @Override
    public int getId() {
        return PALE_OAK_LEAVES;
    }

    public String getName() {
        return "Pale Oak Leaves";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    protected Item getSapling() {
        return Item.get(ItemBlockID.PALE_OAK_SAPLING);
    }
}
