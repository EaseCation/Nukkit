package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockTerracottaGray extends BlockTerracottaStained {
    BlockTerracottaGray() {

    }

    @Override
    public int getId() {
        return GRAY_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Gray Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }
}
