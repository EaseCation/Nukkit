package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabPaleOak extends BlockDoubleSlabWood {
    BlockDoubleSlabPaleOak() {

    }

    @Override
    public int getId() {
        return PALE_OAK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Pale Oak Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.PALE_OAK_SLAB);
    }
}
