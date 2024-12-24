package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcreteRed extends BlockConcrete {
    public BlockConcreteRed() {
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
    public BlockColor getColor() {
        return DyeColor.RED.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }
}
