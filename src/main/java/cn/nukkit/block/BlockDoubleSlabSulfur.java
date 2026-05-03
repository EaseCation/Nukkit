package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabSulfur extends BlockDoubleSlabStone {
    BlockDoubleSlabSulfur() {
    }

    @Override
    public int getId() {
        return SULFUR_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Sulfur Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.SULFUR_SLAB);
    }
}
