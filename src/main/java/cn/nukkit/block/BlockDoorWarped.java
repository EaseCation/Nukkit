package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockDoorWarped extends BlockDoorWood {
    public BlockDoorWarped() {
        this(0);
    }

    public BlockDoorWarped(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Warped Door Block";
    }

    @Override
    public int getId() {
        return BLOCK_WARPED_DOOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.WARPED_DOOR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_STEM_BLOCK_COLOR;
    }

    @Override
    public int getFuelTime() {
        return 0;
    }
}
