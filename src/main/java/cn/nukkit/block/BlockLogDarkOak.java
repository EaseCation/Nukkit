package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogDarkOak extends BlockLog {
    BlockLogDarkOak() {

    }

    @Override
    public int getId() {
        return DARK_OAK_LOG;
    }

    @Override
    public String getName() {
        return "Dark Oak Log";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_DARK_OAK_LOG, getDamage());
    }
}
