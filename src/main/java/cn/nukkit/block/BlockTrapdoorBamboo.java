package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorBamboo extends BlockTrapdoor {
    BlockTrapdoorBamboo() {

    }

    @Override
    public int getId() {
        return BAMBOO_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Bamboo Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
