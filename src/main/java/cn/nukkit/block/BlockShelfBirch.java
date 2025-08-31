package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockShelfBirch extends BlockShelf {
    public BlockShelfBirch() {
        this(0);
    }

    public BlockShelfBirch(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BIRCH_SHELF;
    }

    @Override
    public String getName() {
        return "Birch Shelf";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
