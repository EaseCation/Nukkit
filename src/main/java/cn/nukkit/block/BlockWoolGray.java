package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockWoolGray extends BlockWool {
    BlockWoolGray() {

    }

    @Override
    public String getName() {
        return "Gray Wool";
    }

    @Override
    public int getId() {
        return GRAY_WOOL;
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }
}
