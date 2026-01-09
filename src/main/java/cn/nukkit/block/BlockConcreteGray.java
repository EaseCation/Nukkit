package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcreteGray extends BlockConcrete {
    BlockConcreteGray() {

    }

    @Override
    public int getId() {
        return GRAY_CONCRETE;
    }

    @Override
    public String getName() {
        return "Gray Concrete";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }
}
