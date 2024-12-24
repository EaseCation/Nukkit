package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogDarkOak extends BlockLog {
    public BlockLogDarkOak() {
        this(0);
    }

    public BlockLogDarkOak(int meta) {
        super(meta);
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
