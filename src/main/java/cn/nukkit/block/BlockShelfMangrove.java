package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockShelfMangrove extends BlockShelf {
    BlockShelfMangrove() {

    }

    @Override
    public int getId() {
        return MANGROVE_SHELF;
    }

    @Override
    public String getName() {
        return "Mangrove Shelf";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
