package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabPolishedAndesite extends BlockDoubleSlabStone {
    BlockDoubleSlabPolishedAndesite() {

    }

    @Override
    public int getId() {
        return POLISHED_ANDESITE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Polished Andesite Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.POLISHED_ANDESITE_SLAB);
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_stone_slab3.andesite.smooth.name";
    }
}
