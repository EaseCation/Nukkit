package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcreteBlack extends BlockConcrete {
    public BlockConcreteBlack() {
    }

    @Override
    public int getId() {
        return BLACK_CONCRETE;
    }

    @Override
    public String getName() {
        return "Black Concrete";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.BLACK.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }
}
