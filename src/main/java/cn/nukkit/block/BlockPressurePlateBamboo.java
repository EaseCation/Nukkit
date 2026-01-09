package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPressurePlateBamboo extends BlockPressurePlateWood {
    BlockPressurePlateBamboo() {

    }

    @Override
    public int getId() {
        return BAMBOO_PRESSURE_PLATE;
    }

    @Override
    public String getName() {
        return "Bamboo Pressure Plate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
