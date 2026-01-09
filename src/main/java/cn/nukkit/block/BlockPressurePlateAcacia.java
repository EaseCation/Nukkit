package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPressurePlateAcacia extends BlockPressurePlateWood {

    BlockPressurePlateAcacia() {

    }

    @Override
    public int getId() {
        return ACACIA_PRESSURE_PLATE;
    }

    @Override
    public String getName() {
        return "Acacia Pressure Plate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
