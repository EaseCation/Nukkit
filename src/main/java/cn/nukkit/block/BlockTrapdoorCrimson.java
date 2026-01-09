package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorCrimson extends BlockTrapdoor {
    BlockTrapdoorCrimson() {

    }

    @Override
    public int getId() {
        return CRIMSON_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Crimson Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHER_BLOCK_COLOR;
    }

    @Override
    public int getFuelTime() {
        return 0;
    }
}
