package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcretePowderCyan extends BlockConcretePowder {
    BlockConcretePowderCyan() {

    }

    @Override
    public int getId() {
        return CYAN_CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "Cyan Concrete Powder";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }

    @Override
    protected int getConcreteBlockId() {
        return CYAN_CONCRETE;
    }
}
