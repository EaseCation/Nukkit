package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcreteMagenta extends BlockConcrete {
    BlockConcreteMagenta() {

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
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}
