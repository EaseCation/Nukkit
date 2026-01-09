package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcreteYellow extends BlockConcrete {
    BlockConcreteYellow() {

    }

    @Override
    public int getId() {
        return YELLOW_CONCRETE;
    }

    @Override
    public String getName() {
        return "Yellow Concrete";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }
}
