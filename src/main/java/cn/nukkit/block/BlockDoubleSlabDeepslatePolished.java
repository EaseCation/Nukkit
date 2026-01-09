package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabDeepslatePolished extends BlockDoubleSlabStone {
    BlockDoubleSlabDeepslatePolished() {

    }

    @Override
    public int getId() {
        return POLISHED_DEEPSLATE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Polished Deepslate Slab";
    }

    @Override
    public float getHardness() {
        return 3.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.POLISHED_DEEPSLATE_SLAB);
    }
}
