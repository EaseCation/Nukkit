package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockTerracottaCyan extends BlockTerracottaStained {
    BlockTerracottaCyan() {

    }

    @Override
    public int getId() {
        return CYAN_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Cyan Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }
}
