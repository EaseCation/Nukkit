package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcretePowderLightBlue extends BlockConcretePowder {
    BlockConcretePowderLightBlue() {

    }

    @Override
    public int getId() {
        return LIGHT_BLUE_CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "Light Blue Concrete Powder";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_BLUE;
    }

    @Override
    protected int getConcreteBlockId() {
        return LIGHT_BLUE_CONCRETE;
    }
}
