package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabDeepslateTile extends BlockDoubleSlabStone {
    BlockDoubleSlabDeepslateTile() {

    }

    @Override
    public int getId() {
        return DEEPSLATE_TILE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Deepslate Tile Slab";
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
        return Item.get(ItemBlockID.DEEPSLATE_TILE_SLAB);
    }
}
