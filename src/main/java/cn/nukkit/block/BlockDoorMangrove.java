package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockDoorMangrove extends BlockDoorWood {
    public BlockDoorMangrove() {
        this(0);
    }

    public BlockDoorMangrove(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mangrove Door Block";
    }

    @Override
    public int getId() {
        return BLOCK_MANGROVE_DOOR;
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
