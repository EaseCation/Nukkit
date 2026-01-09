package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockCarpetLightBlue extends BlockCarpet {
    BlockCarpetLightBlue() {

    }

    @Override
    public int getId() {
        return LIGHT_BLUE_CARPET;
    }

    @Override
    public String getName() {
        return "Light Blue Carpet";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.LIGHT_BLUE.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_BLUE;
    }
}
