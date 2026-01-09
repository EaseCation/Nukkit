package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockWoolOrange extends BlockWool {
    BlockWoolOrange() {

    }

    @Override
    public String getName() {
        return "Orange Wool";
    }

    @Override
    public int getId() {
        return ORANGE_WOOL;
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }
}
