package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockDoorMangrove extends BlockDoorWood {
    BlockDoorMangrove() {

    }

    @Override
    public String getName() {
        return "Mangrove Door Block";
    }

    @Override
    public int getId() {
        return MANGROVE_DOOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.MANGROVE_DOOR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
