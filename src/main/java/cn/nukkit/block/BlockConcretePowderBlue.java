package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcretePowderBlue extends BlockConcretePowder {
    BlockConcretePowderBlue() {

    }

    @Override
    public int getId() {
        return BLUE_CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "Blue Concrete Powder";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }

    @Override
    protected int getConcreteBlockId() {
        return BLUE_CONCRETE;
    }
}
