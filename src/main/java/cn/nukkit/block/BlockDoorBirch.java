package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockDoorBirch extends BlockDoorWood {

    BlockDoorBirch() {

    }

    @Override
    public String getName() {
        return "Birch Door Block";
    }

    @Override
    public int getId() {
        return BIRCH_DOOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.BIRCH_DOOR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
