package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcretePowderMagenta extends BlockConcretePowder {
    BlockConcretePowderMagenta() {

    }

    @Override
    public int getId() {
        return MAGENTA_CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "Magenta Concrete Powder";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }

    @Override
    protected int getConcreteBlockId() {
        return MAGENTA_CONCRETE;
    }
}
