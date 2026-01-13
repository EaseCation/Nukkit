package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWoodBirch extends BlockLogBirch {
    BlockWoodBirch() {

    }

    @Override
    public int getId() {
        return BIRCH_WOOD;
    }

    @Override
    public String getName() {
        return "Birch Wood";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public boolean isWood() {
        return true;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_BIRCH_WOOD, getDamage());
    }

    @Override
    public String getDescriptionId() {
        return "tile.wood.birch.name";
    }
}
