package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabResinBrick extends BlockDoubleSlabStone {
    BlockDoubleSlabResinBrick() {

    }

    @Override
    public int getId() {
        return RESIN_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Resin Brick Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.RESIN_BRICK_SLAB);
    }
}
