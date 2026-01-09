package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockDoorJungle extends BlockDoorWood {

    BlockDoorJungle() {

    }

    @Override
    public String getName() {
        return "Jungle Door Block";
    }

    @Override
    public int getId() {
        return JUNGLE_DOOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.JUNGLE_DOOR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
