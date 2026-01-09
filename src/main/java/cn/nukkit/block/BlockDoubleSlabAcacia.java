package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabAcacia extends BlockDoubleSlabWood {
    BlockDoubleSlabAcacia() {

    }

    @Override
    public int getId() {
        return ACACIA_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Acacia Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.ACACIA_SLAB);
    }
}
