package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWoodStrippedCherry extends BlockLogStrippedCherry {
    BlockWoodStrippedCherry() {

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

    @Override
    public boolean isWood() {
        return true;
    }
}
