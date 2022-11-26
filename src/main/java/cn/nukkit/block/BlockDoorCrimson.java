package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockDoorCrimson extends BlockDoorWood {
    public BlockDoorCrimson() {
        this(0);
    }

    public BlockDoorCrimson(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Crimson Door Block";
    }

    @Override
    public int getId() {
        return BLOCK_CRIMSON_DOOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.CRIMSON_DOOR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CRIMSON_STEM_BLOCK_COLOR;
    }
}
