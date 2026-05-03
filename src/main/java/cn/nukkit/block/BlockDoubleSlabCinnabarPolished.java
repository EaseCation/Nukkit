package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockDoubleSlabCinnabarPolished extends BlockDoubleSlabCinnabar {
    BlockDoubleSlabCinnabarPolished() {
    }

    @Override
    public int getId() {
        return POLISHED_CINNABAR_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Polished Cinnabar Slab";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.POLISHED_CINNABAR_SLAB);
    }
}
