package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorPaleOak extends BlockTrapdoor {
    BlockTrapdoorPaleOak() {

    }

    @Override
    public int getId() {
        return PALE_OAK_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Pale Oak Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }
}
