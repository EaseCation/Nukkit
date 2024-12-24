package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcreteLime extends BlockConcrete {
    public BlockConcreteLime() {
    }

    @Override
    public int getId() {
        return LIME_CONCRETE;
    }

    @Override
    public String getName() {
        return "Lime Concrete";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.LIME.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIME;
    }
}
