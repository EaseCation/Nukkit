package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorAcacia extends BlockTrapdoor {

    BlockTrapdoorAcacia() {

    }

    @Override
    public int getId() {
        return ACACIA_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Acacia Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
