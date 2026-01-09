package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockWoolPurple extends BlockWool {
    BlockWoolPurple() {

    }

    @Override
    public String getName() {
        return "Purple Wool";
    }

    @Override
    public int getId() {
        return PURPLE_WOOL;
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }
}
