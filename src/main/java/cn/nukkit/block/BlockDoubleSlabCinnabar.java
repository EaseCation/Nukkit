package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabCinnabar extends BlockDoubleSlabStone {
    BlockDoubleSlabCinnabar() {
    }

    @Override
    public int getId() {
        return CINNABAR_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Cinnabar Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.CINNABAR_SLAB);
    }
}
