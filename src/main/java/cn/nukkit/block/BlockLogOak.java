package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogOak extends BlockLog {
    public BlockLogOak() {
        this(0);
    }

    public BlockLogOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return OAK_LOG;
    }

    @Override
    public String getName() {
        return "Oak Log";
    }

    @Override
    public BlockColor getColor() {
        return (getDamage() & PILLAR_AXIS_MASK) == 0 ? BlockColor.WOOD_BLOCK_COLOR : BlockColor.PODZOL_BLOCK_COLOR;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_OAK_LOG, getDamage());
    }
}
