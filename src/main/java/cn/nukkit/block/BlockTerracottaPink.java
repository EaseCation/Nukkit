package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockTerracottaPink extends BlockTerracottaStained {
    BlockTerracottaPink() {

    }

    @Override
    public int getId() {
        return PINK_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Pink Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}
