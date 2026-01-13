package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedCyan extends BlockTerracottaGlazed {

    BlockTerracottaGlazedCyan() {

    }

    @Override
    public int getId() {
        return CYAN_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Cyan Glazed Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }
}
