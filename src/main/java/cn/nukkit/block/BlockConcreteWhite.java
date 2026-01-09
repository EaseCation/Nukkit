package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcreteWhite extends BlockConcrete {
    BlockConcreteWhite() {

    }

    @Override
    public int getId() {
        return WHITE_CONCRETE;
    }

    @Override
    public String getName() {
        return "White Concrete";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
