package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabDarkOak extends BlockDoubleSlabWood {
    BlockDoubleSlabDarkOak() {

    }

    @Override
    public int getId() {
        return DARK_OAK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Dark Oak Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.DARK_OAK_SLAB);
    }
}
