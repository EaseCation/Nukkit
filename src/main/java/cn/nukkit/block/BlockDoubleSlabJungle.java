package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabJungle extends BlockDoubleSlabWood {
    BlockDoubleSlabJungle() {

    }

    @Override
    public int getId() {
        return JUNGLE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Jungle Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.JUNGLE_SLAB);
    }
}
