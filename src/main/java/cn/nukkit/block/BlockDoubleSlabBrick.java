package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabBrick extends BlockDoubleSlabStone {
    BlockDoubleSlabBrick() {

    }

    @Override
    public int getId() {
        return BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Brick Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.BRICK_SLAB);
    }
}
