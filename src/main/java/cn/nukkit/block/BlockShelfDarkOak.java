package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockShelfDarkOak extends BlockShelf {
    BlockShelfDarkOak() {

    }

    @Override
    public int getId() {
        return DARK_OAK_SHELF;
    }

    @Override
    public String getName() {
        return "Dark Oak Shelf";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }
}
