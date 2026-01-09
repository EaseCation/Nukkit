package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabCutSandstone extends BlockDoubleSlabStone {
    BlockDoubleSlabCutSandstone() {

    }

    @Override
    public int getId() {
        return CUT_SANDSTONE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Cut Sandstone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.CUT_SANDSTONE_SLAB);
    }
}
