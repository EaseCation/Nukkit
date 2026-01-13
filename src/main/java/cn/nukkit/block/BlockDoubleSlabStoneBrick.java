package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabStoneBrick extends BlockDoubleSlabStone {
    BlockDoubleSlabStoneBrick() {

    }

    @Override
    public int getId() {
        return STONE_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Stone Brick Slab";
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
        return Item.get(ItemBlockID.STONE_BRICK_SLAB);
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_stone_slab.smoothStoneBrick.name";
    }
}
