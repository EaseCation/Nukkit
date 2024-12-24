package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogAcacia extends BlockLog {
    public BlockLogAcacia() {
        this(0);
    }

    public BlockLogAcacia(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ACACIA_LOG;
    }

    @Override
    public String getName() {
        return "Acacia Log";
    }

    @Override
    public BlockColor getColor() {
        return (getDamage() & PILLAR_AXIS_MASK) == 0 ? BlockColor.ORANGE_BLOCK_COLOR : BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_ACACIA_LOG, getDamage());
    }
}
