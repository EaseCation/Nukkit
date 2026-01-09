package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorBirch extends BlockTrapdoor {

    BlockTrapdoorBirch() {

    }

    @Override
    public int getId() {
        return BIRCH_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Birch Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
