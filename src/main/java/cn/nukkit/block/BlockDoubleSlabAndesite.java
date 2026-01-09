package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabAndesite extends BlockDoubleSlabStone {
    BlockDoubleSlabAndesite() {

    }

    @Override
    public int getId() {
        return ANDESITE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Andesite Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.ANDESITE_SLAB);
    }
}
