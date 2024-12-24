package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcreteBrown extends BlockConcrete {
    public BlockConcreteBrown() {
    }

    @Override
    public int getId() {
        return BROWN_CONCRETE;
    }

    @Override
    public String getName() {
        return "Brown Concrete";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.BROWN.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}
