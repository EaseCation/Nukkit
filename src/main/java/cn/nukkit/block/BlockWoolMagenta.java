package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockWoolMagenta extends BlockWool {
    BlockWoolMagenta() {

    }

    @Override
    public String getName() {
        return "Magenta Wool";
    }

    @Override
    public int getId() {
        return MAGENTA_WOOL;
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}
