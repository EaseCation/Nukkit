package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcretePowderRed extends BlockConcretePowder {
    BlockConcretePowderRed() {

    }

    @Override
    public int getId() {
        return RED_CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "Red Concrete Powder";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }

    @Override
    protected int getConcreteBlockId() {
        return RED_CONCRETE;
    }
}
