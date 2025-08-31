package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockShelfPaleOak extends BlockShelf {
    public BlockShelfPaleOak() {
        this(0);
    }

    public BlockShelfPaleOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PALE_OAK_SHELF;
    }

    @Override
    public String getName() {
        return "Pale Oak Shelf";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }
}
