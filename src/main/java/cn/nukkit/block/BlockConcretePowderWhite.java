package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcretePowderWhite extends BlockConcretePowder {
    BlockConcretePowderWhite() {

    }

    @Override
    public int getId() {
        return WHITE_CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "White Concrete Powder";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }

    @Override
    protected int getConcreteBlockId() {
        return WHITE_CONCRETE;
    }
}
