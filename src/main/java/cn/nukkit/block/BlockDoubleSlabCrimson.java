package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabCrimson extends BlockDoubleSlabWood {
    BlockDoubleSlabCrimson() {

    }

    @Override
    public int getId() {
        return CRIMSON_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Crimson Slab";
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
        return BlockColor.NETHER_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.CRIMSON_SLAB);
    }
}
