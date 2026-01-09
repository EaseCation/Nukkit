package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabNetherBrick extends BlockDoubleSlabStone {
    BlockDoubleSlabNetherBrick() {

    }

    @Override
    public int getId() {
        return NETHER_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Nether Brick Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHER_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.NETHER_BRICK_SLAB);
    }
}
