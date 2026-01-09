package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcretePowderBrown extends BlockConcretePowder {
    BlockConcretePowderBrown() {

    }

    @Override
    public int getId() {
        return BROWN_CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "Brown Concrete Powder";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }

    @Override
    protected int getConcreteBlockId() {
        return BROWN_CONCRETE;
    }
}
