package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabSmoothSandstone extends BlockDoubleSlabStone {
    BlockDoubleSlabSmoothSandstone() {

    }

    @Override
    public int getId() {
        return SMOOTH_SANDSTONE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Smooth Sandstone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.SMOOTH_SANDSTONE_SLAB);
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_stone_slab2.sandstone.smooth.name";
    }
}
