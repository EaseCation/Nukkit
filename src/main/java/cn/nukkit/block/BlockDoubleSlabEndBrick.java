package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabEndBrick extends BlockDoubleSlabStone {
    BlockDoubleSlabEndBrick() {

    }

    @Override
    public int getId() {
        return END_STONE_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double End Stone Brick Slab";
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 45;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.END_STONE_BRICK_SLAB);
    }
}
