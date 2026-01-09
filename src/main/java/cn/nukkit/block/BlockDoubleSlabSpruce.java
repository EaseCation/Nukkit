package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabSpruce extends BlockDoubleSlabWood {
    BlockDoubleSlabSpruce() {

    }

    @Override
    public int getId() {
        return SPRUCE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Spruce Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PODZOL_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.SPRUCE_SLAB);
    }
}
