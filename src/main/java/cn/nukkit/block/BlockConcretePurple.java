package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcretePurple extends BlockConcrete {
    BlockConcretePurple() {

    }

    @Override
    public int getId() {
        return PURPLE_CONCRETE;
    }

    @Override
    public String getName() {
        return "Purple Concrete";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }
}
