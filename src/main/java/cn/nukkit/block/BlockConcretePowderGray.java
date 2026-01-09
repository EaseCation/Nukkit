package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcretePowderGray extends BlockConcretePowder {
    BlockConcretePowderGray() {

    }

    @Override
    public int getId() {
        return GRAY_CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "Gray Concrete Powder";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }

    @Override
    protected int getConcreteBlockId() {
        return GRAY_CONCRETE;
    }
}
