package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWoodDarkOak extends BlockLogDarkOak {
    BlockWoodDarkOak() {

    }

    @Override
    public int getId() {
        return DARK_OAK_WOOD;
    }

    @Override
    public String getName() {
        return "Dark Oak Wood";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }

    @Override
    public boolean isWood() {
        return true;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_DARK_OAK_WOOD, getDamage());
    }

    @Override
    public String getDescriptionId() {
        return "tile.wood.dark_oak.name";
    }
}
