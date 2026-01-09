package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabNormalStone extends BlockDoubleSlabStone {
    BlockDoubleSlabNormalStone() {

    }

    @Override
    public int getId() {
        return NORMAL_STONE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Stone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.NORMAL_STONE_SLAB);
    }
}
