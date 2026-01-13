package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedMagenta extends BlockTerracottaGlazed {

    BlockTerracottaGlazedMagenta() {

    }

    @Override
    public int getId() {
        return MAGENTA_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Magenta Glazed Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}
