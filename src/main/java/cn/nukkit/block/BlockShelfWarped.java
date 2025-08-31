package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockShelfWarped extends BlockShelf {
    public BlockShelfWarped() {
        this(0);
    }

    public BlockShelfWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_SHELF;
    }

    @Override
    public String getName() {
        return "Warped Shelf";
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_HYPHAE_BLOCK_COLOR;
    }
}
