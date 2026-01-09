package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWoodPaleOak extends BlockLogPaleOak {
    BlockWoodPaleOak() {

    }

    @Override
    public int getId() {
        return PALE_OAK_WOOD;
    }

    @Override
    public String getName() {
        return "Pale Oak Wood";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public boolean isWood() {
        return true;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_PALE_OAK_WOOD, getDamage());
    }
}
