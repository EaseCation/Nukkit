package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockShelfAcacia extends BlockShelf {
    BlockShelfAcacia() {

    }

    @Override
    public int getId() {
        return ACACIA_SHELF;
    }

    @Override
    public String getName() {
        return "Acacia Shelf";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
