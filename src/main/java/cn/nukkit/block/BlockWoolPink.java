package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockWoolPink extends BlockWool {
    BlockWoolPink() {

    }

    @Override
    public String getName() {
        return "Pink Wool";
    }

    @Override
    public int getId() {
        return PINK_WOOL;
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}
