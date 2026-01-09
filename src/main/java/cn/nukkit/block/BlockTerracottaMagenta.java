package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockTerracottaMagenta extends BlockTerracottaStained {
    BlockTerracottaMagenta() {

    }

    @Override
    public int getId() {
        return MAGENTA_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Magenta Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}
