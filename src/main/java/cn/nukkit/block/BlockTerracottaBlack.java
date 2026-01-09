package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockTerracottaBlack extends BlockTerracottaStained {
    BlockTerracottaBlack() {

    }

    @Override
    public int getId() {
        return BLACK_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Black Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }
}
