package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcreteWhite extends BlockConcrete {
    public BlockConcreteWhite() {
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
    public BlockColor getColor() {
        return DyeColor.WHITE.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
