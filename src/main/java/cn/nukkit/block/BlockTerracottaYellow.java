package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockTerracottaYellow extends BlockTerracottaStained {
    BlockTerracottaYellow() {

    }

    @Override
    public int getId() {
        return YELLOW_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Yellow Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }
}
