package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabSmoothStone extends BlockDoubleSlabStone {
    BlockDoubleSlabSmoothStone() {

    }

    @Override
    public int getId() {
        return SMOOTH_STONE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Smooth Stone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.SMOOTH_STONE_SLAB);
    }
}
