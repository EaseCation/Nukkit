package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockShelfAcacia extends BlockShelf {
    public BlockShelfAcacia() {
        this(0);
    }

    public BlockShelfAcacia(int meta) {
        super(meta);
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
