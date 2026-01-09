package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockTerracottaLime extends BlockTerracottaStained {
    BlockTerracottaLime() {

    }

    @Override
    public int getId() {
        return LIME_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Lime Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIME;
    }
}
