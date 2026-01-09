package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorJungle extends BlockTrapdoor {

    BlockTrapdoorJungle() {

    }

    @Override
    public int getId() {
        return JUNGLE_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Jungle Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
