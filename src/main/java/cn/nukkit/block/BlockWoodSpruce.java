package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWoodSpruce extends BlockLogSpruce {
    BlockWoodSpruce() {

    }

    @Override
    public int getId() {
        return SPRUCE_WOOD;
    }

    @Override
    public String getName() {
        return "Spruce Wood";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PODZOL_BLOCK_COLOR;
    }

    @Override
    public boolean isWood() {
        return true;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_SPRUCE_WOOD, getDamage());
    }

    @Override
    public String getDescriptionId() {
        return "tile.wood.spruce.name";
    }
}
