package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabSandstone extends BlockDoubleSlabStone {
    BlockDoubleSlabSandstone() {

    }

    @Override
    public int getId() {
        return SANDSTONE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Sandstone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.SANDSTONE_SLAB);
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_stone_slab.sand.name";
    }
}
