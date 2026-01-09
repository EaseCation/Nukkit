package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcretePowderGreen extends BlockConcretePowder {
    BlockConcretePowderGreen() {

    }

    @Override
    public int getId() {
        return GREEN_CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "Green Concrete Powder";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }

    @Override
    protected int getConcreteBlockId() {
        return GREEN_CONCRETE;
    }
}
