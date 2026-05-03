package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockDoubleSlabCinnabarBrick extends BlockDoubleSlabCinnabar {
    BlockDoubleSlabCinnabarBrick() {
    }

    @Override
    public int getId() {
        return CINNABAR_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Cinnabar Brick Slab";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.CINNABAR_BRICK_SLAB);
    }
}
