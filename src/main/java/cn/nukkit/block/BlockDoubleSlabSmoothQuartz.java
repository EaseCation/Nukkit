package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabSmoothQuartz extends BlockDoubleSlabStone {
    BlockDoubleSlabSmoothQuartz() {

    }

    @Override
    public int getId() {
        return SMOOTH_QUARTZ_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Smooth Quartz Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.SMOOTH_QUARTZ_SLAB);
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_stone_slab4.smooth_quartz.name";
    }
}
