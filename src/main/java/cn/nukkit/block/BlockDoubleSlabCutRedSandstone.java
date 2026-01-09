package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabCutRedSandstone extends BlockDoubleSlabStone {
    BlockDoubleSlabCutRedSandstone() {

    }

    @Override
    public int getId() {
        return CUT_RED_SANDSTONE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Cut Red Sandstone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.CUT_RED_SANDSTONE_SLAB);
    }
}
