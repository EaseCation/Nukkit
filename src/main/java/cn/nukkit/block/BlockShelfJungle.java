package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockShelfJungle extends BlockShelf {
    BlockShelfJungle() {

    }

    @Override
    public int getId() {
        return JUNGLE_SHELF;
    }

    @Override
    public String getName() {
        return "Jungle Shelf";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
