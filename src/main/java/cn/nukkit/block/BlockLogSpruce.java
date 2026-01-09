package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogSpruce extends BlockLog {
    BlockLogSpruce() {

    }

    @Override
    public int getId() {
        return SPRUCE_LOG;
    }

    @Override
    public String getName() {
        return "Spruce Log";
    }

    @Override
    public BlockColor getColor() {
        return (getDamage() & PILLAR_AXIS_MASK) == 0 ? BlockColor.PODZOL_BLOCK_COLOR : BlockColor.BROWN_BLOCK_COLOR;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_SPRUCE_LOG, getDamage());
    }
}
