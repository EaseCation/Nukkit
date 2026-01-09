package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabMossyStoneBrick extends BlockDoubleSlabStone {
    BlockDoubleSlabMossyStoneBrick() {

    }

    @Override
    public int getId() {
        return MOSSY_STONE_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Mossy Stone Brick Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.MOSSY_STONE_BRICK_SLAB);
    }
}
