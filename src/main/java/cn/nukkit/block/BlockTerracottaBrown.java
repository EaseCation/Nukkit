package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockTerracottaBrown extends BlockTerracottaStained {
    BlockTerracottaBrown() {

    }

    @Override
    public int getId() {
        return BROWN_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Brown Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}
