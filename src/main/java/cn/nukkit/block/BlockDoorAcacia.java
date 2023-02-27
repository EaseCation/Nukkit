package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockDoorAcacia extends BlockDoorWood {

    public BlockDoorAcacia() {
        this(0);
    }

    public BlockDoorAcacia(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Acacia Door Block";
    }

    @Override
    public int getId() {
        return BLOCK_ACACIA_DOOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.ACACIA_DOOR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
