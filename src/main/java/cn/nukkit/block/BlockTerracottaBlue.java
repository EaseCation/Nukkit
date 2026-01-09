package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockTerracottaBlue extends BlockTerracottaStained {
    BlockTerracottaBlue() {

    }

    @Override
    public int getId() {
        return BLUE_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Blue Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }
}
