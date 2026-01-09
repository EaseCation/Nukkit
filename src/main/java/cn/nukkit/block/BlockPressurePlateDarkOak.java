package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPressurePlateDarkOak extends BlockPressurePlateWood {

    BlockPressurePlateDarkOak() {

    }

    @Override
    public int getId() {
        return DARK_OAK_PRESSURE_PLATE;
    }

    @Override
    public String getName() {
        return "Dark Oak Pressure Plate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }
}
