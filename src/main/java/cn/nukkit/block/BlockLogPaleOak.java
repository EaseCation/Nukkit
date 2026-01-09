package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogPaleOak extends BlockLog {
    BlockLogPaleOak() {

    }

    @Override
    public int getId() {
        return PALE_OAK_LOG;
    }

    @Override
    public String getName() {
        return "Pale Oak Log";
    }

    @Override
    public BlockColor getColor() {
        return (getDamage() & PILLAR_AXIS_MASK) == 0 ? BlockColor.QUARTZ_BLOCK_COLOR : BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_PALE_OAK_LOG, getDamage());
    }
}
