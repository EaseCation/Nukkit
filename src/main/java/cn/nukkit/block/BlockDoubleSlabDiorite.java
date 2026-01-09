package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabDiorite extends BlockDoubleSlabStone {
    BlockDoubleSlabDiorite() {

    }

    @Override
    public int getId() {
        return DIORITE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Diorite Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.DIORITE_SLAB);
    }
}
