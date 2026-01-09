package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPressurePlateSpruce extends BlockPressurePlateWood {

    BlockPressurePlateSpruce() {

    }

    @Override
    public int getId() {
        return SPRUCE_PRESSURE_PLATE;
    }

    @Override
    public String getName() {
        return "Spruce Pressure Plate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PODZOL_BLOCK_COLOR;
    }
}
