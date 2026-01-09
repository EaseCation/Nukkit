package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabWarped extends BlockDoubleSlabWood {
    BlockDoubleSlabWarped() {

    }

    @Override
    public int getId() {
        return WARPED_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Warped Slab";
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.WARPED_SLAB);
    }
}
