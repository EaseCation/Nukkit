package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWoodJungle extends BlockLogJungle {
    BlockWoodJungle() {

    }

    @Override
    public int getId() {
        return JUNGLE_WOOD;
    }

    @Override
    public String getName() {
        return "Jungle Wood";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Override
    public boolean isWood() {
        return true;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_JUNGLE_WOOD, getDamage());
    }

    @Override
    public String getDescriptionId() {
        return "tile.wood.jungle.name";
    }
}
