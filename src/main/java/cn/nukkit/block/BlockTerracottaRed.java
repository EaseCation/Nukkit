package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockTerracottaRed extends BlockTerracottaStained {
    BlockTerracottaRed() {

    }

    @Override
    public int getId() {
        return RED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Red Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }
}
