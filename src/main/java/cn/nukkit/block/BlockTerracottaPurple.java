package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockTerracottaPurple extends BlockTerracottaStained {
    BlockTerracottaPurple() {

    }

    @Override
    public int getId() {
        return PURPLE_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Purple Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }
}
