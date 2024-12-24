package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcreteGreen extends BlockConcrete {
    public BlockConcreteGreen() {
    }

    @Override
    public int getId() {
        return GREEN_CONCRETE;
    }

    @Override
    public String getName() {
        return "Green Concrete";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.GREEN.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }
}
