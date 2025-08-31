package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockShelfSpruce extends BlockShelf {
    public BlockShelfSpruce() {
        this(0);
    }

    public BlockShelfSpruce(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SPRUCE_SHELF;
    }

    @Override
    public String getName() {
        return "Spruce Shelf";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PODZOL_BLOCK_COLOR;
    }
}
