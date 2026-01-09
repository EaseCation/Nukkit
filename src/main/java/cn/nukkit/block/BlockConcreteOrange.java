package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcreteOrange extends BlockConcrete {
    BlockConcreteOrange() {

    }

    @Override
    public int getId() {
        return ORANGE_CONCRETE;
    }

    @Override
    public String getName() {
        return "Orange Concrete";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }
}
