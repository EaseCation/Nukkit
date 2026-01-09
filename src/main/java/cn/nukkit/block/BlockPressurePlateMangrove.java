package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPressurePlateMangrove extends BlockPressurePlateWood {
    BlockPressurePlateMangrove() {

    }

    @Override
    public int getId() {
        return MANGROVE_PRESSURE_PLATE;
    }

    @Override
    public String getName() {
        return "Mangrove Pressure Plate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
