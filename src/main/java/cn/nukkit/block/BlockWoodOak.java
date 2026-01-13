package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWoodOak extends BlockLogOak {
    BlockWoodOak() {

    }

    @Override
    public int getId() {
        return OAK_WOOD;
    }

    @Override
    public String getName() {
        return "Oak Wood";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public boolean isWood() {
        return true;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_OAK_WOOD, getDamage());
    }

    @Override
    public String getDescriptionId() {
        return "tile.wood.oak.name";
    }
}
