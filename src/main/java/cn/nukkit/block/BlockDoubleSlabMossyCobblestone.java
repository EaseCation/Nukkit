package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabMossyCobblestone extends BlockDoubleSlabStone {
    BlockDoubleSlabMossyCobblestone() {

    }

    @Override
    public int getId() {
        return MOSSY_COBBLESTONE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Mossy Cobblestone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.MOSSY_COBBLESTONE_SLAB);
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_stone_slab2.mossy_cobblestone.name";
    }
}
