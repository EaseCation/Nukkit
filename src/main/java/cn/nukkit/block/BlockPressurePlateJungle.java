package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPressurePlateJungle extends BlockPressurePlateWood {

    BlockPressurePlateJungle() {

    }

    @Override
    public int getId() {
        return JUNGLE_PRESSURE_PLATE;
    }

    @Override
    public String getName() {
        return "Jungle Pressure Plate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
