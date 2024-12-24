package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcreteMagenta extends BlockConcrete {
    public BlockConcreteMagenta() {
    }

    @Override
    public int getId() {
        return MAGENTA_CONCRETE;
    }

    @Override
    public String getName() {
        return "Magenta Concrete";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.MAGENTA.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}
