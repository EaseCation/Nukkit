package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public class BlockDoubleSlabRedSandstone extends BlockDoubleSlabStone {
    BlockDoubleSlabRedSandstone() {

    }

    @Override
    public int getId() {
        return RED_SANDSTONE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Red Sandstone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.RED_SANDSTONE_SLAB);
    }
}
