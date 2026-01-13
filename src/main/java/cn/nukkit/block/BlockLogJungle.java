package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogJungle extends BlockLog {
    BlockLogJungle() {

    }

    @Override
    public int getId() {
        return JUNGLE_LOG;
    }

    @Override
    public String getName() {
        return "Jungle Log";
    }

    @Override
    public BlockColor getColor() {
        return (getDamage() & PILLAR_AXIS_MASK) == 0 ? BlockColor.DIRT_BLOCK_COLOR : BlockColor.PODZOL_BLOCK_COLOR;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_JUNGLE_LOG, getDamage());
    }

    @Override
    public String getDescriptionId() {
        return "tile.log.jungle.name";
    }
}
