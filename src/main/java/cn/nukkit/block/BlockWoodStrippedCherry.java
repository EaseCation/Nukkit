package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWoodStrippedCherry extends BlockLogStrippedCherry {
    public BlockWoodStrippedCherry() {
        this(0);
    }

    public BlockWoodStrippedCherry(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_CHERRY_WOOD;
    }

    @Override
    public String getName() {
        return "Stripped Cherry Wood";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PINK_TERRACOTA_BLOCK_COLOR;
    }
}
