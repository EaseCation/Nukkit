package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockShelfCrimson extends BlockShelf {
    public BlockShelfCrimson() {
        this(0);
    }

    public BlockShelfCrimson(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CRIMSON_SHELF;
    }

    @Override
    public String getName() {
        return "Crimson Shelf";
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
        return BlockColor.CRIMSON_HYPHAE_BLOCK_COLOR;
    }
}
