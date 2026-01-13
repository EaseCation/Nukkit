package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedSilver extends BlockTerracottaGlazed {

    BlockTerracottaGlazedSilver() {

    }

    @Override
    public int getId() {
        return SILVER_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Light Gray Glazed Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }
}
