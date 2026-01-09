package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabPolishedTuff extends BlockDoubleSlabStone {
    BlockDoubleSlabPolishedTuff() {

    }

    @Override
    public int getId() {
        return POLISHED_TUFF_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Polished Tuff Slab";
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
        return Item.get(ItemBlockID.POLISHED_TUFF_SLAB);
    }
}
