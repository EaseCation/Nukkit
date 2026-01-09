package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWoodMangrove extends BlockLogMangrove {
    BlockWoodMangrove() {

    }

    @Override
    public int getId() {
        return MANGROVE_WOOD;
    }

    @Override
    public String getName() {
        return "Mangrove Wood";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    public boolean isWood() {
        return true;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_MANGROVE_WOOD, getDamage());
    }
}
