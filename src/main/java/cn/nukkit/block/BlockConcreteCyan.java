package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockConcreteCyan extends BlockConcrete {
    BlockConcreteCyan() {

    }

    @Override
    public int getId() {
        return CYAN_CONCRETE;
    }

    @Override
    public String getName() {
        return "Cyan Concrete";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }
}
