package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcretePowderPink extends BlockConcretePowder {
    BlockConcretePowderPink() {

    }

    @Override
    public int getId() {
        return PINK_CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "Pink Concrete Powder";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }

    @Override
    protected int getConcreteBlockId() {
        return PINK_CONCRETE;
    }
}
