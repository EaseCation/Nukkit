package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcreteRed extends BlockConcrete {
    BlockConcreteRed() {

    }

    @Override
    public int getId() {
        return RED_CONCRETE;
    }

    @Override
    public String getName() {
        return "Red Concrete";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }
}
