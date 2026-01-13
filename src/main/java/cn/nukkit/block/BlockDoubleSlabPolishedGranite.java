package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabPolishedGranite extends BlockDoubleSlabStone {
    BlockDoubleSlabPolishedGranite() {

    }

    @Override
    public int getId() {
        return POLISHED_GRANITE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Polished Granite Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.POLISHED_GRANITE_SLAB);
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_stone_slab3.granite.smooth.name";
    }
}
