package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcretePowderLightGray extends BlockConcretePowder {
    BlockConcretePowderLightGray() {

    }

    @Override
    public int getId() {
        return LIGHT_GRAY_CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "Light Gray Concrete Powder";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }

    @Override
    protected int getConcreteBlockId() {
        return LIGHT_GRAY_CONCRETE;
    }
}
