package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockShelfMangrove extends BlockShelf {
    public BlockShelfMangrove() {
        this(0);
    }

    public BlockShelfMangrove(int meta) {
        super(meta);
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
