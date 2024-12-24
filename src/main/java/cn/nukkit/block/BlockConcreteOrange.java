package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcreteOrange extends BlockConcrete {
    public BlockConcreteOrange() {
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
    public BlockColor getColor() {
        return DyeColor.ORANGE.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }
}
