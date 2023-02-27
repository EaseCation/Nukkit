package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockDoorSpruce extends BlockDoorWood {

    public BlockDoorSpruce() {
        this(0);
    }

    public BlockDoorSpruce(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Spruce Door Block";
    }

    @Override
    public int getId() {
        return BLOCK_SPRUCE_DOOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.SPRUCE_DOOR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PODZOL_BLOCK_COLOR;
    }
}
