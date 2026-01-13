package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabQuartz extends BlockDoubleSlabStone {
    BlockDoubleSlabQuartz() {

    }

    @Override
    public int getId() {
        return QUARTZ_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Quartz Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.QUARTZ_SLAB);
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_stone_slab.quartz.name";
    }
}
