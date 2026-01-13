package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabBirch extends BlockDoubleSlabWood {
    BlockDoubleSlabBirch() {

    }

    @Override
    public int getId() {
        return BIRCH_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Birch Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.BIRCH_SLAB);
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_wooden_slab.birch.name";
    }
}
