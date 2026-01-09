package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockWoolYellow extends BlockWool {
    BlockWoolYellow() {

    }

    @Override
    public String getName() {
        return "Yellow Wool";
    }

    @Override
    public int getId() {
        return YELLOW_WOOL;
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }
}
