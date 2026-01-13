package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabPolishedDiorite extends BlockDoubleSlabStone {
    BlockDoubleSlabPolishedDiorite() {

    }

    @Override
    public int getId() {
        return POLISHED_DIORITE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Polished Diorite Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.POLISHED_DIORITE_SLAB);
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_stone_slab3.diorite.smooth.name";
    }
}
