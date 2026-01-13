package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabDarkPrismarine extends BlockDoubleSlabStone {
    BlockDoubleSlabDarkPrismarine() {

    }

    @Override
    public int getId() {
        return DARK_PRISMARINE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Dark Prismarine Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIAMOND_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.DARK_PRISMARINE_SLAB);
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_stone_slab2.prismarine.dark.name";
    }
}
