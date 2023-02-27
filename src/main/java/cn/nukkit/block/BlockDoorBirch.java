package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockDoorBirch extends BlockDoorWood {

    public BlockDoorBirch() {
        this(0);
    }

    public BlockDoorBirch(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Birch Door Block";
    }

    @Override
    public int getId() {
        return BLOCK_BIRCH_DOOR;
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
