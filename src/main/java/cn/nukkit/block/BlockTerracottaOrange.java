package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockTerracottaOrange extends BlockTerracottaStained {
    BlockTerracottaOrange() {

    }

    @Override
    public int getId() {
        return ORANGE_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Orange Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }
}
