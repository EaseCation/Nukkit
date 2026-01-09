package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockTerracottaGreen extends BlockTerracottaStained {
    BlockTerracottaGreen() {

    }

    @Override
    public int getId() {
        return GREEN_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Green Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }
}
