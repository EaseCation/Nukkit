package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcretePowderBlack extends BlockConcretePowder {
    BlockConcretePowderBlack() {

    }

    @Override
    public int getId() {
        return BLACK_CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "Black Concrete Powder";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }

    @Override
    protected int getConcreteBlockId() {
        return BLACK_CONCRETE;
    }
}
