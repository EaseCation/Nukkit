package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockTerracottaLightGray extends BlockTerracottaStained {
    BlockTerracottaLightGray() {

    }

    @Override
    public int getId() {
        return LIGHT_GRAY_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Light Gray Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }
}
