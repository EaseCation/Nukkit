package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockTerracottaLightBlue extends BlockTerracottaStained {
    BlockTerracottaLightBlue() {

    }

    @Override
    public int getId() {
        return LIGHT_BLUE_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Light Blue Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_BLUE;
    }
}
