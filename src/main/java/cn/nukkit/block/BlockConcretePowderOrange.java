package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcretePowderOrange extends BlockConcretePowder {
    BlockConcretePowderOrange() {

    }

    @Override
    public int getId() {
        return ORANGE_CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "Orange Concrete Powder";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }

    @Override
    protected int getConcreteBlockId() {
        return ORANGE_CONCRETE;
    }
}
