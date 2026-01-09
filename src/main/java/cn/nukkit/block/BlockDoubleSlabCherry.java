package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabCherry extends BlockDoubleSlabWood {
    BlockDoubleSlabCherry() {

    }

    @Override
    public int getId() {
        return CHERRY_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Cherry Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WHITE_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.CHERRY_SLAB);
    }
}
