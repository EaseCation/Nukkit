package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockWoolLightBlue extends BlockWool {
    BlockWoolLightBlue() {

    }

    @Override
    public String getName() {
        return "Light Blue Wool";
    }

    @Override
    public int getId() {
        return LIGHT_BLUE_WOOL;
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_BLUE;
    }
}
