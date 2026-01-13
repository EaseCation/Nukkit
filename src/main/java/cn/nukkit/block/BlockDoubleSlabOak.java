package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabOak extends BlockDoubleSlabWood {
    BlockDoubleSlabOak() {

    }

    @Override
    public int getId() {
        return OAK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Oak Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.OAK_SLAB);
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_wooden_slab.oak.name";
    }
}
