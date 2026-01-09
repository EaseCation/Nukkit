package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWoodAcacia extends BlockLogAcacia {
    BlockWoodAcacia() {

    }

    @Override
    public int getId() {
        return ACACIA_WOOD;
    }

    @Override
    public String getName() {
        return "Acacia Wood";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }

    @Override
    public boolean isWood() {
        return true;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_ACACIA_WOOD, getDamage());
    }
}
