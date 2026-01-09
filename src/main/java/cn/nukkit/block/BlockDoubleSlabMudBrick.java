package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabMudBrick extends BlockDoubleSlabStone {
    BlockDoubleSlabMudBrick() {

    }

    @Override
    public int getId() {
        return MUD_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Mud Brick Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.MUD_BRICK_SLAB);
    }
}
