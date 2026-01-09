package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcretePowderLime extends BlockConcretePowder {
    BlockConcretePowderLime() {

    }

    @Override
    public int getId() {
        return LIME_CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "Lime Concrete Powder";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIME;
    }

    @Override
    protected int getConcreteBlockId() {
        return LIME_CONCRETE;
    }
}
