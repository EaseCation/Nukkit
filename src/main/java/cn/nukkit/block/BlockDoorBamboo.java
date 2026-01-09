package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockDoorBamboo extends BlockDoorWood {
    BlockDoorBamboo() {

    }

    @Override
    public String getName() {
        return "Bamboo Door Block";
    }

    @Override
    public int getId() {
        return BAMBOO_DOOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), getItemMeta());
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    public boolean isBlockItem() {
        return true;
    }
}
