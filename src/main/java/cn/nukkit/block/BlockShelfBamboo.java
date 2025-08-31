package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockShelfBamboo extends BlockShelf {
    public BlockShelfBamboo() {
        this(0);
    }

    public BlockShelfBamboo(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BAMBOO_SHELF;
    }

    @Override
    public String getName() {
        return "Bamboo Shelf";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
