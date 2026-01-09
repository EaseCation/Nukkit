package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWoodCherry extends BlockLogCherry {
    BlockWoodCherry() {

    }

    @Override
    public int getId() {
        return CHERRY_WOOD;
    }

    @Override
    public String getName() {
        return "Cherry Wood";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    public boolean isWood() {
        return true;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_CHERRY_WOOD, getDamage());
    }
}
