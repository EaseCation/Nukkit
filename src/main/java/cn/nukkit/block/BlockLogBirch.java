package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogBirch extends BlockLog {
    BlockLogBirch() {

    }

    @Override
    public int getId() {
        return BIRCH_LOG;
    }

    @Override
    public String getName() {
        return "Birch Log";
    }

    @Override
    public BlockColor getColor() {
        return (getDamage() & PILLAR_AXIS_MASK) == 0 ? BlockColor.SAND_BLOCK_COLOR : BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_BIRCH_LOG, getDamage());
    }

    @Override
    public String getDescriptionId() {
        return "tile.log.birch.name";
    }
}
