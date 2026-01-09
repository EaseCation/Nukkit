package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockConcretePowderYellow extends BlockConcretePowder {
    BlockConcretePowderYellow() {

    }

    @Override
    public int getId() {
        return YELLOW_CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "Yellow Concrete Powder";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }

    @Override
    protected int getConcreteBlockId() {
        return YELLOW_CONCRETE;
    }
}
