package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogMangrove extends BlockLog {
    public BlockLogMangrove() {
        this(0);
    }

    public BlockLogMangrove(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MANGROVE_LOG;
    }

    @Override
    public String getName() {
        return "Mangrove Log";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_MANGROVE_LOG, getDamage());
    }
}
