package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockDoorDarkOak extends BlockDoorWood {

    public BlockDoorDarkOak() {
        this(0);
    }

    public BlockDoorDarkOak(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Dark Oak Door Block";
    }

    @Override
    public int getId() {
        return BLOCK_DARK_OAK_DOOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.DARK_OAK_DOOR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }
}
