package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabSmoothRedSandstone extends BlockDoubleSlabStone {
    BlockDoubleSlabSmoothRedSandstone() {

    }

    @Override
    public int getId() {
        return SMOOTH_RED_SANDSTONE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Smooth Red Sandstone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.SMOOTH_RED_SANDSTONE_SLAB);
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_stone_slab3.red_sandstone.smooth.name";
    }
}
