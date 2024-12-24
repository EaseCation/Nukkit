package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcreteLightBlue extends BlockConcrete {
    public BlockConcreteLightBlue() {
    }

    @Override
    public int getId() {
        return LIGHT_BLUE_CONCRETE;
    }

    @Override
    public String getName() {
        return "Light Blue Concrete";
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
