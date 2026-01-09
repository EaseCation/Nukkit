package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabMangrove extends BlockDoubleSlabWood {
    BlockDoubleSlabMangrove() {

    }

    @Override
    public int getId() {
        return MANGROVE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Mangrove Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.MANGROVE_SLAB);
    }
}
