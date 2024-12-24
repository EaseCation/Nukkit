package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogCherry extends BlockLog {
    public BlockLogCherry() {
        this(0);
    }

    public BlockLogCherry(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_LOG;
    }

    @Override
    public String getName() {
        return "Cherry Log";
    }

    @Override
    public BlockColor getColor() {
        return (getDamage() & PILLAR_AXIS_MASK) == 0 ? BlockColor.WHITE_TERRACOTA_BLOCK_COLOR : BlockColor.GRAY_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_CHERRY_LOG, getDamage());
    }
}
