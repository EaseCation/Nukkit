package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabPetrifiedOak extends BlockDoubleSlabStone {
    BlockDoubleSlabPetrifiedOak() {

    }

    @Override
    public int getId() {
        return PETRIFIED_OAK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Petrified Oak Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.PETRIFIED_OAK_SLAB);
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_stone_slab.wood.name";
    }
}
