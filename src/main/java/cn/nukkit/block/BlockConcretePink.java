package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcretePink extends BlockConcrete {
    public BlockConcretePink() {
    }

    @Override
    public int getId() {
        return PINK_CONCRETE;
    }

    @Override
    public String getName() {
        return "Pink Concrete";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.PINK.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}
