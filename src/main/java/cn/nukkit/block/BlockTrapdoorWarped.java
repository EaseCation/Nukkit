package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorWarped extends BlockTrapdoor {
    BlockTrapdoorWarped() {

    }

    @Override
    public int getId() {
        return WARPED_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Warped Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    public int getFuelTime() {
        return 0;
    }
}
