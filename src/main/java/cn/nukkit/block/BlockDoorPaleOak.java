package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockDoorPaleOak extends BlockDoorWood {
    public BlockDoorPaleOak() {
        this(0);
    }

    public BlockDoorPaleOak(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Pale Oak Door Block";
    }

    @Override
    public int getId() {
        return PALE_OAK_DOOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), getDefaultMeta());
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    public boolean isBlockItem() {
        return true;
    }
}
