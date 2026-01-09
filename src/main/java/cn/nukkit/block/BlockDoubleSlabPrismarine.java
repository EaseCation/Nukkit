package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabPrismarine extends BlockDoubleSlabStone {
    BlockDoubleSlabPrismarine() {

    }

    @Override
    public int getId() {
        return PRISMARINE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Prismarine Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.PRISMARINE_SLAB);
    }
}
