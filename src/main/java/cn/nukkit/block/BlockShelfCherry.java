package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockShelfCherry extends BlockShelf {
    public BlockShelfCherry() {
        this(0);
    }

    public BlockShelfCherry(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_SHELF;
    }

    @Override
    public String getName() {
        return "Cherry Shelf";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PINK_TERRACOTA_BLOCK_COLOR;
    }
}
