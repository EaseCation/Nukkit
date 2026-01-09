package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockWoolRed extends BlockWool {
    BlockWoolRed() {

    }

    @Override
    public String getName() {
        return "Red Wool";
    }

    @Override
    public int getId() {
        return RED_WOOL;
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }
}
