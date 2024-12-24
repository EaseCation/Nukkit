package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogStrippedCherry extends BlockLogStripped {
    public BlockLogStrippedCherry() {
        this(0);
    }

    public BlockLogStrippedCherry(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_CHERRY_LOG;
    }

    @Override
    public String getName() {
        return "Stripped Cherry Log";
    }

    @Override
    public BlockColor getColor() {
        return (getDamage() & PILLAR_AXIS_MASK) == 0 ? BlockColor.WHITE_TERRACOTA_BLOCK_COLOR : BlockColor.PINK_TERRACOTA_BLOCK_COLOR;
    }
}
