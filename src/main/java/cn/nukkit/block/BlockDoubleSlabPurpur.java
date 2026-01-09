package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabPurpur extends BlockDoubleSlabStone {
    BlockDoubleSlabPurpur() {

    }

    @Override
    public int getId() {
        return PURPUR_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Purpur Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.PURPUR_SLAB);
    }
}
