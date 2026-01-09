package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcreteLightGray extends BlockConcrete {
    BlockConcreteLightGray() {

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
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }
}
