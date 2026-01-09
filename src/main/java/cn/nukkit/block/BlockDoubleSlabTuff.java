package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabTuff extends BlockDoubleSlabStone {
    BlockDoubleSlabTuff() {

    }

    @Override
    public int getId() {
        return TUFF_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Tuff Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.TUFF_SLAB);
    }
}
