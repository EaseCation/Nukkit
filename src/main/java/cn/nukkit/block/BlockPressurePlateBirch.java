package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPressurePlateBirch extends BlockPressurePlateWood {

    BlockPressurePlateBirch() {

    }

    @Override
    public int getId() {
        return BIRCH_PRESSURE_PLATE;
    }

    @Override
    public String getName() {
        return "Birch Pressure Plate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
