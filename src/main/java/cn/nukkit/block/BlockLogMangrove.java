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
        return (getDamage() & PILLAR_AXIS_MASK) == 0 ? BlockColor.RED_BLOCK_COLOR : BlockColor.PODZOL_BLOCK_COLOR;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_MANGROVE_LOG, getDamage());
    }
}
