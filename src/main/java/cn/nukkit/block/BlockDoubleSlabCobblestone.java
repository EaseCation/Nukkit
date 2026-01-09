package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabCobblestone extends BlockDoubleSlabStone {
    BlockDoubleSlabCobblestone() {

    }

    @Override
    public int getId() {
        return COBBLESTONE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Cobblestone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.COBBLESTONE_SLAB);
    }
}
