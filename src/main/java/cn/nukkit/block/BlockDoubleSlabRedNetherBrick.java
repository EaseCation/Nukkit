package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabRedNetherBrick extends BlockDoubleSlabStone {
    BlockDoubleSlabRedNetherBrick() {

    }

    @Override
    public int getId() {
        return RED_NETHER_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Red Nether Brick Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHER_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.RED_NETHER_BRICK_SLAB);
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_stone_slab2.red_nether_brick.name";
    }
}
