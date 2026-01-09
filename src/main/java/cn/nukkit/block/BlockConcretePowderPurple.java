package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcretePowderPurple extends BlockConcretePowder {
    BlockConcretePowderPurple() {

    }

    @Override
    public int getId() {
        return PURPLE_CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "Purple Concrete Powder";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }

    @Override
    protected int getConcreteBlockId() {
        return PURPLE_CONCRETE;
    }
}
