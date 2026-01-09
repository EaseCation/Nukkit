package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockWoolBrown extends BlockWool {
    BlockWoolBrown() {

    }

    @Override
    public String getName() {
        return "Brown Wool";
    }

    @Override
    public int getId() {
        return BROWN_WOOL;
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}
