package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockTerracottaWhite extends BlockTerracottaStained {
    BlockTerracottaWhite() {

    }

    @Override
    public int getId() {
        return WHITE_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "White Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
