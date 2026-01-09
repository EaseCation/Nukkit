package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPressurePlateCherry extends BlockPressurePlateWood {
    BlockPressurePlateCherry() {

    }

    @Override
    public int getId() {
        return CHERRY_PRESSURE_PLATE;
    }

    @Override
    public String getName() {
        return "Cherry Pressure Plate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WHITE_TERRACOTA_BLOCK_COLOR;
    }
}
