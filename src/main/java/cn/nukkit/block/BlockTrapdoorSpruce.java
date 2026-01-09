package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorSpruce extends BlockTrapdoor {

    BlockTrapdoorSpruce() {

    }

    @Override
    public int getId() {
        return SPRUCE_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Spruce Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PODZOL_BLOCK_COLOR;
    }
}
