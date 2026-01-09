package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorMangrove extends BlockTrapdoor {
    BlockTrapdoorMangrove() {

    }

    @Override
    public int getId() {
        return MANGROVE_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Mangrove Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
