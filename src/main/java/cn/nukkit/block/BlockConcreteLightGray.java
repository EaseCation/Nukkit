package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcreteLightGray extends BlockConcrete {
    public BlockConcreteLightGray() {
    }

    @Override
    public int getId() {
        return LIGHT_GRAY_CONCRETE;
    }

    @Override
    public String getName() {
        return "Light Gray Concrete";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.LIGHT_GRAY.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }
}
