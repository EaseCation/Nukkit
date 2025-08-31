package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockShelfOak extends BlockShelf {
    public BlockShelfOak() {
        this(0);
    }

    public BlockShelfOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return OAK_SHELF;
    }

    @Override
    public String getName() {
        return "Oak Shelf";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
