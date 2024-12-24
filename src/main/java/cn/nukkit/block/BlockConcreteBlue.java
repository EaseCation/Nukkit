package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcreteBlue extends BlockConcrete {
    public BlockConcreteBlue() {
    }

    @Override
    public int getId() {
        return BLUE_CONCRETE;
    }

    @Override
    public String getName() {
        return "Blue Concrete";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.BLUE.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }
}
