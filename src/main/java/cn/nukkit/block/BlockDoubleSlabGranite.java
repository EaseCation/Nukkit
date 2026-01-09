package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabGranite extends BlockDoubleSlabStone {
    BlockDoubleSlabGranite() {

    }

    @Override
    public int getId() {
        return GRANITE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Granite Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.GRANITE_SLAB);
    }
}
